package com.example.frnd_task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frnd_task.data.ApiResponse
import com.example.frnd_task.utils.safeLaunch
import com.example.frnd_task.data.ApiResponseState
import com.example.frnd_task.data.ErrorResponse
import com.example.frnd_task.data.TaskResponse
import com.example.frnd_task.network.storeTaskInvoke
import com.example.frnd_task.network.deleteTaskInvoke
import com.example.frnd_task.network.getTasksInvoke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarViewModel : ViewModel() {
    private val _storeTaskLiveData = MutableLiveData<ApiResponseState<ApiResponse>>()
    val storeTaskLiveData: LiveData<ApiResponseState<ApiResponse>> get() = _storeTaskLiveData

    private val _deleteTaskLiveData = MutableLiveData<ApiResponseState<ApiResponse>>()
    val deleteTaskLiveData: LiveData<ApiResponseState<ApiResponse>> get() = _deleteTaskLiveData

    private val _getTaskLiveData = MutableLiveData<ApiResponseState<TaskResponse>>()
    val getTaskLiveData: LiveData<ApiResponseState<TaskResponse>> get() = _getTaskLiveData

    fun storeTask(userId: Int, title: String, description: String, createdAt: String) = viewModelScope.safeLaunch({
        _storeTaskLiveData.value = ApiResponseState.loading()
        withContext(Dispatchers.IO) {
            storeTaskInvoke(userId, title, description, createdAt)
                ?.onSuccess {
                    _storeTaskLiveData.postValue(ApiResponseState.success(it))
                }
                ?.onFailure {
                    _storeTaskLiveData.postValue(
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
        _storeTaskLiveData.postValue(
            ApiResponseState.error(
                ErrorResponse(
                    code = "",
                    message = it.message,
                    null
                )
            )
        )
    })

    fun deleteTask(userId: Int, taskId: Int) = viewModelScope.safeLaunch({
        _deleteTaskLiveData.value = ApiResponseState.loading()
        withContext(Dispatchers.IO) {
            deleteTaskInvoke(userId, taskId)
                ?.onSuccess {
                    _deleteTaskLiveData.postValue(ApiResponseState.success(it))
                }
                ?.onFailure {
                    _deleteTaskLiveData.postValue(
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
        _deleteTaskLiveData.postValue(
            ApiResponseState.error(
                ErrorResponse(
                    code = "",
                    message = it.message,
                    null
                )
            )
        )
    })

    fun getTask(userId: Int) = viewModelScope.safeLaunch({
        _getTaskLiveData.value = ApiResponseState.loading()
        withContext(Dispatchers.IO) {
            getTasksInvoke(userId)
                ?.onSuccess {
                    _getTaskLiveData.postValue(ApiResponseState.success(it))
                }
                ?.onFailure {
                    _getTaskLiveData.postValue(
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
        _getTaskLiveData.postValue(
            ApiResponseState.error(
                ErrorResponse(
                    code = "",
                    message = it.message,
                    null
                )
            )
        )
    })
}

