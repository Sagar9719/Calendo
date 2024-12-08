package com.example.frnd_task.views.interfaces

interface CalendarApis {
    fun callStoreTaskApi(
        userId: Int,
        title: String,
        description: String,
        createdAt: String
    )

    fun deleteTaskApi(userId: Int, taskId: Int)
    fun callTaskApi()
}