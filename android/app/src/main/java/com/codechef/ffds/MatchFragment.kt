package com.codechef.ffds

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codechef.ffds.Api.retrofitService
import com.codechef.ffds.databinding.MatchesFragmentBinding
import com.google.gson.Gson
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MatchFragment : Fragment() {

    private lateinit var binding: MatchesFragmentBinding

    private var matches: ArrayList<Profile> = ArrayList()
    private var user = Profile()
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MatchesFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        binding.apply {

            matches = ArrayList()
            viewModel =
                ViewModelProvider(
                    this@MatchFragment,
                    UserViewModelFactory(requireActivity().application)
                ).get(
                    UserViewModel::class.java
                )
            val prefs = requireContext().getSharedPreferences("MY PREFS", Context.MODE_PRIVATE)
            viewModel.getUserData(prefs.getString("id", "")!!)
                .observe(viewLifecycleOwner, { currentUser: Profile ->
                    user = currentUser
                    getData(user)

                })
            if (matches.isEmpty()) noMatches.visibility = View.VISIBLE else noMatches.visibility =
                View.INVISIBLE
            adapter = Adapter(requireContext(), matches)
            matchesRecyclerView.adapter = adapter
            matchesRecyclerView.layoutManager = LinearLayoutManager(context)
            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(matchesRecyclerView)
            adapter.setOnItemClickListener(object : Adapter.OnItemClickListener {
                override fun onShowProfileClicked(position: Int) {

                }
            })
        }
    }

    private fun getData(user: Profile?) {
        retrofitService.getFeed(user!!.token)!!.enqueue(object : Callback<Feed?> {
            override fun onFailure(call: Call<Feed?>, t: Throwable) {
                Toast.makeText(context, "Failed " + t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Feed?>, response: Response<Feed?>) {
                if (response.message() == "OK") {
                    val feed = response.body()
                    if (feed != null) {
                        matches = feed.feed
                        adapter.notifyDataSetChanged()
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

    private var simpleCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                when (direction) {
                    ItemTouchHelper.RIGHT -> retrofitService.createNewConversation(
                        user.token,
                        user._id
                    )!!
                        .enqueue(object : Callback<Conversation?> {
                            override fun onFailure(call: Call<Conversation?>, t: Throwable) {
                                Toast.makeText(context, "Failed " + t.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<Conversation?>,
                                response: Response<Conversation?>
                            ) {
                                if (response.message() == "OK") {
                                    matches.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    val manager = parentFragmentManager
                                    manager.beginTransaction()
                                        .replace(R.id.container, MatchedFragment()).commit()
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
                    ItemTouchHelper.LEFT -> retrofitService.rejectMatch(user.token, user._id)!!
                        .enqueue(object : Callback<ResponseBody?> {
                            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                Toast.makeText(context, "Failed " + t.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody?>,
                                response: Response<ResponseBody?>
                            ) {
                                if (response.message() == "OK") {
                                    matches.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    Toast.makeText(
                                        requireContext(),
                                        "User successfully rejected",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.cupid_icon)
                    .addSwipeLeftActionIcon(R.drawable.reject_icon)
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
}