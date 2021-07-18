package com.codechef.ffds

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codechef.ffds.databinding.ActivityChatBinding
import com.fasterxml.jackson.databind.ObjectMapper
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.MediaType
import okhttp3.RequestBody
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

    private val NEW_MESSAGE = "New Message"
    private lateinit var mSocket: Socket
    var chats = ArrayList<Chat>()
    val chatAdapter = ChatAdapter()
    lateinit var conversationId: String

    init {
        try {
            //mSocket = IO.socket("http://chat.socket.io")
        } catch (e: URISyntaxException) {
        }
    }

    private val onNewMessage =
        Emitter.Listener { args ->
            runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val username: String
                val message: String
                try {
                    username = data.getString("username")
                    message = data.getString("message")
                } catch (e: JSONException) {
                    return@Runnable
                }

                // add the message to view
                //addMessage(username, message)
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //mSocket.on(NEW_MESSAGE, onNewMessage)
        //mSocket.connect()

        Api.retrofitService.getSpecificConversation(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImhyaXRoaWsuYWdhcndhbDIwMjBAdml0c3R1ZGVudC5hYy5pbiIsImlkIjoiNjBlODA1OGRkZjNlM2YwMDMzOGEyYzQxIiwiaWF0IjoxNjI2NjI1OTE2LCJleHAiOjMyNTM2MTE4MzIsImlzcyI6Ikp1c3RBbklzc3VlciJ9.043egx2Yxmc_PnyOKosnEV_juPeHRb1zv-z-5d_eGps",
            "60f43e363644a14ee09e2af1"
        )!!
            .enqueue(object : Callback<Conversation?> {
                override fun onFailure(call: Call<Conversation?>, t: Throwable) {
                    Toast.makeText(baseContext, "CONVO: ${t.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<Conversation?>,
                    response: Response<Conversation?>
                ) {
                    if (response.message() == "OK") {
                        conversationId = response.body()?._id.toString()
                        Api.retrofitService.getAllMessages(
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImhyaXRoaWsuYWdhcndhbDIwMjBAdml0c3R1ZGVudC5hYy5pbiIsImlkIjoiNjBlODA1OGRkZjNlM2YwMDMzOGEyYzQxIiwiaWF0IjoxNjI2NjI4NzMyLCJleHAiOjMyNTM2MTc0NjQsImlzcyI6Ikp1c3RBbklzc3VlciJ9.Bq5ri1OoV_6J-0CFCJBiyrM6PQbv04Md6GDCy3kHrQo",
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
                                        Toast.makeText(
                                            baseContext,
                                            "All messages success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val list = response.body()
                                        if (list != null) {
                                            for (chat in list) {
                                                addDate(chat.createdAt.time)
                                                chats.add(chat.copy(type = getType(chat)))
                                            }
                                            chatAdapter.submitList(chats)
                                        }
                                    } else
                                        Toast.makeText(
                                            baseContext,
                                            "MESSAGES: ${response.message()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }
                            })
                    } else
                        Toast.makeText(
                            baseContext,
                            "CONVERSATION: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                }

            })

        val bundle = intent.extras
        val name = bundle?.getString("Name")

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
                        senderId = "60e8058ddf3e3f00338a2c41",
                        senderName = name!!,
                        text = msg,
                        type = ItemType.Sent,
                        createdAt = date
                    )

                    val flag = addDate(chat.createdAt.time)

                    //chats.add(if (flag) chat.copy(type = ItemType.SentMid) else chat)
                    chats.add(chat)
                    sendMessage(chat)

                    chatAdapter.submitList(chats)
                    chatAdapter.notifyItemInserted(chats.size)
                    recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    //mSocket.emit(NEW_MESSAGE, msg)
                }
            }
        }
    }

    private fun addDate(timestamp: Long): Boolean {
        val dt = Chat(
            conversationId = "",
            senderId = "",
            senderName = "",
            text = getDate(timestamp),
            type = ItemType.Date,
            createdAt = Date()
        )
        return if (chats.size != 0) {
            val prevChat = chats.last()
            val prevTimeStamp = prevChat.createdAt.time
            if (getDate(timestamp) != getDate(prevTimeStamp))
                chats.add(dt)
            prevChat.type == ItemType.Sent || prevChat.type == ItemType.SentMid
        } else {
            chats.add(dt)
            false
        }
    }

    private fun getType(chat: Chat): ItemType {
        return if (chat.senderId == "60e8058ddf3e3f00338a2c41")
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
        val om = ObjectMapper()
        val fields = om.writeValueAsString(chat)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), fields)
        Api.retrofitService.sendMessage(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImhyaXRoaWsuYWdhcndhbDIwMjBAdml0c3R1ZGVudC5hYy5pbiIsImlkIjoiNjBlODA1OGRkZjNlM2YwMDMzOGEyYzQxIiwiaWF0IjoxNjI2NjI4NzMyLCJleHAiOjMyNTM2MTc0NjQsImlzcyI6Ikp1c3RBbklzc3VlciJ9.Bq5ri1OoV_6J-0CFCJBiyrM6PQbv04Md6GDCy3kHrQo",
            body
        )?.enqueue(object : Callback<ResponseBody?> {
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.message() == "OK")
                    Toast.makeText(baseContext, "Message sent", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(baseContext, "send: ${response.message()}", Toast.LENGTH_SHORT)
                        .show()
            }
        })
    }

}