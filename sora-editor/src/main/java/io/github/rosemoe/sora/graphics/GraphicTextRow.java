/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2022  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.graphics;

import static io.github.rosemoe.sora.lang.styling.TextStyle.isBold;
import static io.github.rosemoe.sora.lang.styling.TextStyle.isItalics;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.text.ContentLine;

import java.util.List;

/** Manages graphical(actually measuring) operations of a text row */
public class GraphicTextRow {

    private static final GraphicTextRow[] sCached = new GraphicTextRow[5];
    private final float[] mBuffer;
    private Paint mPaint;
    private ContentLine mText;
    private int mStart;
    private int mEnd;
    private int mTabWidth;
    private List<Span> mSpans;
    private boolean mCache = true;
    private List<Integer> mSoftBreaks;

    private GraphicTextRow() {
        mBuffer = new float[2];
    }

    public static GraphicTextRow obtain() {
        GraphicTextRow st;
        synchronized (sCached) {
            for (int i = sCached.length; --i >= 0; ) {
                if (sCached[i] != null) {
                    st = sCached[i];
                    sCached[i] = null;
                    return st;
                }
            }
        }
        st = new GraphicTextRow();
        return st;
    }

    public static void recycle(GraphicTextRow st) {
        st.mText = null;
        st.mSpans = null;
        st.mPaint = null;
        st.mStart = st.mEnd = st.mTabWidth = 0;
        st.mCache = true;
        st.mSoftBreaks = null;
        synchronized (sCached) {
            for (int i = 0; i < sCached.length; ++i) {
                if (sCached[i] == null) {
                    sCached[i] = st;
                    break;
                }
            }
        }
    }

    /** Reset */
    public void set(
            @NonNull ContentLine line,
            int start,
            int end,
            int tabWidth,
            @Nullable List<Span> spans,
            @NonNull Paint paint) {
        mPaint = paint;
        mText = line;
        mTabWidth = tabWidth;
        mStart = start;
        mEnd = end;
        mSpans = spans;
    }

    public void setSoftBreaks(@Nullable List<Integer> softBreaks) {
        mSoftBreaks = softBreaks;
    }

    public void disableCache() {
        mCache = false;
    }

    /** Build measure cache for the text */
    public void buildMeasureCache() {
        if (mText.widthCache == null || mText.widthCache.length < mEnd + 4) {
            mText.widthCache = new float[Math.max(90, mText.length() + 16)];
        }
        measureTextInternal(mStart, mEnd, mText.widthCache);
        // Generate prefix sum
        var cache = mText.widthCache;
        var pending = cache[0];
        cache[0] = 0f;
        for (int i = 1; i <= mEnd; i++) {
            var tmp = cache[i];
            cache[i] = cache[i - 1] + pending;
            pending = tmp;
        }
    }

    /**
     * From {@code start} to measure characters, until measured width add next char's width is
     * bigger than {@code advance}.
     *
     * <p>Note that the result array should not be stored.
     *
     * @return Element 0 is offset, Element 1 is measured width
     */
    public float[] findOffsetByAdvance(int start, float advance) {
        if (mText.widthCache != null && mCache) {
            var cache = mText.widthCache;
            var end = mEnd;
            int left = start, right = end;
            var base = cache[start];
            while (left <= right) {
                var mid = (left + right) / 2;
                if (mid < start || mid >= end) {
                    left = mid;
                    break;
                }
                var value = cache[mid] - base;
                if (value > advance) {
                    right = mid - 1;
                } else if (value < advance) {
                    left = mid + 1;
                } else {
                    left = mid;
                    break;
                }
            }
            if (cache[left] - base > advance) {
                left--;
            }
            left = Math.max(start, Math.min(end, left));
            mBuffer[0] = left;
            mBuffer[1] = cache[left] - base;
            return mBuffer;
        }
        var mRegionItr = new TextRegionIterator();
        mRegionItr.set(mEnd, mSpans, mSoftBreaks);
        mRegionItr.requireStartOffset(start);
        float currentPosition = 0f;
        // Find in each region
        var lastStyle = 0L;
        var chars = mText.value;
        float tabAdvance = mPaint.getSpaceWidth() * mTabWidth;
        int offset = start;
        while (mRegionItr.hasNextRegion() && currentPosition < advance) {
            mRegionItr.nextRegion();
            var regionStart = mRegionItr.getStartIndex();
            var regionEnd = mRegionItr.getEndIndex();
            regionEnd = Math.min(mEnd, regionEnd);
            var style = mRegionItr.getSpan().getStyleBits();
            if (style != lastStyle) {
                if (isBold(style) != isBold(lastStyle)) {
                    mPaint.setFakeBoldText(isBold(style));
                }
                if (isItalics(style) != isItalics(lastStyle)) {
                    mPaint.setTextSkewX(isItalics(style) ? GraphicsConstants.TEXT_SKEW_X : 0f);
                }
                lastStyle = style;
            }

            // Find in subregion
            int res = -1;
            {
                int lastStart = regionStart;
                for (int i = regionStart; i < regionEnd; i++) {
                    if (chars[i] == '\t') {
                        // Here is a tab
                        // Try to find advance
                        if (lastStart != i) {
                            int idx =
                                    mPaint.findOffsetByRunAdvance(
                                            mText, lastStart, i, advance - currentPosition, mCache);
                            currentPosition +=
                                    mPaint.measureTextRunAdvance(
                                            chars, lastStart, idx, regionStart, regionEnd);
                            if (idx < i) {
                                res = idx;
                                break;
                            } else {
                                if (currentPosition + tabAdvance > advance) {
                                    res = i;
                                    break;
                                } else {
                                    currentPosition += tabAdvance;
                                }
                            }
                        } else {
                            if (currentPosition + tabAdvance > advance) {
                                res = i;
                                break;
                            } else {
                                currentPosition += tabAdvance;
                            }
                        }
                        lastStart = i + 1;
                    }
                }
                if (res == -1) {
                    int idx =
                            mPaint.findOffsetByRunAdvance(
                                    mText, lastStart, regionEnd, advance - currentPosition, mCache);
                    currentPosition += measureText(lastStart, idx);
                    res = idx;
                }
            }

            offset = res;
            if (res < regionEnd) {
                break;
            }

            if (regionEnd == mEnd) {
                break;
            }
        }
        if (lastStyle != 0L) {
            mPaint.setFakeBoldText(false);
            mPaint.setTextSkewX(0f);
        }
        if (currentPosition > advance && offset > start) {
            offset--;
            currentPosition -= measureText(offset, offset + 1);
        }
        mBuffer[0] = offset;
        mBuffer[1] = currentPosition;
        return mBuffer;
    }

    public float measureText(int start, int end) {
        if (start >= end) {
            if (start != end) Log.w("GraphicTextRow", "start > end");
            return 0f;
        }
        if (mText.widthCache != null && mCache) {
            var cache = mText.widthCache;
            return cache[end] - cache[start];
        }
        return measureTextInternal(start, end, null);
    }

    private float measureTextInternal(int start, int end, float[] widths) {
        // Backup values
        final var originalBold = mPaint.isFakeBoldText();
        final var originalSkew = mPaint.getTextSkewX();

        start = Math.max(start, mStart);
        end = Math.min(end, mEnd);
        var mRegionItr = new TextRegionIterator();
        mRegionItr.set(end, mSpans, mSoftBreaks);
        mRegionItr.requireStartOffset(start);
        float width = 0f;
        // Measure for each region
        var lastStyle = 0L;
        while (mRegionItr.hasNextRegion()) {
            mRegionItr.nextRegion();
            var regionStart = mRegionItr.getStartIndex();
            var regionEnd = mRegionItr.getEndIndex();
            regionEnd = Math.min(end, regionEnd);
            var style = mRegionItr.getSpan().getStyleBits();
            if (style != lastStyle) {
                if (isBold(style) != isBold(lastStyle)) {
                    mPaint.setFakeBoldText(isBold(style));
                }
                if (isItalics(style) != isItalics(lastStyle)) {
                    mPaint.setTextSkewX(isItalics(style) ? GraphicsConstants.TEXT_SKEW_X : 0f);
                }
                lastStyle = style;
            }
            width += measureTextInner(regionStart, regionEnd, widths);
            if (regionEnd >= end) {
                break;
            }
        }
        mPaint.setFakeBoldText(originalBold);
        mPaint.setTextSkewX(originalSkew);
        mRegionItr.reset();
        return width;
    }

    @SuppressLint("NewApi")
    private float measureTextInner(int start, int end, float[] widths) {
        if (start >= end) {
            return 0f;
        }
        // Can be called directly
        float width =
                mPaint.getTextRunAdvances(
                        mText.value,
                        start,
                        end - start,
                        start,
                        end - start,
                        false,
                        widths,
                        widths == null ? 0 : start);
        float tabWidth = mPaint.getSpaceWidth() * mTabWidth;
        int tabCount = 0;
        for (int i = start; i < end; i++) {
            if (mText.charAt(i) == '\t') {
                tabCount++;
                if (widths != null) {
                    widths[i] = tabWidth;
                }
            }
        }
        float extraWidth = tabCount == 0 ? 0 : tabWidth - mPaint.measureText("\t");
        return width + extraWidth * tabCount;
    }
}
