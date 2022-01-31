package com.codechef.ffds

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ImageTypeConverter {
    @TypeConverter
    fun fromImageObject(image: Image): String {
        val gson = Gson()
        val type: Type = object : TypeToken<Image>() {}.type
        return gson.toJson(image, type)
    }

    @TypeConverter
    fun toImageObject(string: String?): Image? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<Image>() {}.type
        return gson.fromJson<Image>(string, type)
    }
}