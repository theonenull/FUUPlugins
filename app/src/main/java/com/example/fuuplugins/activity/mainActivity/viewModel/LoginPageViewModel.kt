package com.example.fuuplugins.activity.mainActivity.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.fuuplugins.activity.mainActivity.ui.WhetherVerificationCode
import kotlinx.coroutines.flow.MutableStateFlow

class LoginPageViewModel: ViewModel() {
    val usernameState = MutableStateFlow("")
    val passwordState = MutableStateFlow("")
    val loginButtonState  = MutableStateFlow(true)
    val verificationCodeTextState = MutableStateFlow("")
    val verificationCodeState = MutableStateFlow(WhetherVerificationCode.NO)
}