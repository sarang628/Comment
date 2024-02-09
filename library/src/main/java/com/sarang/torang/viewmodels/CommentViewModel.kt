package com.sarang.torang.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.isFavorite
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.selectedIndex
import com.sarang.torang.uistate.selectedReplyIndex
import com.sarang.torang.uistate.toComment
import com.sarang.torang.usecase.comments.AddCommentLikeUseCase
import com.sarang.torang.usecase.comments.DeleteCommentLikeUseCase
import com.sarang.torang.usecase.comments.DeleteCommentUseCase
import com.sarang.torang.usecase.comments.GetCommentsUseCase
import com.sarang.torang.usecase.comments.GetUserUseCase
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
    private val sendReplyUseCase: SendReplyUseCase
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
                _uiState.update {
                    it.copy(
                        list = getCommentsUseCase.invoke(reviewId = reviewId),
                        reviewId = reviewId
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackBarMessage = e.message) }
            }
        }
    }

    fun sendComment() {
        if (uiState.value.uploadingComment != null) {
            _uiState.update { it.copy(snackBarMessage = "업로드 중입니다.") }
            return
        }

        if (_uiState.value.reply == null) {
            //리스트 코멘트 추가 및 입력창 초기화 상단으로 보내고 기다리기
            val uploadComment = uiState.value.toComment
            _uiState.update {
                it.copy(
                    list = ArrayList(it.list).apply { add(0, uploadComment) },
                    comment = "",
                    reply = null,
                    uploadingComment = uploadComment,
                    movePosition = 0
                )
            }
        } else {
            // 하위 코멘트로 추가하기
            val modList = ArrayList(uiState.value.list)
            val uploadComment = uiState.value.toComment
            val selectedIndex = uiState.value.selectedIndex
            // 코멘트 추가
            modList.add(selectedIndex + 1, uploadComment)
            _uiState.update {
                it.copy(
                    list = modList,
                    comment = "",
                    reply = null,
                    movePosition = selectedIndex,
                    uploadingComment = uploadComment
                )
            }
        }
    }

    private fun addNewComment(comment: Comment) {
        viewModelScope.launch {
            try {
                val list = ArrayList(_uiState.value.list)
                list[0] = sendCommentUseCase.invoke(
                    reviewId = uiState.value.reviewId!!,
                    comment = comment.comment
                )
                _uiState.update { it.copy(list = list, uploadingComment = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackBarMessage = e.message) }
            }
        }
    }

    private fun addReply(comment: Comment) {
        viewModelScope.launch {
            try {
                val modList = ArrayList(uiState.value.list)
                val selectedIndex = uiState.value.selectedReplyIndex
                modList[selectedIndex + 1] = sendReplyUseCase.invoke(
                    reviewId = uiState.value.reviewId!!,
                    parentCommentId = comment.parentCommentId!!,
                    comment = comment.comment
                )
                _uiState.update { it.copy(list = modList, uploadingComment = null) }
            } catch (e: Exception) {
                Log.e("_CommentViewModel", e.toString())
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
        uiState.value.uploadingComment?.let {
            if (it.parentCommentId != null) {
                addReply(it)
            } else {
                addNewComment(it)
            }
        }
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
}