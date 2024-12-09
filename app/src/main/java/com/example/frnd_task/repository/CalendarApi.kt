package com.example.frnd_task.repository

import com.example.frnd_task.data.ApiResponse
import com.example.frnd_task.data.DeleteTaskRequest
import com.example.frnd_task.data.GetTaskRequest
import com.example.frnd_task.data.StoreTaskRequest
import com.example.frnd_task.data.TaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CalendarApi {
    @POST("api/storeCalendarTask")
    suspend fun addTask(@Body request: StoreTaskRequest): Response<ApiResponse>

    @POST("api/deleteCalendarTask")
    suspend fun deleteTask(@Body request: DeleteTaskRequest): Response<ApiResponse>

    @POST("api/getCalendarTaskList")
    suspend fun getTasks(@Body request: GetTaskRequest): Response<TaskResponse>
}