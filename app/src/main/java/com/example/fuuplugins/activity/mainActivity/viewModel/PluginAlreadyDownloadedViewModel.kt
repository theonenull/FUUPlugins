package com.example.fuuplugins.activity.mainActivity.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.network.bean.carousel.CarouselData
import com.example.fuuplugins.activity.mainActivity.network.bean.carousel.CarouselPicture
import com.example.fuuplugins.activity.mainActivity.repositories.PluginRepository
import com.example.fuuplugins.util.NetworkResult
import com.example.fuuplugins.util.bindingNormalNetworkCollectWithError
import com.example.fuuplugins.util.normalNetworkCollectWithError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PluginAlreadyDownloadedViewModel:ViewModel() {
    val carouselData = MutableStateFlow<NetworkResult<CarouselPicture>>(NetworkResult.UNLOAD())

    init {
        refreshCarouselData()
    }

    fun refreshCarouselData(){
        viewModelScope.launch(Dispatchers.IO) {
            PluginRepository.getCarouselPictureList()
                .bindingNormalNetworkCollectWithError(carouselData)
        }
    }
}