package com.example.chargehoodapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import android.Manifest
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.databinding.HomepageFragmentBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class HomepageFragment : Fragment(), OnMapReadyCallback {

    private var binding: HomepageFragmentBinding? = null
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var drawerLayout: DrawerLayout? = null
    private var hamburger_button: ImageButton? = null
    private var profile_button: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomepageFragmentBinding.inflate(inflater, container, false)

        //setup the map
        mapView = binding?.mapView
        mapView?.onCreate(savedInstanceState)

        //initialize the map- call the onMapReady callback
        mapView?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        //Set the hamburger button to open the drawer
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout)
        hamburger_button = binding?.hamburgerButton
        hamburger_button?.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }

        //Set the profile button
        profile_button = binding?.profileButton
        profile_button?.setOnClickListener {
            val action = HomepageFragmentDirections.actionHomepageFragmentToProfileFragment()
            findNavController().navigate(action)
        }



        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        mapView?.onDestroy()
    }

    //------------------------------------------------------------------------------------------------//
    //enable location permission from device
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            enableMyLocation()
        }
    }

    //If first time, request permission to using location
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }
//-----------------------------------------------------------------------------------------------------------

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    //Set the map settings
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        checkLocationPermission()

        // Enable location button
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //Hide the default button
            googleMap?.uiSettings?.isMyLocationButtonEnabled = false

            //Set the current location on focus
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    binding?.myLocationButton?.setOnClickListener{
                        googleMap?.isMyLocationEnabled=true
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    }
                }
            }
        }

        //Enable zoom controls
        binding?.zoomInButton?.setOnClickListener{
            googleMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }
        binding?.zoomOutButton?.setOnClickListener{
            googleMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressbar = activity?.findViewById<View>(R.id.progressBar)
        progressbar?.visibility = View.GONE
    }


}