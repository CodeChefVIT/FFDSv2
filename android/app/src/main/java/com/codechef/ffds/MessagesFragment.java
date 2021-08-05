package com.codechef.ffds;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment {

    ArrayList<Conversation> conversations;
    ArrayList<Messages> messages = new ArrayList<>();
    ArrayList<Messages> matches = new ArrayList<>();

    ArrayList<Profile> profiles = new ArrayList<>();
    ArrayList<Chat> chats = new ArrayList<>();

    TextView noMatches;
    TextView noMessages;
    ProgressBar matchesProgress;
    ProgressBar messagesProgress;
    RecyclerView recyclerView;
    MessageAdapter matchAdapter;
    RecyclerView recyclerView1;
    MessageAdapter messageAdapter;

    Profile user;
    UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messages_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View root, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        noMatches = root.findViewById(R.id.no_matches);
        noMessages = root.findViewById(R.id.no_messages);

        matchesProgress = root.findViewById(R.id.matchesProgress);
        messagesProgress = root.findViewById(R.id.messagesProgress);

        recyclerView = root.findViewById(R.id.matches_view);
        matchAdapter = new MessageAdapter(requireContext(), matches, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(matchAdapter);

        recyclerView1 = root.findViewById(R.id.messages_view);
        messageAdapter = new MessageAdapter(requireContext(), messages, true);
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

        viewModel = new ViewModelProvider(this, new UserViewModelFactory(getActivity().getApplication())).get(UserViewModel.class);

        SharedPreferences prefs = getContext().getSharedPreferences("MY PREFS", MODE_PRIVATE);
        viewModel.getUserData(prefs.getString("id", "")).observe(getViewLifecycleOwner(), currentUser -> {
            if (user == null) {
                user = currentUser;
                updateRecyclerView();
                updateData(user);
            }
        });
    }

    private void updateRecyclerView() {
        viewModel.getAllConversations().observe(getViewLifecycleOwner(), conversations -> {
            matches.clear();
            messages.clear();
            for (var conversation : conversations) {
                String id = conversation.getMembers().get(1);
                if (id.equals(user.get_id()))
                    id = conversation.getMembers().get(0);
                viewModel.getUserData(id).observe(getViewLifecycleOwner(), profile -> {
                    if (profile != null) {
                        viewModel.getLastMessage(conversation.get_id()).observe(getViewLifecycleOwner(), chat -> {
                            if (chat == null)
                                matches.add(new Messages("", R.drawable.re, profile.getName(), profile.get_id(), conversation.get_id()));
                            else
                                messages.add(new Messages(chat.getText(), R.drawable.re, profile.getName(), profile.get_id(), conversation.get_id()));

                            if (messages.size() + matches.size() == conversations.size()) {
                                matchesProgress.setVisibility(View.GONE);
                                messagesProgress.setVisibility(View.GONE);

                                if (matches.isEmpty()) {
                                    noMatches.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    matchAdapter.submitList(matches);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    noMatches.setVisibility(View.GONE);
                                }

                                if (messages.isEmpty()) {
                                    noMessages.setVisibility(View.VISIBLE);
                                    recyclerView1.setVisibility(View.GONE);
                                } else {
                                    messageAdapter.submitList(messages);
                                    recyclerView1.setVisibility(View.VISIBLE);
                                    noMessages.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateData(Profile user) {

        Api.INSTANCE.getRetrofitService().getAllConversations(user.getToken()).enqueue(new Callback<>() {
            @Override
            public void onFailure(@NotNull Call<ArrayList<Conversation>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "All Conversation: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call<ArrayList<Conversation>> call, @NotNull Response<ArrayList<Conversation>> response) {
                if (response.message().equals("OK")) {
                    conversations = response.body();
                    if (conversations != null) {
                        profiles.clear();
                        chats.clear();
                        getData(user, 0);
                    }
                } else
                    Toast.makeText(requireContext(), "Conversations: " + response.message(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(Profile user, int count) {

        Conversation conversation = conversations.get(count);
        String id = conversation.getMembers().get(1);
        if (id.equals(user.get_id()))
            id = conversation.getMembers().get(0);
        Api.INSTANCE.getRetrofitService().getUserDetail(id).enqueue(new Callback<>() {
            @Override
            public void onFailure(@NotNull Call<Profile> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "User details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call<Profile> call, @NotNull Response<Profile> response) {
                if (response.message().equals("OK")) {
                    Profile profile = response.body();
                    if (profile != null) {
                        profiles.add(profile);

                        Api.INSTANCE.getRetrofitService().getAllMessages(user.getToken(), conversation.get_id()).enqueue(new Callback<>() {
                            @Override
                            public void onFailure(@NonNull Call<ArrayList<Chat>> call, @NonNull Throwable t) {
                                Toast.makeText(getContext(), "all messages: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(@NonNull Call<ArrayList<Chat>> call, @NonNull Response<ArrayList<Chat>> response) {
                                if (response.message().equals("OK")) {
                                    var chat = response.body();
                                    if (chat != null)
                                        for (var ch : chat)
                                            chats.add(ch.copy(ch.get_id(), ch.getConversationId(), ch.getSenderId(), ch.getText(), ch.getCreatedAt(), ch.getUpdatedAt(), getType(user, ch)));

                                    if (profiles.size() == conversations.size()) {
                                        viewModel.insertAllMessages(chats.toArray(new Chat[0]));
                                        viewModel.insertUser(profiles.toArray(new Profile[0]));
                                        viewModel.insertAllConversations(conversations.toArray(new Conversation[0]));
                                    } else
                                        getData(user, count + 1);
                                } else
                                    Toast.makeText(requireContext(), "all msgs: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else
                    Toast.makeText(requireContext(), "details: " + response.message(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ItemType getType(Profile user, Chat chat) {
        if (chat.getSenderId().equals(user.get_id()))
            return ItemType.Sent;
        else
            return ItemType.Received;
    }
}
