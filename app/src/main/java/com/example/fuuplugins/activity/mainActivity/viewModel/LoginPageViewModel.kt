package com.example.fuuplugins.activity.mainActivity.viewModel

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository.checkTheUserInformation
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository.loadCookieData
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository.loginByTokenForIdInUrl
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository.loginStudent
import com.example.fuuplugins.activity.mainActivity.repositories.LoginResult
import com.example.fuuplugins.activity.mainActivity.ui.WhetherVerificationCode
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.setUserDataStore
import com.example.fuuplugins.config.dataStore.userDataStore
import com.example.fuuplugins.util.catchWithMassage
import com.example.fuuplugins.util.easyToast
import com.example.fuuplugins.util.flowIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginPageViewModel: ViewModel() {
    val usernameState = MutableStateFlow("102101624")
    val passwordState = MutableStateFlow("351172abc2015@")
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

    fun <T> Flow<T>.catchWithMassageAndRefreshVerificationCodeState(block: FlowCollector<T>.(Throwable)->Unit):Flow<T>{
        getVerificationCodeFromNetwork()
        return this.catchWithMassage(block)
    }

    fun checkIsLogin(navigationToMainFramework: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isLogin = FuuApplication.instance.userDataStore.data.map {
                it[UserPreferencesKey.USER_IS_LOGIN] ?: false
            }.first()
            if (isLogin){
                withContext(Dispatchers.Main){
                    navigationToMainFramework.invoke()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun login(
        loginSuccessful:() -> Unit = {},
        loginFailed :() -> Unit = {}
    ){
        viewModelScope.launch(Dispatchers.IO) {
            loginButtonState.value = false
            val username = usernameState.value
            val password = passwordState.value
            setUserDataStore(UserPreferencesKey.USER_USERNAME,username)
            setUserDataStore(UserPreferencesKey.USER_PASSWORD,password)
            loginStudent(
                pass = passwordState.value,
                user = usernameState.value,
                captcha = verificationCodeTextState.value,
                everyErrorAction = {
                    easyToast(it.throwable.message.toString())
                    getVerificationCodeFromNetwork()
                    loginButtonState.value = true
                }
            )
                .flatMapConcat {
//                    Log.d("ssss",it.body()?.string().toString() ?: "")
                    loginByTokenForIdInUrl(
                        result = it,
                        failedToGetAccount = {
                            easyToast(it.message.toString())
                        },
                        elseMistake = { error ->
                            easyToast(error.message.toString())
                        }
                    ).retryWhen{ error,tryTime ->
                        error.message == "获取account失败" && tryTime <= 3
                    }
                            .catchWithMassage {
                                if(it.message == "获取account失败"){
                                    easyToast(it.message.toString())
                                }
                                else{
                                    easyToast(it.message.toString())
                                }
                            }.flowIO()
                }
                .flatMapConcat {
                    loadCookieData(
                        queryMap = it,
                        user = username
                    )
                    .catchWithMassage {
                        loginButtonState.value = true
                        getVerificationCodeFromNetwork()
                    }
                }
                .flatMapConcat {
                    checkTheUserInformation(
                        user = username,
                        serialNumberHandling = {

                        }
                    ).catchWithMassage {
                        loginButtonState.value = true
                        getVerificationCodeFromNetwork()
                    }
                }
                .collect{ loginResult ->
                    when(loginResult){
                        LoginResult.LoginError->{
                            easyToast("登录失败,请重新登录")
                            loginButtonState.value = true
                            getVerificationCodeFromNetwork()
                            loginFailed.invoke()
                        }
                        LoginResult.LoginSuccess->{
                            easyToast("登录成功")
                            FuuApplication.instance.userDataStore.edit {
                                it[UserPreferencesKey.USER_IS_LOGIN] = true
                            }
                            withContext(Dispatchers.Main){
                                loginSuccessful.invoke()
                            }
                        }
                    }
                }
        }
    }

}