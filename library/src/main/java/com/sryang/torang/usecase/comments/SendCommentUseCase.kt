package com.sryang.torang.usecase.comments

import com.sryang.torang.data.comments.Comment

interface SendCommentUseCase {
    suspend fun invoke(reviewId: Int, comment: String) : Comment
}