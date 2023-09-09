package com.example.fuuplugins.util


interface NetworkResult<T>{

    data class SUCCESS<T>(
        val data : T
    ): NetworkResult<T>

    data class LOADING<T>(
        val massage : String
    ): NetworkResult<T>

    data class ERROR<T>(
        val error : Throwable
    ): NetworkResult<T>

    class UNLOAD<T>():NetworkResult<T>
}