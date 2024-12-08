package com.example.frnd_task.data

import com.google.gson.annotations.SerializedName

data class GetTaskRequest(
    @SerializedName("user_id")
    val userId: Int
)
