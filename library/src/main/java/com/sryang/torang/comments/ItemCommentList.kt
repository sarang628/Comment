package com.sryang.torang.comments

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sryang.torang.data.comments.Comment
import com.sryang.torang.data.comments.testComment
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ItemCommentList(
    myId: Int?,
    profileImageServerUrl: String,
    list: List<Comment>,
    onTop: Boolean,
    onScrollTop: () -> Unit,
    onDelete: (Int) -> Unit,
    onUndo: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = onTop, block = {
        if (onTop) {
            listState.animateScrollToItem(index = 0)
            onScrollTop.invoke()
        }
    })

    Box(Modifier.heightIn(min = 350.dp).fillMaxWidth()) {
        LazyColumn(
            state = listState,
            content = {
                items(list, key = { it.commentsId })
                { comment ->
                    var confirm by remember { mutableStateOf(false) }
                    var state = rememberDismissState(
                        positionalThreshold = { f -> 500f },
                        confirmValueChange = { value ->
                            if (value == DismissValue.DismissedToStart) {
                                Log.d("ItemCommentList", value.toString())
                                confirm = true
                                onDelete.invoke(comment.commentsId)
                            }
                            true
                        }
                    )
                    Column(Modifier.animateItemPlacement()) {
                        SwipeToDismiss(
                            state = state,
                            background = {
                                val color by animateColorAsState(
                                    when (state.targetValue) {
                                        DismissValue.Default -> Color.Transparent
                                        DismissValue.DismissedToStart -> Color.Red
                                        else -> Color.Transparent
                                    }, label = ""
                                )
                                Box(
                                    modifier = Modifier
                                        .background(color = color)
                                        .fillMaxSize()
                                        .padding(end = 10.dp)
                                ) {
                                    if (confirm) {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    coroutineScope.launch {
                                                        onUndo.invoke(comment.commentsId)
                                                        state.reset()
                                                    }
                                                },
                                            textAlign = TextAlign.Center,
                                            text = "Undo",
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "",
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    )
                                }
                            },
                            dismissContent = {
                                ItemComment(
                                    profileImageServerUrl = profileImageServerUrl,
                                    uiState = comment
                                )
                            },
                            directions = if (comment.userId == myId) setOf(DismissDirection.EndToStart) else setOf()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            })

        if (list.isEmpty()) {
            Column(Modifier.align(Alignment.Center)) {
                Text(text = "No comments yet", fontWeight = FontWeight.Bold, fontSize = 23.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Start the conversation.")
            }
        }
    }
}

@Preview
@Composable
fun PreviewItemCommentList() {
    ItemCommentList(
        profileImageServerUrl = "",
        list = arrayListOf(testComment(), testComment()),
        onTop = false,
        onScrollTop = {},
        onDelete = {},
        onUndo = {},
        myId = 0
    )
}