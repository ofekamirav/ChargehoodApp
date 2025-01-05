package com.example.chargehoodapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chargehoodapp.databinding.WelcomeFragmentBinding

class WelcomeFragment: Fragment() {

    private var binding: WelcomeFragmentBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=WelcomeFragmentBinding.inflate(inflater,container,false)

        return binding?.root
    }
}