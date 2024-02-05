package com.sarang.torang.compose.comments

import TorangAsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.isLogin
import com.sarang.torang.viewmodels.CommentViewModel

@Composable
fun Comments(
    viewModel: CommentViewModel = hiltViewModel(),
    reviewId: Int,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = reviewId, block = {
        viewModel.loadComment(reviewId)
    })

    Comments(
        uiState = uiState,
        clearErrorMessage = { viewModel.clearErrorMessage() },
        onUndo = { viewModel.onUndo(it) },
        onDelete = { viewModel.onDelete(it) },
        onCommentChange = { viewModel.onCommentChange(it) },
        onScrollTop = { viewModel.onScrollTop() },
        sendComment = { viewModel.sendComment() },
        onFavorite = { viewModel.onFavorite(it) },
        onReply = { viewModel.onReply(it) },
        onClearReply = { viewModel.onClearReply() })
}

@Composable
fun Comments(
    uiState: CommentsUiState,
    clearErrorMessage: () -> Unit,
    onScrollTop: () -> Unit,
    onDelete: (Int) -> Unit,
    onUndo: (Int) -> Unit,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit,
    onFavorite: ((Int) -> Unit)? = null,
    onReply: ((Comment) -> Unit)? = null,
    onClearReply: (() -> Unit)? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            clearErrorMessage()
        }
    })

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxWidth(),
    ) { padding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            constraintSet = commentsConstraintSet()
        ) {
            Text(
                modifier = Modifier.layoutId("title"),
                text = "Comments",
                fontWeight = FontWeight.Bold
            )
            CommentHelp(Modifier.layoutId("commentHelp"))

            ItemCommentList(
                modifier = Modifier.layoutId("itemCommentList"),
                list = uiState.list,
                onTop = uiState.onTop,
                onScrollTop = { onScrollTop() },
                onDelete = { onDelete(it) },
                onUndo = { onUndo(it) },
                myId = uiState.myId,
                onFavorite = onFavorite,
                onReply = onReply
            )

            if (uiState.reply != null)
                Row(
                    Modifier
                        .layoutId("reply")
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFFEEEEEE))
                        .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TorangAsyncImage(
                        model = "",
                        modifier = Modifier.size(30.dp),
                        progressSize = 20.dp,
                        errorIconSize = 20.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Replying to ${uiState.reply?.name}", color = Color.Gray)
                    Box(Modifier.fillMaxWidth()) {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = { onClearReply?.invoke() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "reply clear"
                            )
                        }
                    }
                }

            if (uiState.isLogin)
                HorizontalDivider(
                    modifier = Modifier.layoutId("divide"),
                    color = Color.LightGray
                )

            if (uiState.isLogin)
                InputComment(
                    modifier = Modifier.layoutId("inputComment"),
                    profileImageUrl = uiState.profileImageUrl,
                    onSend = { sendComment() },
                    name = uiState.name,
                    input = uiState.comment,
                    onValueChange = { onCommentChange(it) },
                    replyName = uiState.reply?.name
                )

        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsModal(
    reviewId: Int,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Comments(
            reviewId = reviewId
        )
    }
}

@Preview
@Composable
fun PreviewComments() {
    Comments(/*Preview*/
        onScrollTop = {},
        onCommentChange = {},
        onDelete = {},
        onUndo = {},
        sendComment = {},
        uiState = CommentsUiState().copy(
            list = arrayListOf(testComment()),
            myId = 10,
            reply = testComment()
        ),
        clearErrorMessage = {}
    )
}