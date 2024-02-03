package com.sarang.torang.data.comments

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.Color
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
    val commentLikeCount : Int
)

val Comment.background: Color get() = if (isUploading) Color.LightGray else Color.Transparent
val Comment.favoriteIcon: ImageVector get() = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
val Comment.isFavorite: Boolean get() = commentLikeId != null

fun testComment(): Comment {
    return Comment(
        userId = 0,
        profileImageUrl = "1/2023-09-14/10_44_39_302.jpeg",
        date = "1d",
        comment = "comment comment comment comment comment comment comment comment comment comment comment comment comment comment comment comment",
//        comment = "comment",
        name = "name",
        isUploading = false,
        commentLikeCount = 20
    )
}