package com.sarang.torang

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.samples.apps.sunflower.ui.TorangTheme
import com.sarang.torang.compose.comments.CommentsModal
import com.sarang.torang.compose.comments.PreviewComments
import com.sarang.torang.di.comment_di.CommentBottomSheet
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
            val sheetValue by remember { mutableStateOf(SheetValue.Hidden) }
            val sheetState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    initialValue = sheetValue,
                    skipHiddenState = false
                )
            )
            var init by remember { mutableStateOf(true) }
            var reviewId: Int? by remember { mutableStateOf(329) }

            LaunchedEffect(key1 = sheetState.bottomSheetState) {
                snapshotFlow { sheetState.bottomSheetState.currentValue }.collect {
                    if (it == SheetValue.Hidden) {
                        Log.d("__sryang", "reviewId set null")
                        reviewId = null
                    }
                }
            }


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
                                init = false
                                sheetState.bottomSheetState.expand()
                            }
                        }) {
                            Text(text = "showComment")
                        }

                        Button(onClick = { showCommentDialog = true }) {
                            Text(text = "showCommentDialog")
                        }
                        LoginRepositoryTest(loginRepository = loginRepository)
                    }
                    if (init == false && sheetState.bottomSheetState.currentValue != SheetValue.Hidden)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000))
                        ) {
                            Text(text = "!!")
                        }

                    Box(modifier = Modifier) {
                        CommentBottomSheet(
                            reviewId = reviewId,
                            sheetState = sheetState,
                            onDismissRequest = {},
                            onBackPressed = {},
                            init = init,
                            onHidden = { init = true },
                            content = {}
                        )

                        if (showCommentDialog) {
                            CommentsModal(
                                reviewId = reviewId,
                                onDismissRequest = {
                                    Log.d("__sryang", "reviewId = null")
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