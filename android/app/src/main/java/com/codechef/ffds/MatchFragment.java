package com.codechef.ffds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchFragment extends Fragment {

    ArrayList<User> matches;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.match_activity, container, false);

        matches = new ArrayList<>();
        matches.add(new User("Ishan", "axil.ishan3@gmail.com", "Traveller and a hopeless romantic "));
        matches.add(new User("Shriyashish", "axil.ishan3@gmail.com", "Sensitive non-weeb who fantasizes vishu "));
        matches.add(new User("DSP", "axil.ishan3@gmail.com", "9 pointer but still a stud "));

        TextView noMatches = root.findViewById(R.id.no_matches);
        if (matches.isEmpty())
            noMatches.setVisibility(View.VISIBLE);
        else
            noMatches.setVisibility(View.INVISIBLE);

        RecyclerView recyclerView = root.findViewById(R.id.matches_list);
        Adapter adapter = new Adapter(getContext(), matches);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onShowProfileClicked(int position) {

            }

            @Override
            public void onRejectClicked(int position) {

            }

            @Override
            public void onAcceptClicked(int position) {

            }
        });

        return root;
    }

}
