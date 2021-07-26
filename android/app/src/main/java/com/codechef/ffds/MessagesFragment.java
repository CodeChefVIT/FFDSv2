package com.codechef.ffds;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    ArrayList<Messages> matches = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messages_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View root, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        TextView noMatches = root.findViewById(R.id.no_matches);
        TextView noMessages = root.findViewById(R.id.no_messages);

        ProgressBar matchesProgress = root.findViewById(R.id.matchesProgress);
        ProgressBar messagesProgress = root.findViewById(R.id.messagesProgress);

        RecyclerView recyclerView = root.findViewById(R.id.matches_view);
        MessageAdapter matchAdapter = new MessageAdapter(requireContext(), matches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(matchAdapter);

        RecyclerView recyclerView1 = root.findViewById(R.id.messages_view);
        MessageAdapter messageAdapter = new MessageAdapter(requireContext(), messages);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView1.setAdapter(messageAdapter);

        matchAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("Name", matches.get(position).getName());
            intent.putExtra("ID", matches.get(position).getId());
            intent.putExtra("ConversationId", matches.get(position).getConversationId());
            startActivity(intent);
        });

        messageAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("Name", messages.get(position).getName());
            intent.putExtra("ID", messages.get(position).getId());
            intent.putExtra("ConversationId", messages.get(position).getConversationId());
            startActivity(intent);
        });

        Api.INSTANCE.getRetrofitService().getAllConversations(user.getToken()).enqueue(new Callback<ArrayList<Conversation>>() {
            @Override
            public void onFailure(@NotNull Call<ArrayList<Conversation>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "All Conversation: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getContext(), "User details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(@NotNull Call<Profile> call, @NotNull Response<Profile> response) {
                                    if (response.message().equals("OK")) {
                                        Profile profile = response.body();
                                        if (profile != null) {
                                            Api.INSTANCE.getRetrofitService().getLastMessage(conversation.get_id()).enqueue(new Callback<Chat>() {
                                                @Override
                                                public void onFailure(@NotNull Call<Chat> call, @NotNull Throwable t) {
                                                    Toast.makeText(getContext(), "last message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call<Chat> call, @NotNull Response<Chat> response) {
                                                    if (response.message().equals("OK")) {
                                                        Chat chat = response.body();
                                                        if (chat != null)
                                                            messages.add(new Messages(chat.getText(), R.drawable.re, profile.getName(), profile.get_id(), conversation.get_id()));

                                                        if (messages.size() + matches.size() == conversations.size()) {
                                                            matchesProgress.setVisibility(View.GONE);
                                                            messagesProgress.setVisibility(View.GONE);

                                                            matchAdapter.submitList(matches);
                                                            if (matches.isEmpty()) {
                                                                noMatches.setVisibility(View.VISIBLE);
                                                                recyclerView.setVisibility(View.GONE);
                                                            } else {
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                noMatches.setVisibility(View.GONE);
                                                            }
                                                            messageAdapter.submitList(messages);
                                                            if (messages.isEmpty()) {
                                                                noMessages.setVisibility(View.VISIBLE);
                                                                recyclerView1.setVisibility(View.GONE);
                                                            } else {
                                                                recyclerView1.setVisibility(View.VISIBLE);
                                                                noMessages.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    } else if (response.message().equals("Not Found")) {
                                                        matches.add(new Messages("", R.drawable.re, profile.getName(), profile.get_id(), conversation.get_id()));
                                                        if (messages.size() + matches.size() == conversations.size()) {
                                                            matchesProgress.setVisibility(View.GONE);
                                                            messagesProgress.setVisibility(View.GONE);
                                                            
                                                            matchAdapter.submitList(matches);
                                                            if (matches.isEmpty()) {
                                                                noMatches.setVisibility(View.VISIBLE);
                                                                recyclerView.setVisibility(View.GONE);
                                                            } else {
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                noMatches.setVisibility(View.GONE);
                                                            }
                                                            messageAdapter.submitList(messages);
                                                            if (messages.isEmpty()) {
                                                                noMessages.setVisibility(View.VISIBLE);
                                                                recyclerView1.setVisibility(View.GONE);
                                                            } else {
                                                                recyclerView1.setVisibility(View.VISIBLE);
                                                                noMessages.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    } else
                                                        Toast.makeText(requireContext(), "last msg: " + response.message(), Toast.LENGTH_SHORT).show();
                                                }

                                            });
                                        }
                                    } else
                                        Toast.makeText(requireContext(), "details: " + response.message(), Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    }
                } else
                    Toast.makeText(requireContext(), "Conversations: " + response.message(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
