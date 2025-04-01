package com.sarang.torang

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.samples.apps.sunflower.ui.TorangTheme
import com.sarang.torang.compose.comments.CommentsModal
import com.sarang.torang.compose.comments.PreviewComments
import com.sarang.torang.di.comment_di.CommentBottomSheet
import com.sarang.torang.di.image.provideTorangAsyncImage
import com.sarang.torang.repository.LoginRepository
import com.sarang.torang.repository.LoginRepositoryTest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutine = rememberCoroutineScope()
            var showCommentDialog by remember { mutableStateOf(false) }
            var showComment by remember { mutableStateOf(false) }
            var reviewId: Int? by remember { mutableStateOf(329) }


            TorangTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Button(onClick = {
                            coroutine.launch {
                                reviewId = 329
                                showComment = true
                            }
                        }) {
                            Text(text = "showComment")
                        }

                        Button(onClick = { showCommentDialog = true }) {
                            Text(text = "showCommentDialog")
                        }
                        LoginRepositoryTest(loginRepository = loginRepository)
                    }


                    CommentBottomSheet(
                        reviewId = reviewId,
                        onDismissRequest = {},
                        show = showComment,
                        onHidden = {
                            showComment = false
                            reviewId = null
                        },
                        content = {},
                        image = provideTorangAsyncImage(),
                        onImage = {},
                        onName = {}
                    )

                    if (showCommentDialog) {
                        CommentsModal(
                            reviewId = reviewId,
                            onDismissRequest = { showCommentDialog = false },
                            image = provideTorangAsyncImage(),
                            onImage = {},
                            onName = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class ThemePreviews

@ThemePreviews
@Composable
fun PreviewComments1() {
    TorangTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PreviewComments()
        }
    }
}