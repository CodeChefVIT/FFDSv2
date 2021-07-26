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
    private val matches: ArrayList<Messages>
) : ListAdapter<Messages, MessageAdapter.ViewHolder>(DiffCallback()) {

    lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.messages_adapter_item, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dp.setImageResource(matches[position].profileImage)
        holder.last.text = matches[position].lastMessage
        holder.name.text = matches[position].name
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    class ViewHolder constructor(itemView: View, listener: OnItemClickListener) :
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
            //(oldItem.id == newItem.id)
            (oldItem == newItem)

        override fun areContentsTheSame(oldItem: Messages, newItem: Messages) = (oldItem == newItem)
    }
}