package com.codechef.ffds;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment {

    private ArrayList<Conversation> conversations = new ArrayList<>();
    private ArrayList<Messages> messages = new ArrayList<>();
    private ArrayList<Messages> matches = new ArrayList<>();

    private ArrayList<Profile> profiles = new ArrayList<>();
    private ArrayList<Chat> chats = new ArrayList<>();

    private TextView noMatches;
    private TextView noMessages;
    private RecyclerView recyclerView;
    private MessageAdapter matchAdapter;
    private RecyclerView recyclerView1;
    private MessageAdapter messageAdapter;

    private Profile user;
    private UserViewModel viewModel;

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

        showHideViews();

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
                                matches.add(new Messages("", profile.getUserArray(), profile.getName(), profile.get_id(), conversation.get_id()));
                            else
                                messages.add(new Messages(chat.getText(), profile.getUserArray(), profile.getName(), profile.get_id(), conversation.get_id()));

                            if (messages.size() + matches.size() == conversations.size())
                                showHideViews();
                        });
                    }
                });
            }
        });
    }

    private void updateData(Profile user) {

        Api.INSTANCE.getRetrofitService().getAllConversations(user.getToken()).enqueue(new Callback<ConversationList>() {
            @Override
            public void onFailure(@NonNull Call<ConversationList> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "All Conversation: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call<ConversationList> call, @NonNull Response<ConversationList> response) {
                if (response.message().equals("OK")) {
                    ConversationList conversationList = response.body();
                    if (conversationList != null) {
                        conversations.addAll(conversationList.getMatchedOnly());
                        conversations.addAll(conversationList.getHasMessages());
                        if(!conversations.isEmpty()) {
                            profiles.clear();
                            chats.clear();
                            getData(user, 0);
                        }
                    }
                } else {
                    Gson gson = new Gson();
                    Error error = gson.fromJson(response.errorBody().charStream(), Error.class);
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
                        profiles.add(profile.copy(profile.getEmail(), profile.get_id(), profile.getToken(), profile.getName(), profile.getPhone(), profile.getVerified(), profile.getBranch(), profile.getGender(), profile.getBio(), profile.getYear(), profile.getExpectations(), profile.getSlot(), profile.getUserImage(), getByteArray(profile.getUserImage().getUrl()), profile.getGenderPreference(), profile.getAccepted(), profile.getRejected(), profile.getBlocked()));

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

    private byte[] getByteArray(String url) {
        Bitmap bitmap = null;
        if(url.isEmpty())
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.profile_image);
        else {
            try {
                bitmap = Glide.with(this).asBitmap().load(Uri.parse(url)).submit().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

    private void showHideViews() {
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
}
