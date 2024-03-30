package com.sarang.torang

import android.util.Log
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.findRootCommentId
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun commentUiState() {
        val commentUiState = CommentsUiState(
            list = listOf(
                testComment().copy(commentsId = 1),
                testComment().copy(commentsId = 7, parentCommentId = 1),
                testComment().copy(commentsId = 8, parentCommentId = 1),
                testComment().copy(commentsId = 9, parentCommentId = 1),
                testComment().copy(commentsId = 10, parentCommentId = 1),
                testComment().copy(commentsId = 2),
                testComment().copy(commentsId = 3),
                testComment().copy(commentsId = 4),
                testComment().copy(commentsId = 5),
                testComment().copy(commentsId = 6),
            )
        )
        System.out.println(
            commentUiState.findRootCommentId(commentUiState.list[0])
        )
        System.out.println(
            commentUiState.findRootCommentId(commentUiState.list[4])
        )
        Log.d(
            "__sryang",
            commentUiState.list.indexOf(
                commentUiState.list.findLast { it.parentCommentId == 1L }
            ).toString()
        )

    }
}