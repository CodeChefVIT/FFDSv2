package com.codechef.ffds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter internal constructor(
    private val context: Context,
    private val arrayList: ArrayList<Messages>,
    private val flag: Boolean
) : ListAdapter<Messages, RecyclerView.ViewHolder>(DiffCallback()) {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (flag) {
            false -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.match_adapter_item, parent, false)
                MatchViewHolder(view, mListener)
            }
            true -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.messages_adapter_item, parent, false)
                MessageViewHolder(view, mListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.javaClass) {
            MatchViewHolder::class.java -> {
                val viewHolder = holder as MatchViewHolder
                viewHolder.dp.setImageResource(arrayList[position].profileImage)
            }
            MessageViewHolder::class.java -> {
                val viewHolder = holder as MessageViewHolder
                viewHolder.dp.setImageResource(arrayList[position].profileImage)
                viewHolder.last.text = arrayList[position].lastMessage
                viewHolder.name.text = arrayList[position].name
            }
        }

    }

    class MatchViewHolder constructor(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        var dp: CircleImageView = itemView.findViewById(R.id.match_dp)

        init {
            itemView.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    listener.onItemClick(pos)
            }
        }
    }

    class MessageViewHolder constructor(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        var dp: CircleImageView = itemView.findViewById(R.id.dp)
        var name: TextView = itemView.findViewById(R.id.name)
        var last: TextView = itemView.findViewById(R.id.last_message)

        init {
            itemView.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    listener.onItemClick(pos)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Messages>() {
        override fun areItemsTheSame(oldItem: Messages, newItem: Messages) =
            (oldItem.id == newItem.id)

        override fun areContentsTheSame(oldItem: Messages, newItem: Messages) = (oldItem == newItem)
    }
}