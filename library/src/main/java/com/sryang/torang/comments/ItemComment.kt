package com.sryang.torang.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sryang.torang.R
import com.sryang.torang.data.comments.Comment
import com.sryang.torang.data.comments.testComment

@Composable
fun ItemComment(profileImageServerUrl: String, uiState: Comment) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        AsyncImage(
            model = profileImageServerUrl + uiState.profileImageUrl,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row {
                Text(text = uiState.name)
                Text(text = uiState.date)
            }
            Text(text = uiState.comment)
            Row {
                if (uiState.isUploading) {
                    Text(text = "Posting")
                } else {
                    Text(text = "Reply")
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = R.drawable.bxv,
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
            )
            Text(text = uiState.likeCount.toString())
        }
    }
}

@Preview
@Composable
fun PreviewItemComment() {
    ItemComment(profileImageServerUrl = "", uiState = testComment())
}