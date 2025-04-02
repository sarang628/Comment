package com.sarang.torang.compose.comments

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.favoriteIcon
import com.sarang.torang.data.comments.isSubComment
import com.sarang.torang.data.comments.testComment
import com.sarang.torang.data.comments.testSubComment
import com.sarang.torang.data.comments.transFormComment
import kotlinx.coroutines.launch

@Composable
fun Comment(
    comment: Comment,
    isLogin: Boolean,
    onFavorite: () -> Unit,
    onReply: () -> Unit,
    onViewMore: () -> Unit,
    onName: () -> Unit,
    onImage: () -> Unit,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                if (comment.isUploading) {
                    MaterialTheme.colorScheme.surfaceBright
                } else {
                    //MaterialTheme.colorScheme.background
                    Color.Transparent
                }
            ),
        constraintSet = itemCommentConstraintSet(isSubComment = comment.isSubComment)
    ) {
        image.invoke(
            Modifier
                .layoutId("profileImage")
                .size(if (comment.isSubComment) 30.dp else 40.dp)
                .clip(CircleShape)
                .clickable { onImage.invoke() },
            comment.profileImageUrl,
            20.dp,
            20.dp,
            ContentScale.Crop
        )

        Text(
            text = comment.name,
            Modifier
                .layoutId("name")
                .clickable { onName.invoke() },
            fontSize = 13.sp,
            style = LocalTextStyle.current.merge(
                TextStyle.Default.merge(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
        )
        Text(
            text = comment.date,
            Modifier.layoutId("date"),
            color = Color.Gray,
            fontSize = 13.sp,
            style = LocalTextStyle.current.merge(
                TextStyle.Default.merge(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
        )
        Text(
            text = comment.transFormComment(MaterialTheme.colorScheme.primary),
            Modifier.layoutId("comment"),
            fontSize = 13.sp
        )

        if (isLogin)
            Text(
                text = if (comment.isUploading) "Posting" else "Reply",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .layoutId("replyAndPosting")
                    .clickable { onReply.invoke() }, fontSize = 13.sp,
                color = Color.Gray
            )

        if (isLogin) {
            IconButton(modifier = Modifier
                .layoutId("favorite"), onClick = { onFavorite.invoke() }) {
                Icon(
                    modifier = Modifier
                        .size(20.dp),
                    imageVector = comment.favoriteIcon,
                    contentDescription = "",
                )
            }

            Text(text = comment.commentLikeCount.toString(), Modifier.layoutId("likeCount"))
        }

        if (comment.subCommentCount != null && comment.subCommentCount > 0) {
            MoreReply(
                Modifier.layoutId("moreReply"),
                comment.subCommentCount,
                onViewMore = onViewMore
            )
        }
    }
}

fun itemCommentConstraintSet(isSubComment: Boolean = false): ConstraintSet {
    return ConstraintSet {
        val profileImage = createRefFor("profileImage")
        val name = createRefFor("name")
        val date = createRefFor("date")
        val comment = createRefFor("comment")
        val replyAndPosting = createRefFor("replyAndPosting")
        val favorite = createRefFor("favorite")
        val likeCount = createRefFor("likeCount")
        val replies = createRefFor("replies")
        val moreReply = createRefFor("moreReply")

        constrain(profileImage) {
            start.linkTo(parent.start, if (isSubComment) 48.dp else 8.dp)
            top.linkTo(parent.top)
        }

        constrain(name) {
            start.linkTo(profileImage.end, 8.dp)
            top.linkTo(parent.top)
        }

        constrain(date) {
            start.linkTo(name.end, 5.dp)
            top.linkTo(name.top)
        }

        constrain(comment) {
            start.linkTo(name.start)
            top.linkTo(date.bottom, 4.dp)
            end.linkTo(favorite.start)
            width = Dimension.fillToConstraints
        }

        constrain(replyAndPosting) {
            start.linkTo(name.start)
            top.linkTo(comment.bottom, 4.dp)
        }

        constrain(favorite) { end.linkTo(parent.end) }
        constrain(likeCount) {
            end.linkTo(parent.end)
            top.linkTo(favorite.bottom)
            start.linkTo(favorite.start)
            end.linkTo(favorite.end)
        }

        constrain(replies) {
            top.linkTo(replyAndPosting.bottom)
        }

        constrain(moreReply) {
            top.linkTo(replies.bottom)
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissComment(
    comment: Comment,
    isLogin: Boolean,
    onDelete: (Long) -> Unit,
    onUndo: (Long) -> Unit,
    onFavorite: (Long) -> Unit,
    onReply: (Comment) -> Unit,
    myId: Int?,
    onViewMore: () -> Unit,
    onName: () -> Unit,
    onImage: () -> Unit,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var confirm by remember { mutableStateOf(false) }
    val state = rememberSwipeToDismissBoxState(
        positionalThreshold = { f -> 500f },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                Log.d("ItemCommentList", value.toString())
                confirm = true
                onDelete.invoke(comment.commentsId)
            }
            true
        }
    )
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = comment.userId == myId,
        backgroundContent = {
            val color by animateColorAsState(
                when (state.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.secondary
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.secondary
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                }, label = ""
            )
            Undo(
                color = color,
                onUndo = {
                    onUndo.invoke(comment.commentsId)
                    coroutineScope.launch {
                        state.reset()
                    }
                },
                confirm = confirm,
                showIcon = state.targetValue == SwipeToDismissBoxValue.EndToStart
            )
        },
        content = {
            Comment(
                comment = comment,
                onFavorite = { onFavorite.invoke(comment.commentsId) },
                onReply = { onReply.invoke(comment) },
                onViewMore = onViewMore,
                image = image,
                onName = onName,
                onImage = onImage,
                isLogin = isLogin
            )
        }
    )
}

@Composable
fun Undo(
    color: Color,
    onUndo: () -> Unit,
    confirm: Boolean,
    showIcon: Boolean
) {
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
                        onUndo.invoke()
                    },
                textAlign = TextAlign.Center,
                text = "Undo",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (showIcon) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "",
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoreReply(modifier: Modifier = Modifier, count: Int? = 0, onViewMore: (() -> Unit)? = null) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable {
                onViewMore?.invoke()
            }
            .height(30.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(50.dp))
        HorizontalDivider(Modifier.width(50.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "View $count more reply",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HideReply(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .height(30.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(50.dp))
        HorizontalDivider(Modifier.width(50.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Hide replies",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUndo() {
    Undo(
        color = MaterialTheme.colorScheme.secondary,
        onUndo = { /*TODO*/ },
        confirm = true,
        showIcon = true
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSwipeToDismissComment() {
    SwipeToDismissComment(/*Preview*/
        comment = testComment().copy(isUploading = true),
        onDelete = {},
        onUndo = {},
        myId = 0,
        image = { _, _, _, _, _ -> },
        onFavorite = {},
        onReply = {},
        onViewMore = {},
        onName = {},
        onImage = {},
        isLogin = false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewComment() {
    Comment(/*Preview*/
        comment = testComment(),
        image = { _, _, _, _, _ -> },
        onName = {},
        onReply = {},
        onImage = {},
        onFavorite = {},
        onViewMore = {},
        isLogin = false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSubComment() {
    Comment(/*Preview*/
        comment = testSubComment(),
        image = { _, _, _, _, _ -> },
        onName = {},
        onReply = {},
        onImage = {},
        onFavorite = {},
        onViewMore = {},
        isLogin = false
    )
}