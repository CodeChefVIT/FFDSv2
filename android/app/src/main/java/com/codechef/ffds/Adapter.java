package com.codechef.ffds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, bio;
        Button showProfile;

        ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.match_name);
            bio = itemView.findViewById(R.id.match_bio);
            showProfile = itemView.findViewById(R.id.view_match);

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
