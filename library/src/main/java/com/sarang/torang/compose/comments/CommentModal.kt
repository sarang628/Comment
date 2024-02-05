package com.sarang.torang.compose.comments

import TorangAsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsModal(
    viewModel: CommentViewModel = hiltViewModel(),
    reviewId: Int,
    onDismissRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearErrorMessage()
        }
    })

    LaunchedEffect(key1 = reviewId, block = {
        viewModel.loadComment(reviewId)
    })

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier
                .fillMaxWidth(),
        ) { padding ->
            CommentModalBody(
                modifier = Modifier.padding(padding),
                uiState = uiState,
                onUndo = { viewModel.onUndo(it) },
                onDelete = { viewModel.onDelete(it) },
                onCommentChange = { viewModel.onCommentChange(it) },
                onScrollTop = { viewModel.onScrollTop() },
                sendComment = { viewModel.sendComment() },
                onFavorite = { viewModel.onFavorite(it) },
                onReply = { viewModel.onReply(it) },
                onClearReply = { viewModel.onClearReply() })
        }
    }
}

@Composable
fun CommentModalBody(
    modifier: Modifier = Modifier,
    uiState: CommentsUiState,
    onScrollTop: () -> Unit,
    onDelete: (Int) -> Unit,
    onUndo: (Int) -> Unit,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit,
    onFavorite: ((Int) -> Unit)? = null,
    onReply: ((Comment) -> Unit)? = null,
    onClearReply: (() -> Unit)? = null
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize(),
        constraintSet = commentsConstraintSet()
    ) {
        Text(
            modifier = Modifier.layoutId("title"),
            text = "Comments",
            fontWeight = FontWeight.Bold
        )
        CommentHelp(Modifier.layoutId("commentHelp"))

        Comments(
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

@Preview
@Composable
fun CommentHelp(modifie: Modifier = Modifier) {
    Column(modifie.padding(top = 5.dp, bottom = 15.dp, start = 10.dp, end = 10.dp)) {
        Text(
            text = "this reel is shared publicly to Facebook. Your interactions can also appear..",
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "",
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color.LightGray)
        )
    }
}

@Composable
fun InputComment(
    modifier: Modifier = Modifier,
    profileImageUrl: String,
    onSend: () -> Unit,
    name: String,
    input: String,
    onValueChange: (String) -> Unit,
    replyName: String? = null
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        // 다이얼로그 올라오는 도중에 키보드가 올라오면
        // 다이얼로그 애니메이션이 잠시 멈춰 딜레이 추가
        delay(200)
        focusRequester.requestFocus()
    }
    Row(
        modifier
            .height(50.dp)
            .padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        TorangAsyncImage(
            model = profileImageUrl,
            errorIconSize = 20.dp,
            progressSize = 20.dp,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = input,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                if (replyName != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "@${replyName}")
                        Spacer(Modifier.width(3.dp))
                        innerTextField()
                    }

                } else if (input.isEmpty()) {
                    Box {
                        Text(
                            text = "Add a comment for $name",
                            color = Color.Gray
                        )
                        innerTextField()
                    }
                } else {
                    innerTextField()
                }
            }
        )
        Button(onClick = { onSend.invoke() }) {
            Text(text = "send")
        }
    }
}

@Preview
@Composable
fun PreviewInputComment() {
    InputComment(profileImageUrl = "", onSend = {

    }, name = "name", input = "", onValueChange = {})
}

@Preview
@Composable
fun PreviewCommentModalBody() {
    CommentModalBody(/*Preview*/
        onScrollTop = {},
        onCommentChange = {},
        onDelete = {},
        onUndo = {},
        sendComment = {},
        uiState = CommentsUiState().copy(
            list = arrayListOf(testComment()),
            myId = 10,
            reply = testComment()
        )
    )
}