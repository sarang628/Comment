package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.Comment

interface LoadMoreUseCase {
    suspend fun invoke(commentId: Int)
}