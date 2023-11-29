package com.sryang.torang.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sryang.torang.uistate.CommentItemUiState
import com.sryang.torang.uistate.testCommentItemUiState

@Composable
fun ItemCommentList(profileImageServerUrl: String, list: List<CommentItemUiState>) {
    Box(Modifier.heightIn(min = 350.dp)) {
        LazyColumn(content = {
            items(list.size) {
                Column {
                    ItemComment(profileImageServerUrl = profileImageServerUrl, uiState = list[it])
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
        list = arrayListOf(testCommentItemUiState(), testCommentItemUiState())
    )
}