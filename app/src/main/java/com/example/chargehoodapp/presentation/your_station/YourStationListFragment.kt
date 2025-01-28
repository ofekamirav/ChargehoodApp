package com.example.chargehoodapp.presentation.your_station


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.YourStationRecycledListBinding
import com.example.chargehoodapp.presentation.your_station.adapter.YourStationListAdapter
import com.example.chargehoodapp.presentation.your_station.adapter.OnStationClick

class YourStationListFragment : Fragment(), OnStationClick {

    private var _binding: YourStationRecycledListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: YourStationListViewModel by viewModels()
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

        setupRecyclerView()

        // Observe data from ViewModel
        viewModel.stations.observe(viewLifecycleOwner) { stations ->
            adapter.updateStations(stations)
        }

        // Example: Fetch data (replace this with Firebase or Room DB)
        fetchStations()
    }

    private fun setupRecyclerView() {
        adapter = YourStationListAdapter(listener = this)
        binding.recyclerViewChargingStations.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewChargingStations.adapter = adapter
    }

    private fun fetchStations() {
        val sampleStations = listOf(
            ChargingStation(
                id = "1",
                ownerId = "user1",
                latitude = 32.0853,
                longitude = 34.7818,
                addressName = "Tel Aviv - Rothschild Blvd",
                connectionType = "Type 2",
                chargingSpeed = "50 kW",
                availability = true,
                imageUrl = "",
                pricePerkW = 1.2,
                wazeUrl = "https://waze.com",
                lastUpdated = System.currentTimeMillis()
            ),
            ChargingStation(
                id = "2",
                ownerId = "user2",
                latitude = 31.7683,
                longitude = 35.2137,
                addressName = "Jerusalem - Jaffa Street",
                connectionType = "CCS",
                chargingSpeed = "100 kW",
                availability = false,
                imageUrl = "",
                pricePerkW = 1.5,
                wazeUrl = "https://waze.com",
                lastUpdated = System.currentTimeMillis()
            )
        )

        viewModel.setStations(sampleStations)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStationClick(station: ChargingStation) {
        // Handle station click (e.g., open details page)
    }
}
