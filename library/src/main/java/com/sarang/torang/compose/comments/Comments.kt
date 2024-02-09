package com.sarang.torang.compose.comments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.data.comments.testSubComment

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Comments(
    modifier: Modifier = Modifier,
    list: List<Comment>,
    movePosition: Int? = null,
    onPosition: () -> Unit,
    onDelete: (Long) -> Unit,
    onUndo: (Long) -> Unit,
    onFavorite: ((Long) -> Unit)? = null,
    onReply: ((Comment) -> Unit)? = null,
    myId: Int?,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = movePosition, block = {
        movePosition?.let {
            listState.animateScrollToItem(index = movePosition)
            onPosition.invoke()
        }
    })

    // https://developer.android.com/jetpack/compose/lists#content-spacing
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
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
                }
            }
        })
}

@Preview
@Composable
fun PreviewComments() {
    Comments(/*preview*/
        modifier = Modifier,
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
        movePosition = null, onPosition = {}, onDelete = {}, onUndo = {}, myId = 0
    )
}