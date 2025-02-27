package org.cosmic.ide.common

import com.google.gson.Gson

import org.cosmic.ide.common.util.FileUtil
import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.List
import java.util.stream.Collectors

class Indexer {

    private lateinit var json: JSONObject

    private var filePath: String

    @Throws(JSONException::class)
    constructor(projectName: String, projectCacheDirPath: String) {
        filePath = projectCacheDirPath + projectName + ".json"
        load()
    }

    @Throws(JSONException::class)
    fun load() {
        val indexFile = File(filePath)
        try {
            if (!indexFile.exists()) {
                FileUtil.writeFile(filePath, "{}")
            }
            val index = FileUtil.readFile(indexFile)
            json = JSONObject(index)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // @Throws(JSONException::class)
    // fun put(key: String, items: List<File>): Indexer {
        // var value = Gson().toJson(items.stream()
                        // .map(File::getAbsolutePath)
                        // .collect(Collectors.toList()))
        // json.put(key, value)
        // return this
    // }

    @Throws(JSONException::class)
    fun put(key: String, value: String): Indexer {
        json.put(key, value)
        return this
    }

    @Throws(JSONException::class)
    fun put(key: String, value: Long): Indexer {
        json.put(key, value)
        return this
    }

    @Throws(JSONException::class)
    fun notHas(key: String): Boolean {
        return !json.has(key)
    }

    @Throws(JSONException::class)
    fun getString(key: String): String {
        return json.getString(key)
    }

    fun getLong(key: String): Long {
        try {
            return json.getLong(key)
        } catch (e: JSONException) {
            return 0
        }
    }

    @Throws(JSONException::class)
    fun asString(): String {
        return json.toString(4)
    }

    fun flush() {
        try {
            FileUtil.writeFile(filePath, asString())
        } catch (e: Throwable) {
        }
    }
}
