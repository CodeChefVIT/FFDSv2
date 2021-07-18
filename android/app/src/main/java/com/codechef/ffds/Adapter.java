package com.codechef.ffds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final ArrayList<User> matches;
    private final Context context;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_item_layout, parent, false);
        return new ViewHolder(view, mListener);
    }

    Adapter(Context context, ArrayList<User> matches) {
        this.context = context;
        this.matches = matches;
    }

    public interface OnItemClickListener {
        void onShowProfileClicked(int position);

        void onRejectClicked(int position);

        void onAcceptClicked(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull final Adapter.ViewHolder holder, final int position) {

        holder.name.setText(matches.get(position).getName());
        holder.bio.setText(matches.get(position).getBio());
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, bio;
        Button showProfile;
        ImageView reject, accept;

        ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.match_name);
            bio = itemView.findViewById(R.id.match_bio);
            showProfile = itemView.findViewById(R.id.view_match);
            reject = itemView.findViewById(R.id.reject);
            accept = itemView.findViewById(R.id.accept);

            showProfile.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.onShowProfileClicked(position);
                }
            });

            reject.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.onRejectClicked(position);
                }
            });

            accept.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.onAcceptClicked(position);
                }
            });
        }
    }
}
