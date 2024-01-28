package com.sarang.torang.usecase.comments

import com.sarang.torang.data.comments.User

interface GetUserUseCase {
    suspend fun invoke() : User
}