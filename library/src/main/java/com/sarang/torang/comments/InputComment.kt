package com.sarang.torang.comments

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun InputComment(
    profileImageUrl: String,
    onSend: () -> Unit,
    name: String,
    input: String,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        // 다이얼로그 올라오는 도중에 키보드가 올라오면
        // 다이얼로그 애니메이션이 잠시 멈춰 딜레이 추가
        delay(200)
        focusRequester.requestFocus()
    }
    Row(
        Modifier
            .height(50.dp)
            .padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        TorangAsyncImage(
            model = profileImageUrl,
            errorIconSize = 20.dp,
            progressSize = 20.dp,
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
                .weight(1f)
                .focusRequester(focusRequester)
            ,
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
    InputComment(profileImageUrl = "", onSend = {

    }, name = "name", input = "", onValueChange = {})
}