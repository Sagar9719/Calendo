package com.example.frnd_task.data

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("created_date")
    val createdDate: String?
)