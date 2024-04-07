package com.sarang.torang

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.samples.apps.sunflower.ui.TorangTheme
import com.sarang.torang.compose.comments.CommentBottomSheet
import com.sarang.torang.compose.comments.CommentsModal
import com.sarang.torang.compose.comments.PreviewComments
import com.sarang.torang.repository.LoginRepository
import com.sarang.torang.repository.LoginRepositoryTest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loginRepository: LoginRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showCommentDialog by remember { mutableStateOf(false) }
            val coroutine = rememberCoroutineScope()
            val sheetState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
            )
            TorangTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier) {
                        CommentBottomSheet(
                            reviewId = 329,
                            sheetState = sheetState,
                            onDismissRequest = {
                            }
                        ) {
                            Column {
                                Button(onClick = {
                                    coroutine.launch {
                                        sheetState.bottomSheetState.expand()
                                    }
                                }) {
                                    Text(text = "showComment")
                                }

                                Button(onClick = {
                                    showCommentDialog = true
                                }) {
                                    Text(text = "showCommentDialog")
                                }
                                LoginRepositoryTest(loginRepository = loginRepository)
                            }
                            if (sheetState.bottomSheetState.currentValue != SheetValue.Hidden)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0x55000000))
                                ) {
                                    Text(text = "!!")
                                }
                        }

                        if (showCommentDialog) {
                            CommentsModal(
                                reviewId = 329,
                                onDismissRequest = {
                                    showCommentDialog = false
                                },
                            )
                        }
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