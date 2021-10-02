package com.codechef.ffds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codechef.ffds.databinding.FragmentMatchedBinding

class MatchedFragment : Fragment() {

    private lateinit var binding: FragmentMatchedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            sendMessage.setOnClickListener {

            }

            showProfile.setOnClickListener {

            }

        }

    }

}