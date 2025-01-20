package com.example.chargehoodapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.chargehoodapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding?=null
    private var drawerLayout: DrawerLayout? = null
    private var hamburgerButton: ImageButton? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentProfileBinding.inflate(inflater,container,false)

        //Set the hamburger button to open the drawer
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout)
        hamburgerButton = binding?.hamburgerButton
        hamburgerButton?.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }


        return binding?.root
    }


}