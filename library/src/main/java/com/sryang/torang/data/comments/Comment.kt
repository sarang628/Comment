package com.sryang.torang.data.comments

data class Comment(
    val commentsId: Int = Integer.MAX_VALUE,
    val userId: Int,
    val profileImageUrl: String,
    val date: String,
    val comment: String,
    val name: String,
    val likeCount: Int,
    val isUploading: Boolean = false
)

fun testComment(): Comment {
    return Comment(
        userId = 0,
        profileImageUrl = "1/2023-09-14/10_44_39_302.jpeg",
        date = "2",
        comment = "3",
        name = "4",
        likeCount = 5
    )
}