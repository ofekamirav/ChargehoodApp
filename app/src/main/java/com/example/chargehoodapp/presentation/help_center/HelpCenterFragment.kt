package com.example.chargehoodapp.presentation.help_center

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.databinding.FragmentHelpCenterBinding

class HelpCenterFragment: Fragment() {

    private var binding: FragmentHelpCenterBinding?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHelpCenterBinding.inflate(inflater, container, false)


        return binding?.root
    }

}