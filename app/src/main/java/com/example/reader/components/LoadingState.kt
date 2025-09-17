package com.example.reader.components

data class LoadingState
    (
    val status: Status,
    val message: String? = null
            )  {
    enum class Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    companion object {
        val IDLE = LoadingState(Status.IDLE)
        val LOADING = LoadingState(Status.LOADING)
        val SUCCESS = LoadingState(Status.SUCCESS)
        fun error(message: String) = LoadingState(Status.ERROR, message)
    }

}