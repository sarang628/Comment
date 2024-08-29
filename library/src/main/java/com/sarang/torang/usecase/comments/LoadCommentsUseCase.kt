package com.sarang.torang.usecase.comments

interface LoadCommentsUseCase {
    suspend fun invoke(reviewId: Int)
}