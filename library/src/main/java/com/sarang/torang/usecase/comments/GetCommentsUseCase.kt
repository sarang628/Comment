package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.Comment

interface GetCommentsUseCase {
    suspend fun invoke(reviewId : Int): List<Comment>
}