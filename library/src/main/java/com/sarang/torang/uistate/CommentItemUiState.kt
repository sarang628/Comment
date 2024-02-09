package com.sarang.torang.uistate

import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.User


data class CommentsUiState(
    val reviewId: Int? = null,
    val list: List<Comment> = listOf(),
    val snackBarMessage: String? = null,
    val comment: String = "",
    val movePosition: Int? = null,
    val reply: Comment? = null,
    val writer: User? = null,
    val uploadingComment: Comment? = null
)

val CommentsUiState.selectedIndex: Int get() = list.indexOf(list.find { it.commentsId == reply?.commentsId })
val CommentsUiState.selectedReplyIndex: Int get() = list.indexOf(list.find { it.commentsId == uploadingComment?.parentCommentId })

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
            parentCommentId = this.reply?.commentsId // 새 코멘트인지, 답장인지 구분 값
        )
    }

val CommentsUiState.isLogin get() = writer?.userId != null