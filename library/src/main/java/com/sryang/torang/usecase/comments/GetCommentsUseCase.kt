package com.sryang.torang.usecase.comments

import com.sryang.torang.data.comments.Comment

interface GetCommentsUseCase {
    suspend fun invoke(reviewId : Int): List<Comment>
}