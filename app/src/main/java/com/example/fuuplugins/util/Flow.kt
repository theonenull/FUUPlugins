package com.example.fuuplugins.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlin.experimental.ExperimentalTypeInference


fun <T> Flow<T>.flowIO() : Flow<T>{
    return this.flowOn(Dispatchers.IO)
}
fun <T> Flow<T>.flowDefault() : Flow<T>{
    return this.flowOn(Dispatchers.Default)
}
fun <T> Flow<T>.flowUnconfined() : Flow<T>{
    return this.flowOn(Dispatchers.Unconfined)
}

fun <T>Flow<T>.catchWithMassage(block:FlowCollector<T>.(Throwable)->Unit) : Flow<T>{
    return this.catch {
        Log.e("flow throwable",it.toString())
        block.invoke(this,it)
    }
}


suspend fun <T>Flow<T>.normalNetworkCollectWithError(
    error: FlowCollector<T>.(Throwable) -> Unit,
    loadingAction: (FlowCollector<T>) -> Unit = {},
    success: (T) -> Unit = {}
){
    this
        .onStart {
            loadingAction.invoke(this)
        }
        .catchWithMassage(error)
    .collect{
        success.invoke(it)
    }
}


suspend fun <T>Flow<T>.bindingNormalNetworkCollectWithError(
    state : MutableStateFlow<NetworkResult<T>>
){
    this
        .onStart {
            state.value = NetworkResult.UNLOAD()
        }
        .catchWithMassage{
            state.value = NetworkResult.ERROR(it)
        }
        .collect{
            state.value = NetworkResult.SUCCESS(it)
        }
}


