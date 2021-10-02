package com.codechef.ffds

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codechef.ffds.Api.retrofitService
import com.codechef.ffds.databinding.MessagesFragmentBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.ExecutionException

class MessagesFragment : Fragment() {

    private lateinit var binding: MessagesFragmentBinding

    private val conversations = ArrayList<Conversation>()
    private val messages = ArrayList<Messages>()
    private val matches = ArrayList<Messages>()
    private val profiles = ArrayList<Profile>()
    private val chats = ArrayList<Chat>()
    private var user = Profile()
    private lateinit var matchAdapter: MessageAdapter
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MessagesFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        binding.apply {

            matchAdapter = MessageAdapter(requireContext(), matches, false)
            matchesRecyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            matchesRecyclerView.adapter = matchAdapter
            messageAdapter = MessageAdapter(requireContext(), messages, true)
            messagesRecyclerView.layoutManager = LinearLayoutManager(context)
            messagesRecyclerView.adapter = messageAdapter
            matchAdapter.setOnItemClickListener(object : MessageAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val intent = Intent(activity, ChatActivity::class.java)
                    intent.putExtra("Name", matches[position].name)
                    intent.putExtra("ID", matches[position].id)
                    intent.putExtra("ConversationId", matches[position].conversationId)
                    startActivity(intent)
                }
            })

            messageAdapter.setOnItemClickListener(object : MessageAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val intent = Intent(activity, ChatActivity::class.java)
                    intent.putExtra("Name", messages[position].name)
                    intent.putExtra("ID", messages[position].id)
                    intent.putExtra("ConversationId", messages[position].conversationId)
                    startActivity(intent)
                }
            })

            showHideViews()
            viewModel = ViewModelProvider(
                this@MessagesFragment,
                UserViewModelFactory(requireActivity().application)
            ).get(
                UserViewModel::class.java
            )
            val prefs = requireContext().getSharedPreferences("MY PREFS", Context.MODE_PRIVATE)
            viewModel.getUserData(prefs.getString("id", "")!!)
                .observe(viewLifecycleOwner, { currentUser: Profile ->
                        user = currentUser
                        updateRecyclerView()
                        updateData(user)

                })
        }
    }

    private fun updateRecyclerView() {
        viewModel.getAllConversations()
            .observe(viewLifecycleOwner, { conversations: List<Conversation> ->
                matches.clear()
                messages.clear()
                for ((members, _id) in conversations) {
                    var id = members[1]
                    if (id == user._id) id = members[0]
                    viewModel.getUserData(id).observe(viewLifecycleOwner, { profile: Profile? ->
                        if (profile != null) {
                            viewModel.getLastMessage(_id)
                                .observe(viewLifecycleOwner, { chat: Chat? ->
                                    if (chat == null) matches.add(
                                        Messages(
                                            "",
                                            profile.userArray,
                                            profile.name,
                                            profile._id,
                                            _id
                                        )
                                    ) else messages.add(
                                        Messages(
                                            chat.text,
                                            profile.userArray,
                                            profile.name,
                                            profile._id,
                                            _id
                                        )
                                    )
                                    if (messages.size + matches.size == conversations.size) showHideViews()
                                })
                        }
                    })
                }
            })
    }

    private fun updateData(user: Profile?) {
        retrofitService.getAllConversations(user!!.token)!!
            .enqueue(object : Callback<ConversationList?> {
                override fun onFailure(call: Call<ConversationList?>, t: Throwable) {
                    Toast.makeText(context, "All Conversation: " + t.message, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onResponse(
                    call: Call<ConversationList?>,
                    response: Response<ConversationList?>
                ) {
                    if (response.message() == "OK") {
                        val conversationList = response.body()
                        if (conversationList != null) {
                            conversations.addAll(conversationList.matchedOnly)
                            conversations.addAll(conversationList.hasMessages)
                            if (conversations.isNotEmpty()) {
                                profiles.clear()
                                chats.clear()
                                getData(user, 0)
                            }
                        }
                    } else {
                        val gson = Gson()
                        val (_,message) = gson.fromJson(response.errorBody()?.charStream(), Error::class.java)
                        Toast.makeText(
                            requireContext(),
                            "Error ${response.code()}: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    private fun getData(user: Profile?, count: Int) {
        val (members, _id) = conversations[count]
        var id = members[1]
        if (id == user!!._id) id = members[0]
        retrofitService.getUserDetail(id)!!.enqueue(object : Callback<Profile?> {
            override fun onFailure(call: Call<Profile?>, t: Throwable) {
                Toast.makeText(context, "User details: " + t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Profile?>, response: Response<Profile?>) {
                if (response.message() == "OK") {
                    val profile = response.body()
                    if (profile != null) {
                        profiles.add(profile.copy(userArray = getByteArray(profile.userImage.url)))
                        retrofitService.getAllMessages(user.token, _id)!!
                            .enqueue(object : Callback<ArrayList<Chat>?> {
                                override fun onFailure(call: Call<ArrayList<Chat>?>, t: Throwable) {
                                    Toast.makeText(
                                        context,
                                        "all messages: " + t.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onResponse(
                                    call: Call<ArrayList<Chat>?>,
                                    response: Response<ArrayList<Chat>?>
                                ) {
                                    if (response.message() == "OK") {
                                        val chat = response.body()
                                        if (chat != null)
                                            for (ch in chat)
                                                chats.add(ch.copy(type = getType(user, ch)))
                                        if (profiles.size == conversations.size) {
                                            viewModel.insertAllMessages(*chats.toTypedArray())
                                            viewModel.insertUser(*profiles.toTypedArray())
                                            viewModel.insertAllConversations(*conversations.toTypedArray())
                                        } else getData(user, count + 1)
                                    } else {
                                        val gson = Gson()
                                        val (_,message) = gson.fromJson(response.errorBody()?.charStream(), Error::class.java)
                                        Toast.makeText(
                                            requireContext(),
                                            "Error ${response.code()}: all msgs: $message",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            })
                    }
                } else Toast.makeText(
                    requireContext(),
                    "details: " + response.message(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getType(user: Profile?, chat: Chat): ItemType {
        return if (chat.senderId == user!!._id) ItemType.Sent else ItemType.Received
    }

    private fun getByteArray(url: String): List<Byte> {
        var bitmap: Bitmap? = null
        if (url.isEmpty()) bitmap =
            BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.profile_image) else {
            try {
                bitmap = Glide.with(this).asBitmap().load(Uri.parse(url)).submit().get()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray().toList()
    }

    private fun showHideViews() {

        binding.apply {

            if (matches.isEmpty()) {
                noMatches.visibility = View.VISIBLE
                matchesRecyclerView.visibility = View.GONE
            } else {
                matchAdapter.submitList(matches)
                matchesRecyclerView.visibility = View.VISIBLE
                noMatches.visibility = View.GONE
            }
            if (messages.isEmpty()) {
                noMessages.visibility = View.VISIBLE
                messagesRecyclerView.visibility = View.GONE
            } else {
                messageAdapter.submitList(messages)
                messagesRecyclerView.visibility = View.VISIBLE
                noMessages.visibility = View.GONE
            }
        }
    }
}