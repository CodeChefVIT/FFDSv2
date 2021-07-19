package com.codechef.ffds;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment {

    Profile user = MainActivity.Companion.getUser();
    ArrayList<Conversation> conversations;
    ArrayList<Messages> messages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.messages_activity, container, false);

        TextView noMatches = root.findViewById(R.id.no_matches);
        TextView noMessages = root.findViewById(R.id.no_messages);

        ArrayList<Integer> mList = new ArrayList<>();
        mList.add(R.drawable.re);
        mList.add(R.drawable.re);
        mList.add(R.drawable.re);
        mList.add(R.drawable.re);
        mList.add(R.drawable.re);

        if (mList.isEmpty())
            noMatches.setVisibility(View.VISIBLE);
        else
            noMatches.setVisibility(View.GONE);

        RecyclerView recyclerView = root.findViewById(R.id.matches_view);
        MatchAdapter adapter = new MatchAdapter(requireContext(), mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {

        });

        RecyclerView recyclerView1 = root.findViewById(R.id.messages_view);
        MessageAdapter messageAdapter = new MessageAdapter(requireContext(), messages);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView1.setAdapter(messageAdapter);

        messageAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("Name", messages.get(position).getName());
            intent.putExtra("id", messages.get(position).getId());
            startActivity(intent);
        });

        Api.INSTANCE.getRetrofitService().getAllConversations(user.getToken()).enqueue(new Callback<ArrayList<Conversation>>() {
            @Override
            public void onFailure(@NotNull Call<ArrayList<Conversation>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call<ArrayList<Conversation>> call, @NotNull Response<ArrayList<Conversation>> response) {
                if (response.message().equals("OK")) {
                    conversations = response.body();
                    if (conversations != null) {
                        messages.clear();
                        for (Conversation conversation : conversations) {
                            String id = conversation.getMembers().get(1);
                            Api.INSTANCE.getRetrofitService().getUserDetail(id).enqueue(new Callback<Profile>() {
                                @Override
                                public void onFailure(@NotNull Call<Profile> call, @NotNull Throwable t) {
                                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(@NotNull Call<Profile> call, @NotNull Response<Profile> response) {
                                    if (response.message().equals("OK")) {
                                        Profile user = response.body();
                                        if (user != null)
                                            messages.add(new Messages("last message", R.drawable.re, user.getName(), user.get_id()));
                                    } else
                                        Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                        messageAdapter.notifyDataSetChanged();
                        if (messages.isEmpty())
                            noMessages.setVisibility(View.VISIBLE);
                        else
                            noMessages.setVisibility(View.GONE);
                    }
                } else
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
            }

        });

        return root;
    }
}
