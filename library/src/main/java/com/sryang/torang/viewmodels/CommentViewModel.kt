package com.sryang.torang.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sryang.torang.data.comments.Comment
import com.sryang.torang.uistate.CommentsUiState
import com.sryang.torang.usecase.comments.DeleteCommentUseCase
import com.sryang.torang.usecase.comments.GetCommentsUseCase
import com.sryang.torang.usecase.comments.GetUserUseCase
import com.sryang.torang.usecase.comments.SendCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val sendCommentUseCase: SendCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState = _uiState.asStateFlow()
    val jobs = HashMap<Int, Boolean>()

    init {
        viewModelScope.launch {
            try {
                val user = getUserUseCase.invoke()
                _uiState.update { it.copy(profileImageUrl = user.profilerl, myId = user.userId) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadComment(reviewId: Int) {
        viewModelScope.launch {
            try {
                val list = getCommentsUseCase.invoke(reviewId = reviewId)
                _uiState.update { it.copy(list = list, reviewId = reviewId) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun sendComment() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(list = ArrayList<Comment>().apply {
                        add(
                            Comment(
                                userId = 0,
                                profileImageUrl = it.profileImageUrl,
                                comment = it.comment,
                                date = "2023",
                                likeCount = 0,
                                name = it.name,
                                isUploading = true
                            )
                        )
                        addAll(it.list)
                    })
                }
                _uiState.update { it.copy(onTop = true) }
                val comment = _uiState.value.comment
                _uiState.update { it.copy(comment = "") }
                delay(1000)
                val result = sendCommentUseCase.invoke(
                    reviewId = uiState.value.reviewId!!,
                    comment = comment
                )
                _uiState.update {
                    it.copy(list = ArrayList(it.list).apply {
                        set(0, result)
                    })
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(error = null) }
    }

    fun onCommentChange(comment: String) {
        Log.d("_CommentViewModel", comment)
        _uiState.update { it.copy(comment = comment) }
    }

    fun onScrollTop() {
        _uiState.update { it.copy(onTop = false) }
    }

    fun onDelete(commentsId: Int) {
        if (jobs.containsKey(commentsId) && jobs.get(commentsId) == true)
            return

        jobs.put(commentsId, true)
        viewModelScope.launch {
            delay(3000)
            if (jobs.get(commentsId) == true) {
                deleteCommentUseCase.delete(commentId = commentsId)
                _uiState.update {
                    it.copy(list = it.list.filter { it.commentsId != commentsId })
                }
                jobs.remove(commentsId)
            }
        }
    }

    fun onUndo(commentId: Int) {
        jobs.put(commentId, false)
    }
}