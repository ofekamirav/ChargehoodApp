package com.example.chargehoodapp.data.model

// data class model to represent a question-answer pair

data class HelpCenterItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)
