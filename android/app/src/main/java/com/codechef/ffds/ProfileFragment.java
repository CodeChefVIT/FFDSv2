package com.codechef.ffds;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.profile_activity, container, false);

        UserViewModel viewModel = new ViewModelProvider(this, new UserViewModelFactory(requireActivity().getApplication())).get(UserViewModel.class);

        TextView bio = root.findViewById(R.id.bio);
        TextView name = root.findViewById(R.id.your_name);
        TextView phone = root.findViewById(R.id.phone_no);


        viewModel.getUserData().observe(getViewLifecycleOwner(), user -> {

            if (user != null) {
                ArrayList<String> tags = user.getExpectations().isEmpty() ? new ArrayList<>() : (ArrayList<String>) user.getExpectations();

                TagView tagView = root.findViewById(R.id.tagView2);
                tagView.setTagMargin(10f);
                tagView.setTextPaddingTop(2f);
                tagView.settextPaddingBottom(2f);
                for (String tag : tags)
                    tagView.addTag(getNewTag(tag));

                Button signOut = root.findViewById(R.id.sign_out);
                signOut.setOnClickListener(v -> {
                    viewModel.clear();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finishAffinity();
                });

                bio.setText(user.getBio());
                name.setText(user.getName());
                phone.setText(user.getPhone());

                CircleImageView imageView = root.findViewById(R.id.profileImage);
                /*byte[] image = Base64.decode(user.getUserImage().getBytes(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageView.setImageBitmap(bitmap);*/
            }
        });

        Button edit = root.findViewById(R.id.edit_profile);
        edit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UpdateProfileActivity.class));
        });

        return root;
    }

    private Bitmap loadImageFromStorage(String path) throws FileNotFoundException {
        File f = new File(path, "profileImage.jpg");
        return BitmapFactory.decodeStream(new FileInputStream(f));
    }

    private Tag getNewTag(String text) {
        Tag tag = new Tag(text);
        tag.layoutColor =
                ContextCompat.getColor(getContext(), R.color.colorPrimary);

        return tag;
    }
}
