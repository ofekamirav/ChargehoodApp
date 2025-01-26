package com.example.chargehoodapp.presentation.homepage

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.R
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.HomepageFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomepageFragment : Fragment(), OnMapReadyCallback {

    private var binding: HomepageFragmentBinding? = null
    private var viewModel: HomepageViewModel? = null
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[HomepageViewModel::class.java]
        viewModel?.initLocationProvider(requireContext())

        // Observe location permission
        viewModel?.locationPermissionGranted?.observe(viewLifecycleOwner) { granted ->
            if (granted) {
                enableMyLocation()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            }
        }

        // Observe current location
        viewModel?.currentLocation?.observe(viewLifecycleOwner) { location ->
            location?.let {
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            }
        }


        binding?.hamburgerButton?.setOnClickListener {
            val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding?.profileButton?.setOnClickListener {
            findNavController().navigate(HomepageFragmentDirections.actionHomepageFragmentToProfileFragment())
        }

        binding?.myLocationButton?.setOnClickListener {
            viewModel?.updateLocation(requireContext())
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomepageFragmentBinding.inflate(inflater, container, false)

        // Initialize MapView
        mapView = binding?.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)


        binding?.addDummyButton?.setOnClickListener {
            viewModel?.addDummyStation { station ->
                station?.let {
                    addStationMarker(it)
                    Log.d("TAG", "Dummy station added to map: ${it.id}")
                } ?: Log.e("TAG", "Failed to add dummy station")
            }
        }




        return binding?.root
    }

    //handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to display your current location.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //Check if location permission is granted and enable the location layer
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            Log.d("TAG", "HomepageFragment-My location enabled status: ${googleMap?.isMyLocationEnabled}")
            viewModel?.updateLocation(requireContext())
        }
        else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    //After the map is loaded, check if permission is granted and enable the location layer
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        viewModel?.syncStations()

        // Add markers to the map when data is ready
        viewModel?.chargingStations?.observe(viewLifecycleOwner) { stations ->
            stations?.forEach { station ->
                Log.d("TAG", "HomepageFragment-Adding marker for station: ${station.id}")
                addStationMarker(station)
            }
        }

        // Handle Marker click
        googleMap?.setOnMarkerClickListener { marker ->
            val station = marker.tag as? ChargingStation
            station?.let {
                Log.d("TAG", "HomepageFragment-Marker clicked: ${station.id}")
                val action = HomepageFragmentDirections.actionHomepageFragmentToChargingStationDetailsFragment()
                findNavController().navigate(action)
            }
            true
        }

        enableMyLocation()

        googleMap?.uiSettings?.isMyLocationButtonEnabled = false

        if (googleMap?.isMyLocationEnabled == true) {
            Log.d("TAG", "HomepageFragment-My location layer enabled")
        } else {
            Log.d("TAG", "HomepageFragment-My location layer NOT enabled")
        }

        binding?.zoomInButton?.setOnClickListener {
            googleMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }
        binding?.zoomOutButton?.setOnClickListener {
            googleMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }

    //add custom marker to map
    private fun addStationMarker(station: ChargingStation) {
        val position = LatLng(station.latitude, station.longitude)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_location_marker)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 90, 120, false)

        val icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)
        val marker = googleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title(station.id) //title of marker is station id
                .icon(icon)
        )

        marker?.tag = station //set the station to marker
        Log.d("TAG", "Marker added for station: ${station.id}")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mapView?.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

}
