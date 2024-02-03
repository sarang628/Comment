package com.sarang.torang.usecase.comments

/**
 * Request comment like/delete like
 */
interface AddCommentLikeUseCase {
    suspend fun invoke(commentId: Int): Int
}