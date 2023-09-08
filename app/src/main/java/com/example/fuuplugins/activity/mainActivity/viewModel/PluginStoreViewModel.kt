package com.example.fuuplugins.activity.mainActivity.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.network.bean.Data
import com.example.fuuplugins.activity.mainActivity.network.bean.PluginListFromNetwork
import com.example.fuuplugins.activity.mainActivity.repositories.PluginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PluginStoreViewModel:ViewModel() {
    val pluginListFromNetwork = MutableStateFlow<List<Data>?>(null)
    init {
        viewModelScope.launch(Dispatchers.IO) {
            PluginRepository.getPluginList()
                .collect{
                    it?.let {
                        pluginListFromNetwork.value = it.data
                    }

                }
        }
    }
}