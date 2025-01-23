package com.example.chargehoodapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.databinding.WelcomeFragmentBinding

class WelcomeFragment: Fragment() {

    private var binding: WelcomeFragmentBinding?=null
    private var chargingGif: ImageView?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=WelcomeFragmentBinding.inflate(inflater,container,false)

        //set the gif image
        chargingGif=binding?.chargingGif
        Glide.with(this)
            .asGif()
            .load(R.drawable.welcome)
            .into(chargingGif!!)


        binding?.GetStartedButton?.setOnClickListener{
            val action= WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        return binding?.root
    }

    //hide the action bar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}