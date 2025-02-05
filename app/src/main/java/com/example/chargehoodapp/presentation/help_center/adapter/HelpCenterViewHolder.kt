package com.example.chargehoodapp.presentation.help_center.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.HelpCenterItem
import com.example.chargehoodapp.databinding.HelpCenterListRowBinding

class HelpCenterViewHolder(private val binding: HelpCenterListRowBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HelpCenterItem, onClick: (Int) -> Unit) {
        // Set initial visibility based on `isExpanded`
        binding.questionText.visibility = if (item.isExpanded) View.GONE else View.VISIBLE
        binding.answerText.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

        binding.expandIcon.setOnClickListener {
//            item.isExpanded = !item.isExpanded // Toggle state
//
//            // Apply visibility changes
//            binding.questionText.visibility = if (item.isExpanded) View.GONE else View.VISIBLE
//            binding.answerText.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClick(position)
            }
//            onClick(bindingAdapterPosition) // Notify adapter to update the item
        }
    }
}
