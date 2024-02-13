package com.sarang.torang.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.isFavorite
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.findRootCommentId
import com.sarang.torang.uistate.toComment
import com.sarang.torang.usecase.comments.AddCommentLikeUseCase
import com.sarang.torang.usecase.comments.DeleteCommentLikeUseCase
import com.sarang.torang.usecase.comments.DeleteCommentUseCase
import com.sarang.torang.usecase.comments.GetCommentsUseCase
import com.sarang.torang.usecase.comments.GetUserUseCase
import com.sarang.torang.usecase.comments.LoadMoreUseCase
import com.sarang.torang.usecase.comments.SendCommentUseCase
import com.sarang.torang.usecase.comments.SendReplyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val sendCommentUseCase: SendCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val addCommentUseCase: AddCommentLikeUseCase,
    private val deleteCommentLikeUseCaes: DeleteCommentLikeUseCase,
    private val sendReplyUseCase: SendReplyUseCase,
    private val loadMoreUseCase: LoadMoreUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState = _uiState.asStateFlow()
    private val jobs: HashMap<Long, Boolean> = HashMap()

    init {
        viewModelScope.launch {
            getUserUseCase.invoke().collectLatest { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(writer = user)
                    }
                } else {
                    _uiState.update { it.copy(writer = null, snackBarMessage = "로그인을 해주세요.") }
                }
            }
        }
    }

    fun loadComment(reviewId: Int) {
        viewModelScope.launch {
            try {
                getCommentsUseCase.invoke(reviewId = reviewId).collectLatest { list ->
                    _uiState.update {
                        it.copy(
                            list = list,
                            reviewId = reviewId
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackBarMessage = e.message) }
            }
        }
    }

    fun sendComment() {
        if (_uiState.value.reply == null) {
            val uploadComment = uiState.value.toComment
            _uiState.update {
                viewModelScope.launch {
                    try {
                        sendCommentUseCase.invoke(
                            reviewId = uiState.value.reviewId!!,
                            comment = uiState.value.comment
                        )
                    } catch (e: Exception) {
                        _uiState.update { it.copy(snackBarMessage = e.message) }
                    }
                }
                it.copy(
                    comment = "", // 입력창 초기화
                    reply = null, // 댓글 초기화
                )
            }
        } else {
            viewModelScope.launch {
                try {
                    val uploadComment =
                        uiState.value.uploadingComment ?: throw Exception("전송할 코멘트 정보가 없습니다.")
                    sendReplyUseCase.invoke(
                        reviewId = uiState.value.reviewId!!,
                        parentCommentId = uiState.value.findRootCommentId(uploadComment),
                        comment = uiState.value.comment
                    )
                    _uiState.update {
                        it.copy(
                            comment = "", // 입력창 초기화
                            reply = null, // 댓글 초기화
                        )
                    }
                } catch (e: Exception) {
                    Log.e("_CommentViewModel", e.toString())
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(snackBarMessage = null) }
    }

    fun onCommentChange(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun onPosition() {
        _uiState.update { it.copy(movePosition = null) }
    }

    fun onDelete(commentsId: Long) {
        if (jobs.containsKey(commentsId) && jobs[commentsId] == true)
            return

        jobs[commentsId] = true
        viewModelScope.launch {
            delay(3000)
            if (jobs[commentsId] == true) {
                deleteCommentUseCase.delete(commentId = commentsId)
                _uiState.update { it ->
                    it.copy(list = it.list.filter { it.commentsId != commentsId })
                }
                jobs.remove(commentsId)
            }
        }
    }

    fun onUndo(commentId: Long) {
        jobs[commentId] = false
    }

    fun onFavorite(commentId: Long) {
        // 현재 좋아요 상태인지 확인
        val comment = uiState.value.list.find { it.commentsId == commentId } ?: return
        if (comment.isFavorite) {
            // 좋아요 삭제 요청
            viewModelScope.launch {
                try {
                    deleteCommentLikeUseCaes.invoke(commentId)
                    // 좋아요 UI 업데이트
                    _uiState.update { it ->
                        it.copy(
                            list = it.list.map {
                                if (it.commentsId == comment.commentsId)
                                    comment.copy(
                                        commentLikeId = null,
                                        commentLikeCount = it.commentLikeCount - 1
                                    )
                                else
                                    it
                            }
                        )
                    }
                } catch (e: Exception) {
                    Log.e("_CommentViewModel", e.toString())
                }
            }
        } else {
            //좋아요 요청
            viewModelScope.launch {
                try {
                    val result = addCommentUseCase.invoke(commentId)
                    //좋아요 UI 업데이트
                    _uiState.update { it ->
                        it.copy(
                            list = it.list.map {
                                if (it.commentsId == comment.commentsId)
                                    comment.copy(
                                        commentLikeId = result,
                                        commentLikeCount = it.commentLikeCount + 1
                                    )
                                else
                                    it
                            }
                        )
                    }
                } catch (e: Exception) {
                    Log.e("_CommentViewModel", e.toString())
                }
            }
        }
    }

    fun onReply(comment: Comment) {
        _uiState.update { it.copy(reply = comment) }
    }

    fun onClearReply() {
        _uiState.update { it.copy(reply = null) }
    }

    fun onViewMore(commentId: Long) {
        val lastId = uiState.value.list.filter { it.parentCommentId == commentId }
            .map { it.commentsId }
            .min()
        viewModelScope.launch {
            loadMoreUseCase.invoke(lastId.toInt())
        }

    }
}