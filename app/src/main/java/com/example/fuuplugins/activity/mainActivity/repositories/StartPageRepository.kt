package com.example.fuuplugins.activity.mainActivity.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


interface StartPageRepository {
    abstract fun getStartPageData( failAction: FlowCollector<DataType>.(Throwable)->Unit ): Flow<DataType>
}

class DataType(
    val startPageType : Int,
    val context: String
)

object BlockTestStartPageRepository : StartPageRepository {
    override fun getStartPageData(failAction: FlowCollector<DataType>.(Throwable) -> Unit): Flow<DataType> {
        return flow {
            emit(
                DataType(
                    1,
                    "${test_server}/start/page"
                )
            )
        }.catch {
            failAction.invoke(this,it)
        }
    }
}