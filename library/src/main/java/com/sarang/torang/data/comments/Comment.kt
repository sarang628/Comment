package com.sarang.torang.data.comments

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

data class Comment(
    val commentsId: Long = System.currentTimeMillis(),
    val userId: Int,
    val profileImageUrl: String,
    val date: String,
    val comment: String,
    val name: String,
    val isUploading: Boolean = false,
    val commentLikeId: Int? = null,
    val commentLikeCount: Int,
    val parentCommentId: Long? = null,
    val subCommentCount: Int? = null,
    val tagUser: TagUser? = null
)

val Comment.favoriteIcon: ImageVector get() = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
val Comment.isFavorite: Boolean get() = commentLikeId != null
val Comment.isSubComment: Boolean get() = (parentCommentId != null && parentCommentId != 0L)
fun Comment.transFormComment(color: Color = Color(0xFF0000EE)): AnnotatedString {
    return if (tagUser != null) buildAnnotatedString {
        withStyle(
            SpanStyle(color = color, fontWeight = FontWeight.Bold)
        ) {
            append("@${this@transFormComment.tagUser.userName}")
        }
        append(" ")
        append("${this@transFormComment.comment}")
    } else AnnotatedString(this.comment)
}

fun testComment(commentId: Long = 0): Comment {
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
        subCommentCount = 100,
        tagUser = TagUser(0, "tagUser")
    )
}

fun testSubComment(commentId: Long = 0): Comment {
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