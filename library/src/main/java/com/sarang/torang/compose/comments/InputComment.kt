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
import com.sarang.torang.data.comments.User
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.data.comments.testSubComment
import com.sarang.torang.uistate.Comments
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.isLogin
import com.sarang.torang.uistate.isUploading

@Composable
fun InputComment(
    uiState: Comments,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit,
    onClearReply: (() -> Unit)?,
    requestFocus: Boolean = false,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit
) {
    Column {
        if (uiState.reply != null)
            ReplyComment(
                profileImageUrl = uiState.reply.profileImageUrl,
                uiState.reply.name,
                onClearReply,
                image = image
            )

        HorizontalDivider(
            modifier = Modifier.layoutId("divide"),
            color = Color.LightGray
        )

        CommentTextField(
            modifier = Modifier.layoutId("inputComment"),
            profileImageUrl = uiState.writer?.profileUrl ?: "",
            onSend = { sendComment() },
            name = if (uiState.isLogin) "Add a comment for ${uiState.writer?.userName ?: ""}" else "로그인을 해주세요",
            input = uiState.comment,
            onValueChange = { onCommentChange(it) },
            replyName = uiState.reply?.name,
            isUploading = uiState.isUploading,
            requestFocus = requestFocus,
            image = image,
            enabled = uiState.isLogin
        )
    }
}

@Preview
@Composable
fun PreviewInputComment() {
    InputComment(/*Preview*/
        uiState = Comments().copy(
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