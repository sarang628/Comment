package com.sarang.torang.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.viewmodels.CommentViewModel
import kotlin.math.roundToInt

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
        sendComment = { viewModel.sendComment() })
}

@Composable
fun Comments(
    uiState: CommentsUiState,
    clearErrorMessage: () -> Unit,
    onScrollTop: () -> Unit,
    onDelete: (Int) -> Unit,
    onUndo: (Int) -> Unit,
    sendComment: () -> Unit,
    onCommentChange: (String) -> Unit
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
        Box(modifier = Modifier.padding(padding)) {
            Column(
                Modifier
                    .padding(bottom = 50.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Comments", fontWeight = FontWeight.Bold)
                CommentHelp()
                ItemCommentList(
                    list = uiState.list,
                    onTop = uiState.onTop,
                    onScrollTop = { onScrollTop() },
                    onDelete = {
                        onDelete(it)
                    },
                    onUndo = {
                        onUndo(it)
                    },
                    myId = uiState.myId
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                HorizontalDivider(color = Color.LightGray)
                InputComment(
                    profileImageUrl = uiState.profileImageUrl,
                    onSend = { sendComment() },
                    name = uiState.name,
                    input = uiState.comment,
                    onValueChange = { onCommentChange(it) }
                )
            }
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
    Comments(
        onScrollTop = {},
        onCommentChange = {},
        onDelete = {},
        onUndo = {},
        sendComment = {},
        uiState = CommentsUiState().copy(
            list = arrayListOf(testComment())
        ),
        clearErrorMessage = {}
    )
}


@Composable
fun SwipeDismissItem(item: Int) {
    var offset by remember { mutableStateOf(0f) }
    var dismissed by remember { mutableStateOf(false) }

    if (!dismissed) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offset.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        offset += pan.x
                    }
                }
                .background(Color.Gray)
        ) {
            // Your item content here
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Item $item", modifier = Modifier.padding(16.dp))
                IconButton(onClick = { dismissed = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}