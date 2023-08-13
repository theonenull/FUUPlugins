package com.example.fuuplugins.activity.mainActivity.viewModel

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository
import com.example.fuuplugins.activity.mainActivity.ui.WhetherVerificationCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginPageViewModel: ViewModel() {
    val usernameState = MutableStateFlow("")
    val passwordState = MutableStateFlow("")
    val loginButtonState  = MutableStateFlow(true)
    val verificationCodeTextState = MutableStateFlow("")
    val verificationCode = MutableStateFlow<ImageBitmap?>(null)
    val verificationCodeState = MutableStateFlow(WhetherVerificationCode.LOADING)

    fun getVerificationCodeFromNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            BlockLoginPageRepository.getVerifyCode()
                .catch {
                    verificationCodeState.value = WhetherVerificationCode.FAIL
                }.collectLatest {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                verificationCode.value = bitmap.asImageBitmap()
                verificationCodeState.value = WhetherVerificationCode.SUCCESS
            }
        }
    }
}