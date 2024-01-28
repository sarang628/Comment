package com.sarang.torang.usecase.comments

interface DeleteCommentUseCase {
    suspend fun delete(commentId: Int)
}