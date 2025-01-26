package com.example.chargehoodapp.presentation.charging_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.chargehoodapp.databinding.FragmentChargingPageBinding

class ChargingPageFragment : Fragment() {

    private var binding: FragmentChargingPageBinding? = null
    private var viewModel: ChargingPageViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[ChargingPageViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChargingPageBinding.inflate(inflater, container, false)


        return binding?.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}