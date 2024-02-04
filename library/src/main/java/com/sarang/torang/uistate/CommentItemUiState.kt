package com.sarang.torang.uistate

import com.sarang.torang.data.comments.Comment


data class CommentsUiState(
    val reviewId: Int? = null,
    val list: List<Comment> = listOf(),
    val name: String = "", // 코멘트 입력 시 화면에 선반영되는데 닉네임 필요
    val profileImageUrl: String = "",
    val error: String? = null,
    val comment: String = "",
    val onTop: Boolean = false,
    val myId: Int? = null,
    val reply: Comment? = null
)

val CommentsUiState.isLogin get() = myId != null