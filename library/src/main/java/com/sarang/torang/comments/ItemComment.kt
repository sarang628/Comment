package com.sarang.torang.comments

import TorangAsyncImage
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.favoriteIcon
import com.sarang.torang.data.comments.testComment

@Composable
fun ItemComment(
    uiState: Comment,
    onFavorite: (() -> Unit)? = null,
    onReply: (() -> Unit)? = null
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                if (uiState.isUploading) {
                    MaterialTheme.colorScheme.surfaceBright
                } else {
                    //MaterialTheme.colorScheme.background
                    Color.Transparent
                }
            ),
        constraintSet = ItemCommentConstraintSet()
    ) {
        TorangAsyncImage(
            model = uiState.profileImageUrl,
            errorIconSize = 20.dp,
            progressSize = 20.dp,
            modifier = Modifier
                .layoutId("profileImage")
                .size(40.dp)
                .clip(CircleShape),
        )

        Text(text = uiState.name, Modifier.layoutId("name"), fontSize = 13.sp)
        Text(text = uiState.date, Modifier.layoutId("date"), color = Color.Gray, fontSize = 13.sp)
        Text(text = uiState.comment, Modifier.layoutId("comment"), fontSize = 13.sp)
        Text(
            text = if (uiState.isUploading) "Posting" else "Reply",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .layoutId("replyAndPosting")
                .clickable { onReply?.invoke() }, fontSize = 13.sp,
            color = Color.Gray
        )

        IconButton(modifier = Modifier
            .layoutId("favorite"), onClick = { onFavorite?.invoke() }) {
            Icon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = uiState.favoriteIcon,
                contentDescription = "",
            )
        }

        Text(text = uiState.commentLikeCount.toString(), Modifier.layoutId("likeCount"))
    }
}

fun ItemCommentConstraintSet(): ConstraintSet {
    return ConstraintSet {
        val profileImage = createRefFor("profileImage")
        val name = createRefFor("name")
        val date = createRefFor("date")
        val comment = createRefFor("comment")
        val replyAndPosting = createRefFor("replyAndPosting")
        val favorite = createRefFor("favorite")
        val likeCount = createRefFor("likeCount")

        constrain(profileImage) {
            start.linkTo(parent.start, 8.dp)
            top.linkTo(parent.top, 8.dp)
        }

        constrain(name) {
            start.linkTo(profileImage.end, 8.dp)
            top.linkTo(parent.top, 8.dp)
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

    }

}

@Preview
@Composable
fun PreviewItemComment() {
    ItemComment(uiState = testComment())
}