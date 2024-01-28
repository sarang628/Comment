package com.sryang.torang.comments

import TorangAsyncImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InputComment(
    profileImageServerUrl: String,
    profileImageUrl: String,
    onSend: () -> Unit,
    name: String,
    input: String,
    onValueChange: (String) -> Unit
) {

    Row(
        Modifier
            .height(50.dp)
            .padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        TorangAsyncImage(
            model = profileImageServerUrl + profileImageUrl,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = input,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            decorationBox = { innerTextField ->
                Box() {
                    if (input.isEmpty()) {
                        Text(
                            text = "Add a comment for $name",
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            }
        )
        Button(onClick = { onSend.invoke() }) {
            Text(text = "send")
        }
    }
}

@Preview
@Composable
fun PreviewInputComment() {
    InputComment(profileImageServerUrl = "", profileImageUrl = "", onSend = {

    }, name = "name", input = "", onValueChange = {})
}