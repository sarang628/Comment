package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.User
import kotlinx.coroutines.flow.Flow

interface GetUserUseCase {
    suspend fun invoke(): Flow<User?>
}