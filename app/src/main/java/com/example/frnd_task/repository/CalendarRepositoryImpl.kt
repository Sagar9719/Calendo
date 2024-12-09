package com.example.frnd_task.repository

import com.example.frnd_task.data.ApiResponse
import com.example.frnd_task.data.DeleteTaskRequest
import com.example.frnd_task.data.GetTaskRequest
import com.example.frnd_task.data.StoreTaskRequest
import com.example.frnd_task.data.Task
import com.example.frnd_task.data.TaskResponse
import retrofit2.Response
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val calendarApi: CalendarApi
): CalendarRepository{

    override suspend fun storeTaskInvoke(
        userId: Int,
        title: String,
        description: String,
        createdAt: String
    ): Result<ApiResponse>? {
        val task = Task(title, description, createdAt)
        val request = StoreTaskRequest(userId = userId, task = task)

        return try {
            val response: Response<ApiResponse> = calendarApi.addTask(request)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    Result.success(value = data)
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown Error"
                Result.failure(exception = Exception(errorBody))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(exception = e)
        }
    }

    override suspend fun deleteTaskInvoke(
        userId: Int,
        taskId: Int
    ): Result<ApiResponse>? {
        val request = DeleteTaskRequest(userId = userId, taskId = taskId)

        return try {
            val response: Response<ApiResponse> = calendarApi.deleteTask(request)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    Result.success(value = data)
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown Error"
                Result.failure(exception = Exception(errorBody))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(exception = e)
        }
    }

    override suspend fun getTasksInvoke(
        userId: Int
    ): Result<TaskResponse>? {
        val request = GetTaskRequest(userId = userId)

        return try {
            val response: Response<TaskResponse> = calendarApi.getTasks(request)

            if (response.isSuccessful) {
                response.body()?.let { data ->
                    Result.success(data)
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown Error"
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
