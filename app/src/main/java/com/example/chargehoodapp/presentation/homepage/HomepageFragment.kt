package com.example.chargehoodapp.presentation.homepage

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.chargehoodapp.databinding.HomepageFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

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
