package com.example.fuuplugins.util

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.FuuApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun ViewModel.easyToast(msg:String){
    viewModelScope.launch(Dispatchers.Main) {
        Toast.makeText(FuuApplication.instance,msg, Toast.LENGTH_SHORT).show()
    }
}



fun normalToast(msg:String){
    Toast.makeText(FuuApplication.instance,msg, Toast.LENGTH_SHORT).show()
}
