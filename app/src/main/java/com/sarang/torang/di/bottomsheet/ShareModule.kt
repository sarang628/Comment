package com.sryang.torangbottomsheet.di.bottomsheet

import androidx.compose.runtime.Composable
import com.sarang.torang.BuildConfig
import com.sarang.torang.api.ApiProfile
import com.sarang.torang.data.dao.LoggedInUserDao
import com.sarang.torang.session.SessionService
import com.sryang.torang.compose.bottomsheet.share.ShareBottomSheetDialog
import com.sryang.torang.data.User
import com.sryang.torang.usecase.GetFollowerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class ShareModule {
    @Provides
    fun providesGetFollowerUseCase(
        apiProfile: ApiProfile,
        loggedInUserDao: LoggedInUserDao,
        sessionService: SessionService
    ): GetFollowerUseCase {
        return object : GetFollowerUseCase {
            override suspend fun invoke(): List<User> {
                val token = sessionService.getToken() ?: throw Exception("로그인을 해주세요.")

                return apiProfile.getMyFollowing(token).map {
                    User(userId = it.followerId, userName = it.userName, picture = it.profilePicUrl)
                }
            }
        }
    }
}

fun provideShareBottomSheetDialog(): @Composable (onClose: () -> Unit) -> Unit = {
    ShareBottomSheetDialog(
        profileServerUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL,
        isExpand = true,
        onSelect = {},
        onClose = it
    )
}
