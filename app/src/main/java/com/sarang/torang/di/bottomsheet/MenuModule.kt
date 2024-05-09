package com.sarang.torang.di.bottomsheet

import android.util.Log
import androidx.compose.runtime.Composable
import com.sarang.torang.data.dao.LoggedInUserDao
import com.sarang.torang.data.dao.ReviewDao
import com.sryang.torang.compose.bottomsheet.feed.FeedMenuBottomSheetDialog
import com.sryang.torang.usecase.IsMyReviewUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class MenuModule {
    @Provides
    fun ProvidesGetReviewUseCase(
        reviewDao: ReviewDao,
        loggedInUserDao: LoggedInUserDao
    ): IsMyReviewUseCase {
        return object : IsMyReviewUseCase {
            override suspend fun invoke(reviewId: Int): Boolean {
                try {
                    val review = reviewDao.getFeedbyReviewId(reviewId = reviewId)
                    Log.d("_MenuModule", review.toString())
                    val userId = loggedInUserDao.getLoggedInUser1()?.userId
                    Log.d("_MenuModule", "userId : ${userId}")
                    return review.userId == userId
                } catch (e: Exception) {
                    Log.e("_MenuModule", e.message.toString())
                }
                return false
            }
        }
    }
}

fun provideFeedMenuBottomSheetDialog(): @Composable (reviewId: Int, onClose: () -> Unit, onReport: (Int) -> Unit, onDelete: (Int) -> Unit, onEdit: (Int) -> Unit) -> Unit =
    { reviewId, onClose, onReport, onDelete, onEdit ->
        FeedMenuBottomSheetDialog(
            isExpand = true,
            reviewId = reviewId,
            onReport = { onReport.invoke(reviewId) },
            onDelete = { onDelete.invoke(reviewId) },
            onEdit = { onEdit.invoke(reviewId) },
            onClose = onClose
        )
    }