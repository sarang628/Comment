package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.Comment
import kotlinx.coroutines.flow.Flow

interface GetCommentsUseCase {
    suspend fun invoke(reviewId: Int): Flow<List<Comment>>
}