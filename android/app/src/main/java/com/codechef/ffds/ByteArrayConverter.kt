package com.codechef.ffds

import androidx.room.TypeConverter

class ByteArrayConverter {

    @TypeConverter
    fun fromByte(list: List<Byte>): ByteArray {
        return list.toByteArray()
    }

    @TypeConverter
    fun toByte(byteArray: ByteArray): List<Byte> {
        return byteArray.toList()
    }

}