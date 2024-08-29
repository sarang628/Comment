package com.sarang.torang.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarang.torang.compose.comments.IsLoginFlowForCommentUseCase
import com.sarang.torang.data.comments.Comment
import com.sarang.torang.data.comments.isFavorite
import com.sarang.torang.uistate.Comments
import com.sarang.torang.uistate.CommentsUiState
import com.sarang.torang.uistate.findRootCommentId
import com.sarang.torang.usecase.comments.AddCommentLikeUseCase
import com.sarang.torang.usecase.comments.DeleteCommentLikeUseCase
import com.sarang.torang.usecase.comments.DeleteCommentUseCase
import com.sarang.torang.usecase.comments.GetCommentsUseCase
import com.sarang.torang.usecase.comments.GetUserUseCase
import com.sarang.torang.usecase.comments.LoadCommentsUseCase
import com.sarang.torang.usecase.comments.LoadMoreUseCase
import com.sarang.torang.usecase.comments.SendCommentUseCase
import com.sarang.torang.usecase.comments.SendReplyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 코멘트 뷰모델
 * @param getUserUseCase 유저 정보 가져오기
 * @param getCommentsUseCase 코멘트 Flow로 가져오기
 * @param loadCommentsUseCase 코멘트 불러오기
 * @param sendCommentUseCase 코멘트 작성
 * @param deleteCommentUseCase 코멘트 삭제
 * @param addCommentUseCase 코멘트 좋아요
 * @param deleteCommentLikeUseCaes 코멘트 좋아요 삭제
 * @param sendReplyUseCase 코멘트 답글 보내기
 * @param loadMoreUseCase 코멘트 더보기
 * @param isLoginFlowForCommentUseCase 로그인 여부 확인
 */
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val loadCommentsUseCase: LoadCommentsUseCase,
    private val sendCommentUseCase: SendCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val addCommentUseCase: AddCommentLikeUseCase,
    private val deleteCommentLikeUseCaes: DeleteCommentLikeUseCase,
    private val sendReplyUseCase: SendReplyUseCase,
    private val loadMoreUseCase: LoadMoreUseCase,
    private val isLoginFlowForCommentUseCase: IsLoginFlowForCommentUseCase
) : ViewModel() {
    var uiState: CommentsUiState by mutableStateOf(CommentsUiState.Loading)
        private set
    val isLogin = isLoginFlowForCommentUseCase.isLogin
    private val TAG = "__CommentViewModel"

    private val _replySingleEvent: MutableStateFlow<Comment?> = MutableStateFlow(null)
    val replySingleEvent = _replySingleEvent.asStateFlow()

    private val jobs: HashMap<Long, Boolean> = HashMap()
    private var job: Job? = null

    fun loadComment(reviewId: Int?) {
        uiState = CommentsUiState.Loading
        Log.d(TAG, "loadComment: $reviewId")
        if (reviewId == null) {
            onClear()
            return
        }

        job = viewModelScope.launch {
            try {
                loadCommentsUseCase.invoke(reviewId)

                getCommentsUseCase.invoke(reviewId = reviewId).collectLatest { list ->
                    Log.d(TAG, "CommentsUiState: Success: $list")
                    uiState = CommentsUiState.Success(Comments(list = list, reviewId = reviewId))
                }

                getUserUseCase.invoke().collectLatest { user ->
                    if (user != null) {
                        (uiState as CommentsUiState.Success).let {
                            Log.d(TAG, "CommentsUiState: Success")
                            uiState = it.copy(comments = it.comments.copy(writer = user))
                        }
                    } else {
                        (uiState as CommentsUiState.Success).let {
                            Log.d(TAG, "CommentsUiState: Success")
                            uiState = it.copy(comments = it.comments.copy(writer = null))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "error loadComment : {$e} ")
                uiState = CommentsUiState.Success(Comments(snackBarMessage = e.message))
            }
        }
    }

    fun sendComment() {
        (uiState as CommentsUiState.Success).let {
            if (it.comments.reply == null) {
                viewModelScope.launch {
                    try {
                        sendCommentUseCase.invoke(reviewId = it.comments.reviewId!!,
                            comment = it.comments.comment,
                            onLocalUpdated = {
                                uiState = it.copy(
                                    comments = it.comments.copy(
                                        movePosition = 0, // 첫번째 위치로 이동
                                        comment = "", // 입력창 초기화
                                        reply = null, // 댓글 초기화
                                    ),
                                )
                            })
                    } catch (e: Exception) {
                        uiState = it.copy(comments = it.comments.copy(snackBarMessage = e.message))
                    }
                }

            } else {
                viewModelScope.launch {
                    try {
                        val uploadComment =
                            it.comments.reply ?: throw Exception("전송할 코멘트 정보가 없습니다.")
                        sendReplyUseCase.invoke(reviewId = it.comments.reviewId!!,
                            parentCommentId = it.comments.findRootCommentId(uploadComment),
                            comment = it.comments.comment,
                            onLocalUpdated = {})
                        uiState = it.copy(
                            comments = it.comments.copy(
                                comment = "", // 입력창 초기화
                                reply = null, // 댓글 초기화
                            )
                        )
                    } catch (e: Exception) {
                        uiState = it.copy(comments = it.comments.copy(snackBarMessage = e.message))
                        Log.e("_CommentViewModel", e.toString())
                    }
                }
            }
        }
    }

    fun clearErrorMessage() {
        if (uiState is CommentsUiState.Success) {
            (uiState as CommentsUiState.Success).let {
                uiState = it.copy(comments = it.comments.copy(snackBarMessage = null))
            }
        }
    }

    fun onCommentChange(comment: String) {
        if (uiState is CommentsUiState.Success) {
            (uiState as CommentsUiState.Success).let {
                uiState = it.copy(comments = it.comments.copy(comment = comment))
            }
        }
    }

    fun onPosition() {
        if (uiState is CommentsUiState.Success) {
            (uiState as CommentsUiState.Success).let {
                uiState = it.copy(comments = it.comments.copy(movePosition = null))
            }
        }
    }

    fun onDelete(commentsId: Long) {

        if (uiState !is CommentsUiState.Success) return

        if (jobs.containsKey(commentsId) && jobs[commentsId] == true) return

        jobs[commentsId] = true
        viewModelScope.launch {
            delay(3000)
            if (jobs[commentsId] == true) {
                deleteCommentUseCase.delete(commentId = commentsId)
                uiState = (uiState as CommentsUiState.Success).copy(
                    comments = (uiState as CommentsUiState.Success).comments.copy(list = (uiState as CommentsUiState.Success).comments.list.filter { it.commentsId != commentsId })
                )
                jobs.remove(commentsId)
            }
        }
    }

    fun onUndo(commentId: Long) {
        jobs[commentId] = false
    }

    fun onFavorite(commentId: Long) {

        if (uiState !is CommentsUiState.Success) return

        // 현재 좋아요 상태인지 확인
        val comment =
            (uiState as CommentsUiState.Success).comments.list.find { it.commentsId == commentId }
                ?: return
        if (comment.isFavorite) {
            // 좋아요 삭제 요청
            viewModelScope.launch {
                try {
                    deleteCommentLikeUseCaes.invoke(commentId)
                    // 좋아요 UI 업데이트
                    uiState = (uiState as CommentsUiState.Success).copy(
                        comments = (uiState as CommentsUiState.Success).comments.copy(list = (uiState as CommentsUiState.Success).comments.list.map {
                            if (it.commentsId == comment.commentsId) comment.copy(
                                commentLikeId = null, commentLikeCount = it.commentLikeCount - 1
                            )
                            else it
                        })
                    )
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
                    uiState = (uiState as CommentsUiState.Success).copy(
                        comments = (uiState as CommentsUiState.Success).comments.copy(list = (uiState as CommentsUiState.Success).comments.list.map {
                            if (it.commentsId == comment.commentsId) comment.copy(
                                commentLikeId = result, commentLikeCount = it.commentLikeCount + 1
                            )
                            else it
                        })
                    )
                } catch (e: Exception) {
                    Log.e("_CommentViewModel", e.toString())
                }
            }
        }
    }

    fun onReply(comment: Comment) {

        if (uiState !is CommentsUiState.Success) return

        uiState = (uiState as CommentsUiState.Success).copy(
            comments = (uiState as CommentsUiState.Success).comments.copy(reply = comment)
        )
        _replySingleEvent.update { comment }
    }

    fun onClearReply() {
        if (uiState !is CommentsUiState.Success) return

        uiState = (uiState as CommentsUiState.Success).copy(
            comments = (uiState as CommentsUiState.Success).comments.copy(reply = null)
        )
    }

    fun onClear() {
        if (uiState !is CommentsUiState.Success) return

        uiState = (uiState as CommentsUiState.Success).copy(
            comments = (uiState as CommentsUiState.Success).comments.copy(
                reply = null,
                list = listOf()
            )
        )
    }

    fun onViewMore(commentId: Long) {
        if (uiState !is CommentsUiState.Success) return

        val lastId =
            (uiState as CommentsUiState.Success).comments.list.filter { it.parentCommentId == commentId }
                .map { it.commentsId }.min()
        viewModelScope.launch {
            loadMoreUseCase.invoke(lastId.toInt())
        }

    }

    fun onRequestFocus() {
    }

    fun onHidden() {
        Log.d(TAG, "onHidden")
        if (job != null && job!!.isActive)
            job!!.cancel()
    }
}