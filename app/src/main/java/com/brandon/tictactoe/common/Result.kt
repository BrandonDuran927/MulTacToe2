package com.brandon.tictactoe.common

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Throwable) : Result<Nothing>()

    // Helpers
    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure

    fun getOrNull() : T? = (this as? Success)?.data
    fun exceptionOrNull(): Throwable? = (this as? Failure)?.exception
}