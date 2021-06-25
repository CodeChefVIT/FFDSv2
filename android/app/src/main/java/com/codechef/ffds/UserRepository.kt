package com.codechef.ffds

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class UserRepository(application: Application) {

    private var userDatabase: UserDatabase = UserDatabase.getInstance(application)
    private var userDao: UserDao = userDatabase.noteDao()

    private val executorService = Executors.newSingleThreadExecutor()

    fun insert(user: Profile) = executorService.execute { userDao.insert(user) }

    fun update(user: Profile) = executorService.execute { userDao.update(user) }

    fun getUserData() : LiveData<Profile> = userDao.getUserData()

    fun clear() = executorService.execute { userDao.clear() }
}