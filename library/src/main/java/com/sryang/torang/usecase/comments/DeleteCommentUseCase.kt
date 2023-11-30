package com.sryang.torang.usecase.comments

interface DeleteCommentUseCase {
    suspend fun delete(commentId: Int)
}