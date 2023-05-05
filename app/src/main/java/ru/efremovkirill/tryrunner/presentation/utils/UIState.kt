package ru.efremovkirill.tryrunner.presentation.utils

sealed class UIState <T> (
    val status: Status,
    val message: String? = null,
    val data: T? = null
) {
    class Loading <T> (message: String? = null): UIState<T>(
        status = Status.LOADING,
        message = message
    )
    class Success <T> (message: String? = null, data: T): UIState<T>(
        status = Status.SUCCESS,
        message = message,
        data = data
    )
    class Error <T> (message: String, data: T? = null): UIState<T>(
        status = Status.ERROR,
        message = message,
        data = data
    )

    class Idle <T> : UIState<T>(
        status = Status.IDLE
    )
}