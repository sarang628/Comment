package com.sarang.torang.compose.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.User
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.data.comments.testSubComment
import com.sarang.torang.uistate.Comments
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.isLogin
import com.sarang.torang.uistate.isUploading
import com.sarang.torang.viewmodels.CommentViewModel
import kotlinx.coroutines.launch

/**
 * @param reviewId sheet를 Hidden 시킬 때 reviewId를 null로 넘기면 화면이 초기화 됩니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsModal(
    modifier: Modifier = Modifier,
    viewModel: CommentViewModel = hiltViewModel(),
    reviewId: Int?,
    onDismissRequest: () -> Unit,
    onName: (Int) -> Unit,
    onImage: (Int) -> Unit,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit
) {
    val uiState = viewModel.uiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutine = rememberCoroutineScope()

    when (uiState) {
        is CommentsUiState.Success -> {
            val data = uiState.comments
            LaunchedEffect(key1 = data.snackBarMessage, block = {
                data.snackBarMessage?.let {
                    coroutine.launch {
                        snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
                    }
                    viewModel.clearErrorMessage()
                }
            })

            LaunchedEffect(key1 = reviewId, block = {
                viewModel.loadComment(reviewId)
            })

            ModalBottomSheet(
                modifier = modifier,
                sheetState = sheetState,
                onDismissRequest = onDismissRequest
            ) {
                Column {
                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { padding ->
                        CommentModalBody(
                            modifier = Modifier.padding(padding),
                            uiState = uiState.comments,
                            onUndo = { viewModel.onUndo(it) },
                            onDelete = { viewModel.onDelete(it) },
                            onCommentChange = { viewModel.onCommentChange(it) },
                            onScrollTop = { viewModel.onPosition() },
                            sendComment = { viewModel.sendComment() },
                            onFavorite = { viewModel.onFavorite(it) },
                            onReply = { viewModel.onReply(it) },
                            onClearReply = { viewModel.onClearReply() },
                            onViewMore = { viewModel.onViewMore(it) },
                            onRequestFocus = { viewModel.onRequestFocus() },
                            image = image,
                            onName = onName,
                            onImage = onImage
                        )
                    }
                }
            }
        }

        CommentsUiState.Loading -> {}
        CommentsUiState.Error -> {}
    }
}

@Composable
fun CommentModalBody(
    modifier: Modifier = Modifier,
    uiState: Comments,
    onScrollTop: () -> Unit,
    onDelete: (Long) -> Unit,
    onUndo: (Long) -> Unit,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit,
    onFavorite: (Long) -> Unit,
    onReply: (Comment) -> Unit,
    onClearReply: () -> Unit,
    onViewMore: (Long) -> Unit,
    onRequestFocus: () -> Unit,
    onName: (Int) -> Unit,
    onImage: (Int) -> Unit,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit,
) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize(), constraintSet = commentsConstraintSet()
    ) {
        Text(
            modifier = Modifier.layoutId("title"), text = "Comments", fontWeight = FontWeight.Bold
        )
        // CommentHelp(Modifier.layoutId("commentHelp"))

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
                onViewMore = onViewMore,
                image = image,
                onName = onName,
                onImage = onImage,
                isLogin = uiState.isLogin
            )
        }

        if (uiState.reply != null) ReplyComment(
            profileImageUrl = uiState.reply.profileImageUrl,
            uiState.reply.name,
            onClearReply,
            image = image
        )


        HorizontalDivider(
            modifier = Modifier.layoutId("divide"), color = Color.LightGray
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
            onRequestFocus = onRequestFocus,
            image = image,
            enabled = uiState.isLogin
        )
    }
}

fun commentsConstraintSet(): ConstraintSet {
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

@Preview(showBackground = true)
@Composable
fun PreviewCommentModalBody() {
    CommentModalBody(/*Preview*/
        onScrollTop = {},
        onCommentChange = {},
        onDelete = {},
        onUndo = {},
        sendComment = {},
        image = { _, _, _, _, _ -> },
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
            ), writer = User("", 10, ""), reply = testComment(), movePosition = 0
        ),
        onReply = {},
        onFavorite = {},
        onViewMore = {},
        onRequestFocus = {},
        onClearReply = {},
        onName = {},
        onImage = {})
}