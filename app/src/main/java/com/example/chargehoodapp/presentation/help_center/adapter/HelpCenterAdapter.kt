package com.example.chargehoodapp.presentation.help_center.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.databinding.HelpCenterListRowBinding
import com.example.chargehoodapp.data.model.HelpCenterItem

class HelpCenterAdapter(private var helpCenterList: List<HelpCenterItem>) :
    RecyclerView.Adapter<HelpCenterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpCenterViewHolder {
        val binding = HelpCenterListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HelpCenterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HelpCenterViewHolder, position: Int) {
        holder.bind(helpCenterList[position]) {
            helpCenterList[position].isExpanded = !helpCenterList[position].isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = helpCenterList.size
}
