package com.codechef.ffds

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Profile::class, Conversation::class, Chat::class], version = 1)
@TypeConverters(StringArrayConverter::class, ByteArrayConverter::class, MapConverter::class, DateTypeConverter::class, ImageTypeConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private lateinit var instance: UserDatabase

        @Synchronized
        fun getInstance(context: Context): UserDatabase {

                instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java, "user_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            return instance
        }
    }

}