package com.example.chargehoodapp.presentation.help_center.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.databinding.HelpCenterListRowBinding
import com.example.chargehoodapp.data.model.HelpCenterItem

class HelpCenterAdapter(private var helpCenterList: List<HelpCenterItem>, private val Listener: ExpandAnswerListener?=null) :
    RecyclerView.Adapter<HelpCenterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpCenterViewHolder {
        val binding = HelpCenterListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HelpCenterViewHolder(binding, Listener)
    }

    override fun onBindViewHolder(holder: HelpCenterViewHolder, position: Int) {
        val item = helpCenterList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = helpCenterList.size
}
