package com.sryang.torang.usecase.comments

import com.sryang.torang.data.comments.User

interface GetUserUseCase {
    suspend fun invoke() : User
}