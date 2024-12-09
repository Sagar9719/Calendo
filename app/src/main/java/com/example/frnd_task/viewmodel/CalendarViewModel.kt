package com.example.frnd_task.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frnd_task.data.ApiResponse
import com.example.frnd_task.data.ApiResponseState
import com.example.frnd_task.data.ErrorResponse
import com.example.frnd_task.data.TaskResponse
import com.example.frnd_task.repository.CalendarRepository
import com.example.frnd_task.utils.safeLaunch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
): ViewModel() {
    private val _storeTaskSharedFlow: MutableSharedFlow<ApiResponseState<ApiResponse>> =
        MutableSharedFlow(replay = 0)
    val storeTaskSharedFlow: SharedFlow<ApiResponseState<ApiResponse>> = _storeTaskSharedFlow

    private val _deleteTaskSharedFlow: MutableSharedFlow<ApiResponseState<ApiResponse>> =
        MutableSharedFlow(replay = 0)
    val deleteSharedFlow: SharedFlow<ApiResponseState<ApiResponse>> = _deleteTaskSharedFlow

    private val _getTaskSharedFlow: MutableSharedFlow<ApiResponseState<TaskResponse>> =
        MutableSharedFlow(replay = 0)
    val getTaskSharedFlow: SharedFlow<ApiResponseState<TaskResponse>> = _getTaskSharedFlow

    fun storeTask(userId: Int, title: String, description: String, createdAt: String) =
        viewModelScope.safeLaunch({
            _storeTaskSharedFlow.emit(ApiResponseState.loading())
            withContext(Dispatchers.IO) {
                calendarRepository.storeTaskInvoke(userId, title, description, createdAt)
                    ?.onSuccess {
                        _storeTaskSharedFlow.emit(ApiResponseState.success(it))
                    }
                    ?.onFailure {
                        _storeTaskSharedFlow.emit(
                            ApiResponseState.error(
                                ErrorResponse(
                                    code = "",
                                    message = it.message,
                                    null
                                )
                            )
                        )
                    }
            }
        }, {
            viewModelScope.launch {
                _storeTaskSharedFlow.emit(
                    ApiResponseState.error(
                        ErrorResponse(
                            code = "",
                            message = it.message,
                            null
                        )
                    )
                )
            }
        })

    fun deleteTask(userId: Int, taskId: Int) = viewModelScope.safeLaunch({
        _deleteTaskSharedFlow.emit(ApiResponseState.loading())
        withContext(Dispatchers.IO) {
            calendarRepository.deleteTaskInvoke(userId, taskId)
                ?.onSuccess {
                    _deleteTaskSharedFlow.emit(ApiResponseState.success(it))
                }
                ?.onFailure {
                    _deleteTaskSharedFlow.emit(
                        ApiResponseState.error(
                            ErrorResponse(
                                code = "",
                                message = it.message,
                                null
                            )
                        )
                    )
                }
        }
    }, {
        viewModelScope.launch {
            _deleteTaskSharedFlow.emit(
                ApiResponseState.error(
                    ErrorResponse(
                        code = "",
                        message = it.message,
                        null
                    )
                )
            )
        }
    })

    fun getTask(userId: Int) = viewModelScope.safeLaunch({
        _getTaskSharedFlow.emit(ApiResponseState.loading())
        withContext(Dispatchers.IO) {
            calendarRepository.getTasksInvoke(userId)
                ?.onSuccess {
                    _getTaskSharedFlow.emit(ApiResponseState.success(it))
                }
                ?.onFailure {
                    _getTaskSharedFlow.emit(
                        ApiResponseState.error(
                            ErrorResponse(
                                code = "",
                                message = it.message,
                                null
                            )
                        )
                    )
                }
        }
    }, {
        viewModelScope.launch {
            _getTaskSharedFlow.emit(
                ApiResponseState.error(
                    ErrorResponse(
                        code = "",
                        message = it.message,
                        null
                    )
                )
            )
        }
    })
}

