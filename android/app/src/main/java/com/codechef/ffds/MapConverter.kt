package com.codechef.ffds

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MapConverter {

    @TypeConverter
    fun fromMap(tableMap: ArrayList<ArrayList<HashMap<String, Any>>>): String {
        val gson = Gson()
        val type: Type =
            object : TypeToken<ArrayList<ArrayList<HashMap<String, Any>>>>() {}.type
        return gson.toJson(tableMap, type)
    }

    @TypeConverter
    fun toMap(tableMapString: String?): ArrayList<ArrayList<HashMap<String, Any>>>? {
        if (tableMapString == null) {
            return null
        }
        val gson = Gson()
        val type: Type =
            object : TypeToken<ArrayList<ArrayList<HashMap<String, Any>>>?>() {}.type
        return gson.fromJson<ArrayList<ArrayList<HashMap<String, Any>>>>(tableMapString, type)
    }

}