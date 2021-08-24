package com.codechef.ffds

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(vararg user: Profile)

    @Update
    fun updateUser(user: Profile)

    @Query("SELECT * FROM Profile WHERE _id = :id")
    fun getUserData(id: String) : LiveData<Profile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllConversations(vararg conversation: Conversation)

    @Query("SELECT * FROM Conversation ORDER BY createdAt")
    fun getAllConversations() : LiveData<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMessages(vararg chat: Chat)

    @Query("SELECT * FROM Chat WHERE conversationId = :conversationId ORDER BY createdAt")
    fun getAllMessages(conversationId: String): LiveData<List<Chat>>

    @Query("SELECT * FROM Chat WHERE conversationId = :conversationId ORDER BY createdAt DESC LIMIT 1")
    fun getLastMessage(conversationId: String): LiveData<Chat>

    @Query("DELETE FROM Profile")
    fun clear()

}