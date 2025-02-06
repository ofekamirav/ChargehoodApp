package com.example.chargehoodapp.presentation.your_station

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.R
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.YourStationRecycledListBinding
import com.example.chargehoodapp.databinding.YourStationsListRowBinding
import com.example.chargehoodapp.presentation.charging_station_details.ChargingStationDetailsFragment
import com.example.chargehoodapp.presentation.owner_station_details.OwnerStationDetailsFragment
import com.example.chargehoodapp.presentation.your_station.adapter.OnDeleteClick
import com.example.chargehoodapp.presentation.your_station.adapter.OnStationClick
import com.example.chargehoodapp.presentation.your_station.adapter.YourStationListAdapter

class YourStationListFragment : Fragment() {

    private var adapter: YourStationListAdapter?= null

    private var binding: YourStationRecycledListBinding? = null
    private var viewModel: YourStationListViewModel?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = YourStationRecycledListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[YourStationListViewModel::class.java]

        //Initialize the recycler view
        binding?.recyclerViewChargingStations?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }


        //Handle station click or station delete
        adapter = YourStationListAdapter(emptyList(), object : OnStationClick {
            override fun onStationClick(station: ChargingStation) {
                val dialogFragment = OwnerStationDetailsFragment()
                MyApplication.Globals.selectedStation = station
                dialogFragment.show(parentFragmentManager, "OwnerStationDetailsFragment")
            }
        }, object : OnDeleteClick {
            override fun onDeleteClick(station: ChargingStation) {
                showDeleteConfirmationDialog(station)
            }
        })


        binding?.recyclerViewChargingStations?.adapter = adapter


        binding?.AddButton?.setOnClickListener {
            findNavController().navigate(R.id.action_yourStationListFragment_to_addStationFragment)
        }

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        observeViewModel()
    }

    //Show delete confirmation dialog
    private fun showDeleteConfirmationDialog(station: ChargingStation) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Station")
            .setMessage("Are you sure you want to delete this station?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel?.deleteStation(station)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //Update UI based on the station list
    private fun observeViewModel() {
        viewModel?.stations?.observe(viewLifecycleOwner) { stationList ->
            Log.d("TAG", "YourStationListFragment-Observed stations: ${stationList.size}")
            adapter?.setStations(stationList ?: emptyList())
            updateUI(stationList.isNullOrEmpty())
        }


        viewModel?.isEmpty?.observe(viewLifecycleOwner) { isEmpty ->
            updateUI(isEmpty)
        }
    }

    private fun updateUI(isEmpty: Boolean) {
        binding?.apply {
            recyclerViewChargingStations.visibility = if (isEmpty) View.GONE else View.VISIBLE
            emptyStateTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel?.refreshChargingStations()
    }


}
