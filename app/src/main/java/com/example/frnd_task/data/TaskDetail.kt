package com.example.frnd_task.data

import com.google.gson.annotations.SerializedName

data class TaskDetail(
    @SerializedName("task_id")
    val taskId: Int?,
    @SerializedName("task_detail")
    val taskModel: Task?,
)
