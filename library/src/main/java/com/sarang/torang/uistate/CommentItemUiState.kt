package com.sarang.torang.uistate

import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.User
import kotlin.random.Random


data class CommentsUiState(
    val reviewId: Int? = null,
    val list: List<Comment> = listOf(),
    val error: String? = null,
    val comment: String = "",
    val movePosition: Int? = null,
    val reply: Comment? = null,
    val writer: User? = null

)

val CommentsUiState.toComment: Comment
    get() {
        return Comment(
            userId = this.writer?.userId ?: 0,
            profileImageUrl = this.writer?.profileUrl ?: "",
            comment = this.comment,
            date = "",
            name = this.writer?.userName ?: "",
            isUploading = true,
            commentLikeCount = 0,
            parentCommentId = this.reply?.commentsId
        )
    }

val CommentsUiState.isLogin get() = writer?.userId != null