package com.sryang.torang.comments

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sryang.torang.uistate.CommentsUiState
import com.sryang.torang.viewmodels.CommentViewModel

@Composable
fun Comments(
    viewModel: CommentViewModel = hiltViewModel(),
    reviewId: Int,
    profileImageServerUrl: String,
    onSend: (String) -> Unit
) {
    val commentsUiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = reviewId, block = {
        viewModel.loadComment(reviewId)
    })

    LaunchedEffect(key1 = commentsUiState.error, block = {
        commentsUiState.error?.let {
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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Comments", fontWeight = FontWeight.Bold)
                CommentHelp()
                ItemCommentList(
                    profileImageServerUrl = profileImageServerUrl,
                    list = commentsUiState.list
                )
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                HorizontalDivider(color = Color.LightGray)
                InputComment(
                    profileImageServerUrl = profileImageServerUrl,
                    profileImageUrl = commentsUiState.profileImageUrl,
                    onSend = { viewModel.sendComment() },
                    name = commentsUiState.name,
                    input = commentsUiState.comment,
                    onValueChange = { viewModel.onCommentChange(it) }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewComments() {
    Comments(
        profileImageServerUrl = "",
        onSend = {},
        reviewId = 78
    )
}
