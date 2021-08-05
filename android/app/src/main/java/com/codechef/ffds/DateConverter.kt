package com.codechef.ffds

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class DateConverter {

    @TypeConverter
    fun fromDate(date: Date): String {
        val gson = Gson()
        val type: Type = object : TypeToken<Date>() {}.type
        return gson.toJson(date, type)
    }

    @TypeConverter
    fun toDate(string: String?): Date? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<Date?>() {}.type
        return gson.fromJson<Date>(string, type)
    }

}