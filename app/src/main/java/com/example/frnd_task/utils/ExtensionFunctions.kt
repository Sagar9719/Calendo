package com.example.frnd_task.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.safeLaunch(
    launchBody: suspend () -> Unit,
    handleError: (t: Throwable) -> Unit
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.i("CoroutineExceptionHandler", "safeLaunch: " + throwable.message)
        handleError(throwable)
    }

    return this.launch(exceptionHandler) {
        launchBody()
    }
}