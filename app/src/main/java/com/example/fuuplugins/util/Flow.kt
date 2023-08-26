package com.example.fuuplugins.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

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
        Log.e("sss",it.toString())
        block.invoke(this,it)
    }
}