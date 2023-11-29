package com.sryang.torang.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sryang.torang.uistate.CommentItemUiState
import com.sryang.torang.uistate.testCommentItemUiState

@Composable
fun Comments(
    profileImageServerUrl: String,
    profileImageUrl: String,
    list: List<CommentItemUiState>,
    onSend: (String) -> Unit,
    name: String
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        Column(
            Modifier
                .padding(bottom = 50.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Comments", fontWeight = FontWeight.Bold)
            CommentHelp()
            ItemCommentList(profileImageServerUrl = profileImageServerUrl, list = list)
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            HorizontalDivider(color = Color.LightGray)
            InputComment(
                profileImageServerUrl = profileImageServerUrl,
                profileImageUrl = profileImageUrl,
                onSend = onSend,
                name = name
            )
        }
    }
}

@Preview
@Composable
fun PreviewCommentScreen() {
    Comments(
        profileImageServerUrl = "",
        profileImageUrl = "",
        list = ArrayList<CommentItemUiState>().apply {
            add(testCommentItemUiState())
            add(testCommentItemUiState())
            add(testCommentItemUiState())
            add(testCommentItemUiState())
        },
        onSend = {},
        name = ""
    )
}
