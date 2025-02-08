package com.example.chargehoodapp.presentation.help_center

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.HelpCenterItem
import com.example.chargehoodapp.databinding.FragmentHelpCenterBinding
import com.example.chargehoodapp.presentation.help_center.adapter.HelpCenterAdapter
import com.example.chargehoodapp.presentation.help_center.adapter.ExpandAnswerListener
import com.example.chargehoodapp.utils.extensions.HelpCenterUtils

class HelpCenterFragment : Fragment() {

    private var binding: FragmentHelpCenterBinding? = null
    private var helpCenterAdapter: HelpCenterAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        binding?.phoneButton?.setOnClickListener {
            openSmsApp("0509610961", "Hello, I need help with my account.")
        }

        binding?.emailButton?.setOnClickListener {
            sendMail("help@chargehood.com", "Help with my account", "Hello, I need help with my account.")
        }

        recyclerView = binding?.faqRecyclerView
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Load Help Center data -  list of key-value pairs
        val helpCenterList: List<HelpCenterItem> = HelpCenterUtils.loadHelpCenterData(requireContext())
        Log.d("TAG", "HelpCenterFragment - Loaded Help Center Data: $helpCenterList")

        // Set up RecyclerView
        helpCenterAdapter = HelpCenterAdapter(helpCenterList, object: ExpandAnswerListener {
            override fun onExpandAnswer(item: HelpCenterItem) {
                Log.d("TAG", "HelpCenterFragment - onExpandAnswer: $item")
                item.isExpanded = false
                helpCenterAdapter?.notifyItemChanged(helpCenterList.indexOf(item))
            }

            override fun onCollapseAnswer(item: HelpCenterItem) {
                item.isExpanded = true
                helpCenterAdapter?.notifyItemChanged(helpCenterList.indexOf(item))
            }
        })

        recyclerView?.adapter = helpCenterAdapter
    }

    private fun openSmsApp(phoneNumber: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber")).apply {
                putExtra("sms_body", message)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to open messaging app.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMail(email: String, subject: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to open email app.", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

