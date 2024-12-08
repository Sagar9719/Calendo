package com.example.frnd_task.data

data class ApiResponseState<T>(val status: Status, val data: T?, val errorResponse: ErrorResponse?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        fun <T> success(data: T): ApiResponseState<T> {
            return ApiResponseState(Status.SUCCESS, data, null)
        }

        fun <T> error(errorResponse: ErrorResponse): ApiResponseState<T> {
            return ApiResponseState(Status.ERROR, null, errorResponse)
        }

        fun <T> loading(): ApiResponseState<T> {
            return ApiResponseState(Status.LOADING, null, null)
        }
    }
}