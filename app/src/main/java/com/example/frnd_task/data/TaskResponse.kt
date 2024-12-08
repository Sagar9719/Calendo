package com.example.frnd_task.data

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("tasks")
    val taskDetails: List<TaskDetail>?
)
