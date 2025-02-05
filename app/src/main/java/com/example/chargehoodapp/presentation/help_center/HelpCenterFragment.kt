package com.example.chargehoodapp.presentation.help_center

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chargehoodapp.data.model.HelpCenterItem
import com.example.chargehoodapp.databinding.FragmentHelpCenterBinding
import com.example.chargehoodapp.presentation.help_center.adapter.HelpCenterAdapter
import com.example.chargehoodapp.presentation.help_center.utils.HelpCenterUtils

class HelpCenterFragment : Fragment() {

    private var binding: FragmentHelpCenterBinding? = null
    private lateinit var helpCenterAdapter: HelpCenterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Load Help Center data -  list of key-value pairs
        val helpCenterList: List<HelpCenterItem> = HelpCenterUtils.loadHelpCenterData(requireContext())
        Log.d("TAG", "HelpCenterFragment - Loaded Help Center Data: $helpCenterList")

        // Set up RecyclerView
        helpCenterAdapter = HelpCenterAdapter(helpCenterList)
        binding?.faqRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = helpCenterAdapter
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
