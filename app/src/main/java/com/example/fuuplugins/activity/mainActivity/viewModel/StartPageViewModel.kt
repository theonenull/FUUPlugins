package com.example.fuuplugins.activity.mainActivity.viewModel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.repositories.BlockTestStartPageRepository
import com.example.fuuplugins.activity.mainActivity.repositories.DataType
import com.example.fuuplugins.activity.mainActivity.ui.StartPageShowType
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class StartPageViewModel(

) : ViewModel() {
    var currentPage = MutableStateFlow(StartPageShowType.Loading)

    var markDownContext = MutableStateFlow("")

    var imageUrl = MutableStateFlow("")

    private fun upgradeImageUrl(imageUrl:String){
        this.imageUrl.value = imageUrl
        this.currentPage.value = StartPageShowType.ImageOnly
    }

    private fun upgradeMarkDownContext(markDownContext:String){
        this.markDownContext.value = markDownContext
        this.currentPage.value = StartPageShowType.MarkDown
    }

    fun getStartPageData(
        failAction:FlowCollector<DataType>.(Throwable) -> Unit = {}
    ){
        viewModelScope.launch {
            BlockTestStartPageRepository.getStartPageData(
                failAction
            )
                .collectLatest {
                    if(it.startPageType == 1){
                        upgradeImageUrl(it.context)
                    }
                    if(it.startPageType == 2){
                        upgradeMarkDownContext(it.context)
                    }
                }
        }
    }
}


