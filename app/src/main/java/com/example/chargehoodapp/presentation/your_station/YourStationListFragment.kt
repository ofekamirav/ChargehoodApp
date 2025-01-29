package com.example.chargehoodapp.presentation.your_station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chargehoodapp.R
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.YourStationRecycledListBinding
import com.example.chargehoodapp.presentation.your_station.adapter.OnStationClick
import com.example.chargehoodapp.presentation.your_station.adapter.YourStationListAdapter

class YourStationListFragment : Fragment(), OnStationClick {

    private var _binding: YourStationRecycledListBinding? = null
    private val binding get() = _binding!!

    private var viewModel: YourStationListViewModel?=null
    private lateinit var adapter: YourStationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = YourStationRecycledListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(YourStationListViewModel::class.java)

        setupRecyclerView()

        // Observe data from ViewModel
        viewModel?.stations?.observe(viewLifecycleOwner) { stations ->
            adapter.updateStations(stations)
        }

        binding.AddButton.setOnClickListener {
            findNavController().navigate(R.id.action_yourStationListFragment_to_addStationFragment)
        }

    }

    private fun setupRecyclerView() {
        adapter = YourStationListAdapter(listener = this)
        binding.recyclerViewChargingStations.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewChargingStations.adapter = adapter
    }

//    private suspend fun fetchStations() {
//        viewModel?.lowDataStations()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStationClick(station: ChargingStation) {
        // Handle station click (e.g., open details page)
    }
}
