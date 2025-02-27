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
package io.github.rosemoe.sora.event;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.widget.CodeEditor;

/**
 * Long press event.
 *
 * <p>This event can be intercepted so that the editor framework will do nothing (such as selecting
 * a word). You can take over the procedure. Note that after intercepting an event, it will not be
 * sent to other listeners, either.
 *
 * @author Rosemoe
 */
public class LongPressEvent extends Event {

    private final CharPosition pos;
    private final MotionEvent event;

    public LongPressEvent(@NonNull CodeEditor editor, CharPosition position, MotionEvent event) {
        super(editor);
        this.pos = position;
        this.event = event;
    }

    @Override
    public boolean canIntercept() {
        return true;
    }

    public int getLine() {
        return pos.line;
    }

    public int getColumn() {
        return pos.column;
    }

    public int getIndex() {
        return pos.index;
    }

    public float getX() {
        return event.getX();
    }

    public float getY() {
        return event.getY();
    }

    /** Get original event object from Android framework */
    public MotionEvent getCausingEvent() {
        return event;
    }
}
