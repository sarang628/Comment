package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.Comment

interface SendReplyUseCase {
    suspend fun invoke(reviewId: Int, parentCommentId: Long, comment: String, tagUserId : Int? = null): Comment
}