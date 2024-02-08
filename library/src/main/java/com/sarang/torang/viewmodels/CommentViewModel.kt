package com.sarang.torang.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.isFavorite
import com.sarang.torang.uistate.CommentsUiState
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
    val jobs = HashMap<Int, Boolean>()

    init {
        viewModelScope.launch {
            try {
                val user = getUserUseCase.invoke()
                _uiState.update {
                    it.copy(
                        profileImageUrl = user.profileUrl,
                        myId = user.userId,
                        name = user.userName
                    )
                }
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
        if (_uiState.value.reply == null) {
            addNewComment()
        } else {
            addReply()
        }
    }

    fun addNewComment() {
        viewModelScope.launch {
            try {
                val comment = _uiState.value.comment
                val list = ArrayList(_uiState.value.list).apply {
                    add(0, uiState.value.toComment)
                }
                //리스트 코멘트 추가 및 입력창 초기화
                _uiState.update { it.copy(list = list, onTop = true, comment = "", reply = null) }
                delay(1000) //리스트 상단으로 올라가는 시간
                list[0] = sendCommentUseCase.invoke(
                    reviewId = uiState.value.reviewId!!,
                    comment = comment
                )
                _uiState.update { it.copy(list = list) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun addReply() {
        viewModelScope.launch {
            try {
                // 하위 코멘트로 추가하기
                val modList = ArrayList(uiState.value.list)
                val selectedIndex =
                    modList.indexOf(modList.find { it.commentsId == uiState.value.reply?.commentsId })
                // 코멘트 추가
                modList.add(selectedIndex + 1, uiState.value.toComment)
                // 코멘트 입력창 초기화
                _uiState.update { it.copy(list = modList, comment = "", reply = null) }
                //TODO::위치 찾기
                modList[selectedIndex + 2] = sendReplyUseCase.invoke(
                    reviewId = uiState.value.reviewId!!,
                    parentCommentId = uiState.value.reply?.commentsId!!,
                    comment = uiState.value.comment
                )
                _uiState.update { it.copy(list = modList) }
            } catch (e: Exception) {
                Log.e("_CommentViewModel", e.toString())
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(error = null) }
    }

    fun onCommentChange(comment: String) {
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

    fun onFavorite(commentId: Int) {
        // 현재 좋아요 상태인지 확인
        val comment = uiState.value.list.find { it.commentsId == commentId } ?: return
        if (comment.isFavorite) {
            // 좋아요 삭제 요청
            viewModelScope.launch {
                try {
                    deleteCommentLikeUseCaes.invoke(commentId)
                    // 좋아요 UI 업데이트
                    _uiState.update {
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
                }
            }
        } else {
            //좋아요 요청
            viewModelScope.launch {
                try {
                    val result = addCommentUseCase.invoke(commentId)
                    //좋아요 UI 업데이트
                    _uiState.update {
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