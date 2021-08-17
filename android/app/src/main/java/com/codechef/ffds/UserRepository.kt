package com.codechef.ffds

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.Executors

class UserRepository(application: Application) {

    private var userDatabase: UserDatabase = UserDatabase.getInstance(application)
    private var userDao: UserDao = userDatabase.userDao()

    private val executorService = Executors.newSingleThreadExecutor()

    fun insertUser(vararg user: Profile) = executorService.execute { userDao.insertUser(*user) }

    fun updateUser(user: Profile) = executorService.execute { userDao.updateUser(user) }

    fun getUserData(_id: String) : LiveData<Profile> = userDao.getUserData(_id)

    fun insertAllConversations(vararg conversation: Conversation) = executorService.execute { userDao.insertAllConversations(*conversation) }

    fun getAllConversations(): LiveData<List<Conversation>> = userDao.getAllConversations()

    fun insertAllMessages(vararg chat: Chat) = executorService.execute { userDao.insertAllMessages(*chat) }

    fun getAllMessages(conversationId: String): LiveData<List<Chat>> = userDao.getAllMessages(conversationId)

    fun getLastMessage(conversationId: String): LiveData<Chat> = userDao.getLastMessage(conversationId)

    fun clear() = executorService.execute { userDao.clear() }
}