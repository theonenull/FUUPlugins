package com.example.fuuplugins.activity.mainActivity.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.network.bean.pluginItemBean.PluginItemBean
import com.example.fuuplugins.activity.mainActivity.repositories.PluginRepository
import com.example.fuuplugins.util.NetworkResult
import com.example.fuuplugins.util.catchWithMassage
import com.example.fuuplugins.util.easyToast
import com.example.fuuplugins.util.normalNetworkCollectWithError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PluginPageViewModel:ViewModel() {
    val pluginListFromNetwork = MutableStateFlow<List<PluginItemBean>?>(null)
    init {
        viewModelScope.launch(Dispatchers.IO) {
            PluginRepository.getPluginList()
                .catchWithMassage {
                    easyToast("连接错误")
                }
                .collect{
                    it.let {
                        pluginListFromNetwork.value = it.pluginList
                    }
                }
        }

    }
}
class PluginStoreViewModel:ViewModel(){
    val md = MutableStateFlow<NetworkResult<String>>(NetworkResult.UNLOAD())
    val plugin = MutableStateFlow<NetworkResult<PluginItemBean>>(NetworkResult.UNLOAD())

    fun getDataById(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            PluginRepository.getPluginMd(id)
                .normalNetworkCollectWithError(
                    error = {
                        md.value = NetworkResult.ERROR(it)
                    },
                    loadingAction = {
                        md.value = NetworkResult.LOADING("正在加载")
                    },
                    success = {
                        md.value = NetworkResult.SUCCESS(it)
                    },
                )
        }
    }

    fun getJsonById(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            PluginRepository.getPluginJson(id)
                .normalNetworkCollectWithError(
                    error = {
                        plugin.value = NetworkResult.ERROR(it)
                    },
                    loadingAction = {
                        plugin.value = NetworkResult.LOADING("正在加载")
                    }
                ) {
                    plugin.value = NetworkResult.SUCCESS(it)
                }
        }
    }

    fun downLoadPlugin(id: String){
        viewModelScope.launch(Dispatchers.IO) {

        }
    }
}
