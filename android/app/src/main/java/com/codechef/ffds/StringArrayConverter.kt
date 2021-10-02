package com.codechef.ffds

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StringArrayConverter {
    @TypeConverter
    fun fromStringList(list: ArrayList<String>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun toStringList(string: String?): ArrayList<String>? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson<ArrayList<String>>(string, type)
    }
}