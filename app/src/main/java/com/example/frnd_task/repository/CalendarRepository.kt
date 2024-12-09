package com.example.frnd_task.repository

import com.example.frnd_task.data.ApiResponse
import com.example.frnd_task.data.TaskResponse

interface CalendarRepository {
    suspend fun storeTaskInvoke(
        userId: Int,
        title: String,
        description: String,
        createdAt: String
    ): Result<ApiResponse>?

    suspend fun deleteTaskInvoke(
        userId: Int,
        taskId: Int
    ): Result<ApiResponse>?

    suspend fun getTasksInvoke(
        userId: Int
    ): Result<TaskResponse>?
}
