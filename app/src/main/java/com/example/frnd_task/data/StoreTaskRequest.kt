package com.example.frnd_task.data

import com.google.gson.annotations.SerializedName

data class StoreTaskRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("task")
    val task: Task
)
