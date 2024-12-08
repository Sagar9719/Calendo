package com.example.frnd_task.data

import com.google.gson.annotations.SerializedName

data class DeleteTaskRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("task_id")
    val taskId: Int
)
