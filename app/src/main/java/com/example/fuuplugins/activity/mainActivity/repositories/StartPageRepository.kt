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
                    "https://picx.zhimg.com/80/v2-73ad2b662bd48498edd1b2499b29c88d_720w.webp?source=1940ef5c"
                )
            )
        }.catch {
            failAction.invoke(this,it)
        }
    }
}