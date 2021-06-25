package com.codechef.ffds

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert
    fun insert(user: Profile)

    @Update
    fun update(user: Profile)

    @Query("SELECT * FROM Profile WHERE primaryKey = 0")
    fun getUserData() : LiveData<Profile>

    @Query("DELETE FROM Profile")
    fun clear()
}