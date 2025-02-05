package com.example.chargehoodapp.presentation.help_center.adapter

import com.example.chargehoodapp.data.model.HelpCenterItem

interface ExpandAnswerListener {
    fun onExpandAnswer(item: HelpCenterItem)

    fun onCollapseAnswer(item: HelpCenterItem)
}