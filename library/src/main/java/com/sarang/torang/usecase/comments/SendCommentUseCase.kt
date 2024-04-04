package com.sarang.torang.usecase.comments

interface SendCommentUseCase {
    suspend fun invoke(
        reviewId: Int,
        comment: String,
        tagUserId: Int? = null,
        onLocalUpdated: () -> Unit
    )
}