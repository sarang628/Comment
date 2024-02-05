package com.sarang.torang.compose.comments

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
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.testComment
import kotlinx.coroutines.launch


@Composable
fun Comments(
    modifier: Modifier = Modifier,
    myId: Int?,
    list: List<Comment>,
    onTop: Boolean,
    onScrollTop: () -> Unit,
    onDelete: (Int) -> Unit,
    onUndo: (Int) -> Unit,
    onFavorite: ((Int) -> Unit)? = null,
    onReply: ((Comment) -> Unit)? = null
) {
    Box(
        modifier
            .heightIn(min = 350.dp)
            .fillMaxWidth()
    ) {

        if (list.isEmpty()) {
            EmptyComment(modifier.align(Alignment.Center))
        } else {
            Comments(
                list = list,
                onTop = onTop,
                onScrollTop = onScrollTop,
                onDelete = onDelete,
                onUndo = onUndo,
                onFavorite = onFavorite,
                onReply = onReply,
                myId = myId
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Comments(
    list: List<Comment>,
    onTop: Boolean,
    onScrollTop: () -> Unit,
    onDelete: (Int) -> Unit,
    onUndo: (Int) -> Unit,
    onFavorite: ((Int) -> Unit)? = null,
    onReply: ((Comment) -> Unit)? = null,
    myId: Int?,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = onTop, block = {
        if (onTop) {
            listState.animateScrollToItem(index = 0)
            onScrollTop.invoke()
        }
    })

    LazyColumn(state = listState, content = {
        items(list, key = { it.commentsId }) { comment ->
            Column(Modifier.animateItemPlacement()) {
                SwipeToDismissComment(
                    comment = comment,
                    onDelete = onDelete,
                    onUndo = onUndo,
                    myId = myId,
                    onFavorite = onFavorite,
                    onReply = onReply
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    })
}

@Preview
@Composable
fun EmptyComment(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(text = "No comments yet", fontWeight = FontWeight.Bold, fontSize = 23.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Start the conversation.")
    }
}

@Preview
@Composable
fun PreviewComments() {
    Comments(/*preview*/
        list = arrayListOf(
            testComment(0),
            testComment(1),
            testComment(2),
            testComment(3),
            testComment(4),
//            testComment(5),
//            testComment(6),
//            testComment(7),
//            testComment(8),
        ), onTop = false, onScrollTop = {}, onDelete = {}, onUndo = {}, myId = 0
    )
}