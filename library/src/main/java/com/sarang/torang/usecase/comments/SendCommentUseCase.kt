package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.Comment

interface SendCommentUseCase {
    suspend fun invoke(reviewId: Int, comment: String, tagUserId: Int? = null): Comment
}