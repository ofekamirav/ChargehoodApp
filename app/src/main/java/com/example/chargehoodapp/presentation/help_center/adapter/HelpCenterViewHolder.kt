package com.example.chargehoodapp.presentation.help_center.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.HelpCenterItem
import com.example.chargehoodapp.databinding.HelpCenterListRowBinding

class HelpCenterViewHolder(private val binding: HelpCenterListRowBinding, private val listener: ExpandAnswerListener?) : RecyclerView.ViewHolder(binding.root) {
    private var item: HelpCenterItem? = null

    fun bind(item: HelpCenterItem) {
        this.item = item
        binding.questionText.text = item.question
        binding.answerText.text = item.answer

        // ✅ Ensure correct visibility based on `isExpanded`
        binding.answerText.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

        binding.expandIcon.setOnClickListener {
            if (item.isExpanded) {
                listener?.onExpandAnswer(item)
            } else {
                listener?.onCollapseAnswer(item)
            }
        }
    }
}

