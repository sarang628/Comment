package com.sarang.torang.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sarang.torang.usecase.comments.GetCommentsUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

// AndroidJUnit4를 사용하여 테스트 실행
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EmailLoginUseCaseTest {

    // Hilt를 사용한 의존성 주입 설정
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // EmailLoginUseCase를 주입 받음
    @Inject
    lateinit var getCommentUseCase: GetCommentsUseCase

    // 테스트 전에 실행되는 초기화 함수
    @Before
    fun init() {
        // Hilt를 통해 의존성 주입
        hiltRule.inject()
    }

    // 로그인 성공 테스트
    @Test
    fun successLoginTest() {
        runBlocking {
            // 이메일 및 비밀번호 설정 // 로그인 시도
            try {
                getCommentUseCase.invoke(476)
                // 일정 시간 대기 (네트워크 요청 가정)
                delay(3000)
            } catch (e: Exception) {
                // 에러 메시지 확인
                Assert.assertEquals("", e.message)
            }
        }
    }

}