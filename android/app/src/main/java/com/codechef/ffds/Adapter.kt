package com.codechef.ffds;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final ArrayList<Profile> matches;
    private final Context context;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.matches_item_layout, parent, false);
        return new ViewHolder(view, mListener);
    }

    Adapter(Context context, ArrayList<Profile> matches) {
        this.context = context;
        this.matches = matches;
    }

    public interface OnItemClickListener {
        void onShowProfileClicked(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull final Adapter.ViewHolder holder, final int position) {

        holder.name.setText(matches.get(position).getName());
        holder.bio.setText(matches.get(position).getBio());

        Image image = matches.get(position).getUserImage();
        Bitmap bitmap = null;
        if(image.getUrl().isEmpty())
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.profile_image);
        else {
            try {
                bitmap = Glide.with(context).asBitmap().load(Uri.parse(image.getUrl())).submit().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        holder.dp.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, bio;
        Button showProfile;
        CircleImageView dp;

        ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.match_name);
            bio = itemView.findViewById(R.id.match_bio);
            showProfile = itemView.findViewById(R.id.view_match);
            dp = itemView.findViewById(R.id.match_image);

            showProfile.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.onShowProfileClicked(position);
                }
            });
        }
    }
}
