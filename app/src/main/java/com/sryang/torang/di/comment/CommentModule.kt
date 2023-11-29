package com.sryang.torang.di.comment

import com.sryang.torang.data.comments.Comment
import com.sryang.torang.data.comments.User
import com.sryang.torang.usecase.comments.GetCommentsUseCase
import com.sryang.torang.usecase.comments.GetUserUseCase
import com.sryang.torang_repository.api.ApiComment
import com.sryang.torang_repository.data.dao.LoggedInUserDao
import com.sryang.torang_repository.session.SessionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.Exception

@InstallIn(SingletonComponent::class)
@Module
class CommentModule {
    @Provides
    fun ProvidesGetCommentsUseCase(
        apiComment: ApiComment,
        sessionService: SessionService
    ): GetCommentsUseCase {
        return object : GetCommentsUseCase {
            override suspend fun invoke(reviewId: Int): List<Comment> {
                val auth = sessionService.getToken()
                if (auth != null) {
                    return apiComment.getComments(auth = auth, reviewId = reviewId).list.map {
                        Comment(
                            name = it.user.userName,
                            comment = it.comment,
                            date = it.create_date,
                            likeCount = 0,
                            profileImageUrl = it.user.profilePicUrl,
                            userId = it.user.userId
                        )
                    }
                } else {
                    throw Exception("로그인을 해주세요.")
                }

            }
        }
    }

    @Provides
    fun ProvidesGetUserUseCase(
        loggedInUserDao: LoggedInUserDao
    ): GetUserUseCase {
        return object : GetUserUseCase {
            override suspend fun invoke(): User {
                val user = loggedInUserDao.getLoggedInUser1()
                if (user != null) {
                    return User(
                        user.profilePicUrl ?: ""
                    )
                } else {
                    throw Exception("로그인을 해주세요.")
                }
            }
        }
    }
}