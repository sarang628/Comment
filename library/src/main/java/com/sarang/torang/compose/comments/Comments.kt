package com.sarang.torang.compose.comments

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.data.comments.testSubComment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Comments(
    modifier: Modifier = Modifier,
    list: List<Comment>,
    movePosition: Int? = null,
    onPosition: () -> Unit,
    onDelete: (Long) -> Unit,
    onUndo: (Long) -> Unit,
    onFavorite: ((Long) -> Unit)? = null,
    onReply: ((Comment) -> Unit)? = null,
    myId: Int?,
    onViewMore: ((Long) -> Unit)? = null,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit,
) {
    val listState = rememberLazyListState()


    LaunchedEffect(key1 = movePosition) {
        movePosition?.let {
            listState.animateScrollToItem(index = movePosition)
            snapshotFlow { listState.firstVisibleItemIndex }
                .map { index -> movePosition == index }
                .distinctUntilChanged()
                .filter { it }
                .collect {
                    onPosition.invoke()
                }
        }
    }

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
                        onReply = onReply,
                        onViewMore = { onViewMore?.invoke(comment.commentsId) },
                        image = image
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
            testComment(0).copy(isUploading = true),
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
        movePosition = null, onPosition = {}, onDelete = {}, onUndo = {}, myId = 0,
        image = { _, _, _, _, _ -> }
    )
}