package com.dinder.rihla.rider.common

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    class Success<T>(val value: T) : Result<T>()
    class Error(val message: String) : Result<Nothing>()
}
