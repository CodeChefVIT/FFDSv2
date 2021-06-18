package com.codechef.ffds

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codechef.ffds.databinding.ItemDateBinding
import com.codechef.ffds.databinding.ItemMsgReceivedBinding
import com.codechef.ffds.databinding.ItemMsgSentBinding

class ChatAdapter: ListAdapter<Chat, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")

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

    inner class ReceivedViewHolder(private val binding:ItemMsgReceivedBinding)
        :RecyclerView.ViewHolder(binding.root) {
            fun bind(chat: Chat) {
                binding.apply {
                    message.text = chat.msg
                    time.text = chat.time
                }
            }
        }

    inner class SentViewHolder(private val binding:ItemMsgSentBinding)
        :RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                message.text = chat.msg
                time.text = chat.time
            }
        }
    }

    inner class DateViewHolder(private val binding:ItemDateBinding)
        :RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                date.text = chat.time
            }
        }
    }

    override fun getItemViewType(position: Int) = position

    class DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = //(oldItem.id == newItem.id)
            (oldItem == newItem)

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat) = (oldItem == newItem)
    }
}