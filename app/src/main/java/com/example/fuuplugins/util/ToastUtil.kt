package com.example.fuuplugins.util

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.FuuApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ViewModel.easyToast(msg:String){
    viewModelScope.launch(Dispatchers.Main) {
        Toast.makeText(FuuApplication.instance,msg, Toast.LENGTH_SHORT).show()
    }
}



fun normalToast(msg:String){
    Toast.makeText(FuuApplication.instance,msg, Toast.LENGTH_SHORT).show()
}

suspend fun toastInSuspend(msg:String){
    withContext(Dispatchers.Main){
        Toast.makeText(FuuApplication.instance,msg, Toast.LENGTH_SHORT).show()
    }
}
