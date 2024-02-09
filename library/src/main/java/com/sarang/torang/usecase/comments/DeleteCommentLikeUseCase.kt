package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.Comment

/**
 * Request comment like/delete like
 */
interface DeleteCommentLikeUseCase {
    suspend fun invoke(commentId: Long): Boolean
}