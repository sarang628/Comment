package com.sarang.torang.compose.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ReplyComment(
    profileImageUrl: String, name: String, onClearReply: (() -> Unit)?,
    image: @Composable (Modifier, String, Dp?, Dp?, ContentScale?) -> Unit
) {
    Row(
        Modifier
            .layoutId("reply")
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFFEEEEEE))
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        image(
            Modifier
                .size(30.dp)
                .clip(CircleShape),
            profileImageUrl,
            20.dp,
            20.dp,
            ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Replying to ${name}", color = Color.Gray)
        Box(Modifier.fillMaxWidth()) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { onClearReply?.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "reply clear"
                )
            }
        }
    }
}
