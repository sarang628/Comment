package com.sarang.torang.compose.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.sarang.torang.compose.comments.CommentTextField
import com.sarang.torang.compose.comments.ReplyComment
import com.sarang.torang.data.comments.User
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.data.comments.testSubComment
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.isLogin
import com.sarang.torang.uistate.isUploading

@Composable
fun InputComment(
    uiState: CommentsUiState,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit,
    onClearReply: (() -> Unit)?,
    requestFocus: Boolean = false,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit
) {
    Column {
        if (uiState.reply != null)
            ReplyComment(
                profileImageUrl = uiState.reply!!.profileImageUrl,
                uiState.reply!!.name,
                onClearReply,
                image = image
            )

        if (uiState.isLogin)
            HorizontalDivider(
                modifier = Modifier.layoutId("divide"),
                color = Color.LightGray
            )

        if (uiState.isLogin)
            CommentTextField(
                modifier = Modifier.layoutId("inputComment"),
                profileImageUrl = uiState.writer?.profileUrl ?: "",
                onSend = { sendComment() },
                name = uiState.writer?.userName ?: "",
                input = uiState.comment,
                onValueChange = { onCommentChange(it) },
                replyName = uiState.reply?.name,
                isUploading = uiState.isUploading,
                requestFocus = requestFocus,
                image = image
            )
    }
}

@Preview
@Composable
fun PreviewInputComment() {
    InputComment(/*Preview*/
        uiState = CommentsUiState().copy(
            list = arrayListOf(
                testComment(0),
                testComment(1),
                testComment(2),
                testSubComment(9),
                testSubComment(10),
                testSubComment(11),
                testComment(3),
                testComment(4),
                testComment(5),
                testComment(6),
                testComment(7),
                testComment(8),
            ),
            writer = User("", 10, ""),
            reply = testComment()
        ), sendComment = { /*TODO*/ },
        onClearReply = {}, onCommentChange = {}, image = { _, _, _, _, _ -> })
}