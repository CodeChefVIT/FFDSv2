package com.codechef.ffds

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codechef.ffds.databinding.ActivityChatBinding
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {

    private val sendMessage = "sendMessage"
    private val getMessage = "getMessage"
    private val addUser = "addUser"
    private lateinit var mSocket: Socket
    private lateinit var viewModel: UserViewModel
    private var chats = ArrayList<Chat>()
    private val chatAdapter = ChatAdapter()
    private var conversationId = ""
    private var senderId = ""
    private var receiverId = ""
    private var authToken = ""
    private lateinit var binding: ActivityChatBinding

    init {
        try {
            mSocket = IO.socket("https://ffds-backend.herokuapp.com/")
        } catch (e: URISyntaxException) {
        }
    }

    private val onNewMessage =
        Emitter.Listener { args ->
            runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val sId: String
                val message: String
                val createdAt: String
                val timeStamp: Date?
                try {
                    sId = data.getString("senderId")
                    message = data.getString("text")
                    createdAt = data.getString("createdAt")
                    timeStamp =
                        SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(
                            createdAt
                        )
                } catch (e: JSONException) {
                    return@Runnable
                }

                val chat = Chat(
                    conversationId = conversationId,
                    senderId = sId,
                    text = message,
                    type = ItemType.Received,
                    createdAt = timeStamp,
                    updatedAt = timeStamp
                )
                chats.add(chat)
                viewModel.insertAllMessages(chat)
                chatAdapter.submitList(chats)
                chatAdapter.notifyItemInserted(chats.size)
                binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mSocket.connect()
        mSocket.on(getMessage, onNewMessage)

        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(application)
        ).get(UserViewModel::class.java)

        val prefs = getSharedPreferences("MY PREFS", MODE_PRIVATE)
        viewModel.getUserData(prefs.getString("id", "")!!).observe(this) { user ->
            senderId = user._id
            authToken = user.token
            getData()
            mSocket.emit(addUser, senderId)
        }

        val bundle = intent.extras
        val name = bundle?.getString("Name")
        receiverId = bundle?.getString("ID")!!
        conversationId = bundle.getString("ConversationId")!!

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        binding.apply {
            nameText.text = name

            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = chatAdapter

            back.setOnClickListener {
                onBackPressed()
            }

            sendBtn.setOnClickListener {
                val msg = message.text.toString().trim()
                if (msg.isNotEmpty()) {
                    message.text = null
                    val date = Date()
                    val chat = Chat(
                        conversationId = conversationId,
                        senderId = senderId,
                        text = msg,
                        type = ItemType.Sent,
                        createdAt = date,
                        updatedAt = date
                    )

                    addDate(chat.createdAt.time)

                    chats.add(chat)
                    sendMessage(chat)

                    chatAdapter.submitList(chats)
                    chatAdapter.notifyItemInserted(chats.size)
                    recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        }
    }

    private fun getData() {

        viewModel.getAllMessages(conversationId).observe(this) { list ->
            chats.clear()
            for (chat in list) {
                addDate(chat.createdAt.time)
                chats.add(chat.copy(type = getType(chat)))
            }
            chatAdapter.submitList(chats)
        }

        Api.retrofitService.getAllMessages(
            authToken,
            conversationId = conversationId
        )!!
            .enqueue(object : Callback<ArrayList<Chat>?> {
                override fun onFailure(call: Call<ArrayList<Chat>?>, t: Throwable) {
                    Toast.makeText(
                        baseContext,
                        "MSG: ${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onResponse(
                    call: Call<ArrayList<Chat>?>,
                    response: Response<ArrayList<Chat>?>
                ) {
                    if (response.message() == "OK") {
                        val list = response.body()
                        val chats = ArrayList<Chat>()
                        if (list != null) {
                            for (chat in list)
                                chats.add(chat.copy(type = getType(chat)))

                            viewModel.insertAllMessages(*chats.toTypedArray())
                        }
                    } else {
                        val gson = Gson()
                        val (_,message) = gson.fromJson(response.errorBody()?.charStream(), Error::class.java)
                        Toast.makeText(
                            applicationContext,
                            "Error ${response.code()}: MESSAGES: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })


    }

    private fun addDate(timestamp: Long) {
        val dt = Chat(
            conversationId = "",
            senderId = "",
            text = getDate(timestamp),
            type = ItemType.Date,
            createdAt = Date(),
            updatedAt = Date()
        )
        if (chats.size != 0) {
            val prevChat = chats.last()
            val prevTimeStamp = prevChat.createdAt.time
            if (getDate(timestamp) != getDate(prevTimeStamp))
                chats.add(dt)
        } else
            chats.add(dt)
    }

    private fun getType(chat: Chat): ItemType {
        return if (chat.senderId == senderId)
            ItemType.Sent
        else
            ItemType.Received
    }

    private fun getDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        return sdf.format(date)
    }

    private fun sendMessage(chat: Chat) {

        val fields = mutableMapOf(
            "conversationId" to chat.conversationId,
            "senderId" to chat.senderId,
            "text" to chat.text,
            "createdAt" to chat.createdAt,
            "updatedAt" to chat.updatedAt
        )

        Api.retrofitService.sendMessage(authToken, fields)!!
            .enqueue(object : Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.message() == "OK") {
                        viewModel.insertAllMessages(chat)
                        Toast.makeText(baseContext, "Message sent", Toast.LENGTH_SHORT).show()
                        mSocket.emit(
                            sendMessage,
                            senderId,
                            receiverId,
                            chat.text,
                            chat.createdAt
                        )
                    } else {
                        val gson = Gson()
                        val (_,message) = gson.fromJson(response.errorBody()?.charStream(), Error::class.java)
                        Toast.makeText(
                            applicationContext,
                            "Error ${response.code()}: send: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

}