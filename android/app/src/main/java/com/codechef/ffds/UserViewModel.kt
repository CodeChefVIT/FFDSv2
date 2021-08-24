package com.codechef.ffds

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CompletableJob
import java.util.concurrent.CompletableFuture

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = UserRepository(application)

    fun insertUser(vararg user: Profile) = repository.insertUser(*user)

    fun updateUser(user: Profile) = repository.updateUser(user)

    fun getUserData(_id: String): LiveData<Profile> = repository.getUserData(_id)

    fun insertAllConversations(vararg conversation: Conversation) = repository.insertAllConversations(*conversation)

    fun getAllConversations(): LiveData<List<Conversation>> = repository.getAllConversations()

    fun insertAllMessages(vararg chat: Chat) = repository.insertAllMessages(*chat)

    fun getAllMessages(conversationId: String): LiveData<List<Chat>> = repository.getAllMessages(conversationId)

    fun getLastMessage(conversationId: String): LiveData<Chat> = repository.getLastMessage(conversationId)

    fun clear() = repository.clear()
}