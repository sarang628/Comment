package com.sarang.torang.uistate

import com.sarang.torang.data.comments.Comment


data class CommentsUiState(
    val reviewId: Int? = null,
    val list: List<Comment> = listOf(),
    val name: String = "",
    val profileImageUrl: String = "",
    val error: String? = null,
    val comment: String = "",
    val onTop: Boolean = false,
    val myId: Int? = null
)

val CommentsUiState.isLogin get() = myId != null