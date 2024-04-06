package com.sarang.torang.compose.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.User
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.data.comments.testSubComment
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.isLogin
import com.sarang.torang.uistate.isUploading
import com.sryang.torang.compose.bottomsheet.bottomsheetscaffold.TorangCommentBottomSheetScaffold

@Preview
@Composable
fun CommentBottomSheet() {
    TorangCommentBottomSheetScaffold(input = { PreviewInputCommentSticky() }, sheetContent = {
        PreviewCommentBody()
    }, sheetPeekHeight = 400.dp) {

    }
}

@Composable
fun CommentBottomSheetBody(
    modifier: Modifier = Modifier,
    uiState: CommentsUiState,
    onScrollTop: () -> Unit,
    onDelete: (Long) -> Unit,
    onUndo: (Long) -> Unit,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit,
    onFavorite: ((Long) -> Unit)? = null,
    onReply: ((Comment) -> Unit)? = null,
    onClearReply: (() -> Unit)? = null,
    onViewMore: ((Long) -> Unit)? = null
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize(),
        constraintSet = commentsBottomSheetConstraintSet()
    ) {
        Text(
            modifier = Modifier.layoutId("title"),
            text = "Comments",
            fontWeight = FontWeight.Bold
        )
        CommentHelp(Modifier.layoutId("commentHelp"))

        if (uiState.list.isEmpty()) {
            EmptyComment(Modifier.layoutId("itemCommentList"))
        } else {
            Comments(
                modifier = Modifier
                    .layoutId("itemCommentList")
                    .heightIn(min = 350.dp)
                    .fillMaxWidth(),
                list = uiState.list,
                movePosition = uiState.movePosition,
                onPosition = onScrollTop,
                onDelete = onDelete,
                onUndo = onUndo,
                onFavorite = onFavorite,
                onReply = onReply,
                myId = uiState.writer?.userId,
                onViewMore = onViewMore
            )
        }
    }
}

fun commentsBottomSheetConstraintSet(): ConstraintSet {
    return ConstraintSet {
        val title = createRefFor("title")
        val commentHelp = createRefFor("commentHelp")
        val itemCommentList = createRefFor("itemCommentList")
        val inputComment = createRefFor("inputComment")
        val divide = createRefFor("divide")
        val reply = createRefFor("reply")

        constrain(title) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
        }

        constrain(commentHelp) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(title.bottom)
        }

        constrain(itemCommentList) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(commentHelp.bottom)
            bottom.linkTo(inputComment.top)
            height = Dimension.fillToConstraints
        }

        constrain(inputComment) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }

        constrain(divide) {
            start.linkTo(parent.start, 8.dp)
            end.linkTo(parent.end, 8.dp)
            bottom.linkTo(inputComment.top)
        }

        constrain(reply) {
            bottom.linkTo(divide.top)
        }
    }
}

@Composable
fun InputCommentForSticky(
    uiState: CommentsUiState,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit,
    onClearReply: (() -> Unit)?
) {
    Column {
        if (uiState.reply != null)
            ReplyComment(
                profileImageUrl = uiState.reply.profileImageUrl,
                uiState.reply.name,
                onClearReply
            )

        if (uiState.isLogin)
            HorizontalDivider(
                modifier = Modifier.layoutId("divide"),
                color = Color.LightGray
            )

        if (uiState.isLogin)
            InputComment(
                modifier = Modifier.layoutId("inputComment"),
                profileImageUrl = uiState.writer?.profileUrl ?: "",
                onSend = { sendComment() },
                name = uiState.writer?.userName ?: "",
                input = uiState.comment,
                onValueChange = { onCommentChange(it) },
                replyName = uiState.reply?.name,
                isUploading = uiState.isUploading
            )
    }
}

@Preview
@Composable
fun PreviewCommentBody() {
    CommentBottomSheetBody(/*Preview*/
        onScrollTop = {},
        onCommentChange = {},
        onDelete = {},
        onUndo = {},
        sendComment = {},
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
        )
    )
}

@Preview
@Composable
fun PreviewInputCommentSticky() {
    InputCommentForSticky(uiState = CommentsUiState().copy(
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
        onClearReply = {}, onCommentChange = {})
}