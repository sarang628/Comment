package com.sarang.torang.compose.comments

import kotlinx.coroutines.flow.Flow

/**
 * 로그인 여부를 관찰하는 UseCase
 */
interface IsLoginFlowForCommentUseCase {
    val isLogin: Flow<Boolean> // 로그인 여부
}