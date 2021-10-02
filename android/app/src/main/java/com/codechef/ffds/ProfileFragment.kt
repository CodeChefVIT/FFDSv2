package com.codechef.ffds

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codechef.ffds.databinding.ProfileFragmentBinding
import com.cunoraz.tagview.Tag
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        val viewModel =
            ViewModelProvider(this, UserViewModelFactory(requireActivity().application)).get(
                UserViewModel::class.java
            )
        binding.apply {
            val prefs = requireContext().getSharedPreferences("MY PREFS", Context.MODE_PRIVATE)
            viewModel.getUserData(prefs.getString("id", "")!!)
                .observe(viewLifecycleOwner, { user: Profile? ->
                    if (user != null) {
                        val tags =
                            if (user.expectations.isEmpty()) ArrayList() else user.expectations
                        tagView.setTagMargin(10f)
                        tagView.setTextPaddingTop(2f)
                        tagView.settextPaddingBottom(2f)
                        for (tag in tags)
                            tagView.addTag(getNewTag(tag))
                        signOut.setOnClickListener {
                            viewModel.clear()
                            startActivity(Intent(context, LoginActivity::class.java))
                            requireActivity().finishAffinity()
                        }
                        bio.text = user.bio
                        name.text = user.name
                        phone.text = user.phone
                        val image = user.userArray
                        val bitmap: Bitmap = if (image.isNotEmpty()) BitmapFactory.decodeByteArray(
                            image.toByteArray(),
                            0,
                            image.size
                        ) else BitmapFactory.decodeResource(
                            resources, R.drawable.profile_image
                        )
                        profileImage.setImageBitmap(bitmap)
                    }
                })
            editProfile.setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        UpdateProfileActivity::class.java
                    )
                )
            }
        }
    }

    @Throws(FileNotFoundException::class)
    private fun loadImageFromStorage(path: String): Bitmap {
        val f = File(path, "profileImage.jpg")
        return BitmapFactory.decodeStream(FileInputStream(f))
    }

    private fun getNewTag(text: String): Tag {
        val tag = Tag(text)
        tag.layoutColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        return tag
    }
}