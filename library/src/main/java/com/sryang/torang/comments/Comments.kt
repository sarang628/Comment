package com.sryang.torang.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
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
import com.sryang.torang.viewmodels.CommentViewModel
import kotlin.math.roundToInt

@Composable
fun Comments(
    viewModel: CommentViewModel = hiltViewModel(),
    reviewId: Int,
    profileImageServerUrl: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = reviewId, block = {
        viewModel.loadComment(reviewId)
    })

    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearErrorMessage()
        }
    })

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
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
                    profileImageServerUrl = profileImageServerUrl,
                    list = uiState.list,
                    onTop = uiState.onTop,
                    onScrollTop = { viewModel.onScrollTop() },
                    onDelete = {
                        viewModel.onDelete(it)
                    },
                    onUndo = {
                        viewModel.onUndo(it)
                    },
                    myId = uiState.myId
                )
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                HorizontalDivider(color = Color.LightGray)
                InputComment(
                    profileImageServerUrl = profileImageServerUrl,
                    profileImageUrl = uiState.profileImageUrl,
                    onSend = { viewModel.sendComment() },
                    name = uiState.name,
                    input = uiState.comment,
                    onValueChange = { viewModel.onCommentChange(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsModal(
    profileImageServerUrl: String,
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
            profileImageServerUrl = profileImageServerUrl,
            reviewId = reviewId
        )
    }
}

@Preview
@Composable
fun PreviewComments() {
    Comments(
        profileImageServerUrl = "",
        reviewId = 78
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