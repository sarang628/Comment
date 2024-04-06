package com.sarang.torang.compose.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun EmptyComment(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(350.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.align(Alignment.Center)) {
            Text(text = "No comments yet", fontWeight = FontWeight.Bold, fontSize = 23.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Start the conversation.")
        }
    }
}