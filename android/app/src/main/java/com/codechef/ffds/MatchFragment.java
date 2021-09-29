package com.codechef.ffds;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchFragment extends Fragment {

    private ArrayList<Profile> matches;
    private Adapter adapter;

    private Profile user;
    private UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.matches_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View root, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        matches = new ArrayList<>();

        viewModel = new ViewModelProvider(this, new UserViewModelFactory(getActivity().getApplication())).get(UserViewModel.class);

        SharedPreferences prefs = getContext().getSharedPreferences("MY PREFS", MODE_PRIVATE);
        viewModel.getUserData(prefs.getString("id", "")).observe(getViewLifecycleOwner(), currentUser -> {
            if (user == null) {
                user = currentUser;
                getData(user);
            }
        });


        TextView noMatches = root.findViewById(R.id.no_matches);
        if (matches.isEmpty())
            noMatches.setVisibility(View.VISIBLE);
        else
            noMatches.setVisibility(View.INVISIBLE);

        RecyclerView recyclerView = root.findViewById(R.id.matches_list);
        adapter = new Adapter(getContext(), matches);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(position -> {

        });
    }

    private void getData(Profile user) {
        Api.INSTANCE.getRetrofitService().getFeed(user.getToken()).enqueue(new Callback<>() {

            @Override
            public void onFailure(@NonNull Call<Feed> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call<Feed> call, @NonNull Response<Feed> response) {
                if (response.message().equals("OK")) {
                    Feed feed = response.body();
                    if (feed != null) {
                        matches = feed.getFeed();
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Gson gson = new Gson();
                    Error error = gson.fromJson(response.errorBody().charStream(), Error.class);
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAbsoluteAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    Api.INSTANCE.getRetrofitService().createNewConversation(user.getToken(), user.get_id()).enqueue(new Callback<>() {
                        @Override
                        public void onFailure(@NonNull Call<Conversation> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(@NonNull Call<Conversation> call, @NonNull Response<Conversation> response) {
                            if (response.message().equals("OK")) {
                                matches.remove(position);
                                adapter.notifyItemRemoved(position);
                                FragmentManager manager = getParentFragmentManager();
                                manager.beginTransaction().replace(R.id.container, new MatchedFragment()).commit();
                            } else {
                                Gson gson = new Gson();
                                Error error = gson.fromJson(response.errorBody().charStream(), Error.class);
                                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                case ItemTouchHelper.LEFT:
                    Api.INSTANCE.getRetrofitService().rejectMatch(user.getToken(), user.get_id()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.message().equals("OK")) {
                                matches.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(requireContext(), "User successfully rejected", Toast.LENGTH_SHORT).show();
                            } else {
                                Gson gson = new Gson();
                                Error error = gson.fromJson(response.errorBody().charStream(), Error.class);
                                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                    .addSwipeRightActionIcon(R.drawable.cupid_icon)
                    .addSwipeLeftActionIcon(R.drawable.reject_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
