package com.example.frnd_task.data

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: ErrorData?
) {
    companion object {
        fun createEmpty() = ErrorResponse(
            code = "",
            message = "",
            ErrorData(status = "")
        )
    }
}

data class EPLErrorResponse(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
)