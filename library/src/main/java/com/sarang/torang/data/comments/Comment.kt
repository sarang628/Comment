package com.sarang.torang.data.comments

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.vector.ImageVector

data class Comment(
    val commentsId: Int = Integer.MAX_VALUE,
    val userId: Int,
    val profileImageUrl: String,
    val date: String,
    val comment: String,
    val name: String,
    val isUploading: Boolean = false,
    val commentLikeId: Int? = null,
    val commentLikeCount: Int,
    val parentCommentId: Int? = null,
    val subCommentCount: Int? = null
)

val Comment.favoriteIcon: ImageVector get() = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
val Comment.isFavorite: Boolean get() = commentLikeId != null

val Comment.isSubComment: Boolean get() = parentCommentId != null

fun testComment(commentId: Int = 0): Comment {
    return Comment(
        commentsId = commentId,
        userId = 0,
        profileImageUrl = "1/2023-09-14/10_44_39_302.jpeg",
        date = "1d",
        comment = "comment comment comment comment comment comment comment comment comment comment comment comment comment comment comment comment",
//        comment = "comment",
        name = "name",
        isUploading = false,
        commentLikeCount = 20,
        subCommentCount = 100
    )
}

fun testSubComment(commentId: Int = 0): Comment {
    return Comment(
        commentsId = commentId,
        userId = 0,
        profileImageUrl = "1/2023-09-14/10_44_39_302.jpeg",
        date = "1d",
        comment = "comment comment comment comment comment comment comment comment comment comment comment comment comment comment comment comment",
//        comment = "comment",
        name = "name",
        isUploading = false,
        commentLikeCount = 20,
        parentCommentId = 1
    )
}