package com.sryang.torang.uistate

import com.sryang.torang.data.comments.Comment


data class CommentsUiState(
    val list: List<Comment> = listOf(),
    val name: String = "",
    val profileImageUrl: String = "",
    val error: String? = null
)