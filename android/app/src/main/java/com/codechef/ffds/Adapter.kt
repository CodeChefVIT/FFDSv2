package com.codechef.ffds

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import java.util.concurrent.ExecutionException

class Adapter internal constructor(
    private val context: Context,
    private val matches: ArrayList<Profile>
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onShowProfileClicked(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.matches_item_layout, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = matches[position].name
        holder.bio.text = matches[position].bio
        val (url) = matches[position].userImage
        var bitmap: Bitmap? = null
        if (url.isEmpty()) bitmap =
            BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.profile_image) else {
            try {
                bitmap = Glide.with(context).asBitmap().load(Uri.parse(url)).submit().get()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        holder.dp.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.match_name)
        var bio: TextView = itemView.findViewById(R.id.match_bio)
        var showProfile: Button = itemView.findViewById(R.id.view_match)
        var dp: CircleImageView = itemView.findViewById(R.id.match_image)

        init {
            showProfile.setOnClickListener {
                if (listener != null) {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) listener.onShowProfileClicked(position)
                }
            }
        }
    }
}