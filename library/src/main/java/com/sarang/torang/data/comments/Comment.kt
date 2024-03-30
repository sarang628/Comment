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

/**
 * Comment
 * @param commentsId lazyColunm에 key값으로 사용 어떤 용도인지 까먹음
 * @param userId 사용자 ID swipe로 글을 지울 때 내 글만 삭제 요청을 하기위해 사용
 * @param profileImageUrl 프로필 이미지 url
 * @param date comment 작성일
 * @param name 작성자명
 * @param isUploading 업로드 중인지
 * @param commentLikeCount 좋아요 개수
 * @param subCommentCount 대댓글 개수
 * @param tagUser 대댓글 태그된 사용자
 */
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

val Comment.isRoot: Boolean get() = parentCommentId == null || parentCommentId == 0L

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