package com.example.fuuplugins.activity.mainActivity.viewModel

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository
import com.example.fuuplugins.activity.mainActivity.ui.WhetherVerificationCode
import com.example.fuuplugins.util.debug
import com.example.fuuplugins.util.easyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class LoginPageViewModel: ViewModel() {
    val usernameState = MutableStateFlow("")
    val passwordState = MutableStateFlow("")
    val loginButtonState  = MutableStateFlow(true)
    val verificationCodeTextState = MutableStateFlow("")
    val verificationCode = MutableStateFlow<ImageBitmap?>(null)
    val verificationCodeState = MutableStateFlow(WhetherVerificationCode.LOADING)

    fun getVerificationCodeFromNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            verificationCodeState.value = WhetherVerificationCode.LOADING
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



    fun login(){
        viewModelScope.launch(Dispatchers.IO) {
            BlockLoginPageRepository.loginStudent(
                pass = passwordState.value,
                user = usernameState.value,
                captcha = verificationCodeTextState.value,
                everyErrorAction = {
                    easyToast(it.throwable.message.toString())
                    getVerificationCodeFromNetwork()
                }
            )
            .collectLatest {
                debug(it)
            }
        }
    }

}