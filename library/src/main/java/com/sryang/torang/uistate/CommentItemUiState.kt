package com.sryang.torang.uistate

data class CommentItemUiState(
    val userId: Int,
    val profileImageUrl: String,
    val date: String,
    val comment: String,
    val name: String,
    val likeCount: Int
)

fun testCommentItemUiState(): CommentItemUiState {
    return CommentItemUiState(
        userId = 0,
        profileImageUrl = "1/2023-09-14/10_44_39_302.jpeg",
        date = "2",
        comment = "3",
        name = "4",
        likeCount = 5
    )
}