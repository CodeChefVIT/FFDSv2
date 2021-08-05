package com.codechef.ffds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codechef.ffds.databinding.ItemDateBinding
import com.codechef.ffds.databinding.ItemMsgReceivedBinding
import com.codechef.ffds.databinding.ItemMsgSentBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<Chat, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val currentItem = getItem(viewType)
        return when (currentItem.type) {
            ItemType.Sent -> {
                val binding =
                    ItemMsgSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SentViewHolder(binding)
            }
            ItemType.Received -> {
                val binding = ItemMsgReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceivedViewHolder(binding)
            }
            ItemType.Date -> {
                val binding =
                    ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DateViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        when (holder.javaClass) {
            ReceivedViewHolder::class.java -> {
                val viewHolder = holder as ReceivedViewHolder
                viewHolder.bind(currentItem)
            }
            SentViewHolder::class.java -> {
                val viewHolder = holder as SentViewHolder
                viewHolder.bind(currentItem)
            }
            else -> {
                val viewHolder = holder as DateViewHolder
                viewHolder.bind(currentItem)
            }
        }
    }

    private fun getTime(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("hh:mm aa", Locale.US)
        return sdf.format(date)
    }

    inner class ReceivedViewHolder(private val binding: ItemMsgReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                message.text = chat.text
                time.text = getTime(chat.createdAt.time)
            }
        }
    }

    inner class SentViewHolder(private val binding: ItemMsgSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                message.text = chat.text
                time.text = getTime(chat.createdAt.time)
            }
        }
    }

    inner class DateViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                date.text = chat.text
            }
        }
    }

    override fun getItemViewType(position: Int) = position

    class DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = (oldItem._id == newItem._id)

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat) = (oldItem == newItem)
    }
}