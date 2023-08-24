package com.example.fuuplugins.activity.mainActivity.ui

import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fuuplugins.activity.mainActivity.viewModel.LoginPageViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.material.AnimationEffectData
import com.example.material.ButtonState
import com.example.material.LoadableButton

@Composable
fun LoginPage (
    viewModel: LoginPageViewModel = viewModel(),
    navigationToMainFramework:()->Unit = {}
) {
    LaunchedEffect(Unit){
        viewModel.checkIsLogin(navigationToMainFramework)
        viewModel.getVerificationCodeFromNetwork()
    }
    LoginDetailsPage(
        usernameState = viewModel.usernameState.collectAsState(),
        passwordState = viewModel.passwordState.collectAsState(),
        loginButtonState = viewModel.loginButtonState.collectAsState(),
        usernameOnValueChange = {
            viewModel.usernameState.value = it
        },
        passwordOnValueChange = {
            viewModel.passwordState.value = it
        },
        verificationCodeOnValueChange = {
            viewModel.verificationCodeTextState.value = it
        },
        verificationCode = viewModel.verificationCode.collectAsState(),
        verificationCodeTextState = viewModel.verificationCodeTextState.collectAsState(),
        verificationCodeState = viewModel.verificationCodeState.collectAsState(),
        retryGetVerificationCode = {
            viewModel.getVerificationCodeFromNetwork()
        },
        login = { viewModel.login(loginSuccessful = navigationToMainFramework, loginFailed = { -> }) },
    )
}

@Composable
@Preview
fun LoginPagePreview(){
    LoginPage(

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LoginDetailsPage(
    usernameState: State<String> = remember {
        mutableStateOf("")
    },
    passwordState: State<String> = remember {
        mutableStateOf("")
    },
    loginButtonState: State<Boolean> = remember {
        mutableStateOf(true)
    },
    verificationCodeState:State<WhetherVerificationCode> = remember {
        mutableStateOf(WhetherVerificationCode.FAIL)
    },
    verificationCode: State<ImageBitmap?> = remember {
        mutableStateOf(null)
    },
    verificationCodeTextState:State<String> = remember {
        mutableStateOf("")
    },
    usernameOnValueChange:(String)->Unit = {},
    passwordOnValueChange:(String)->Unit = {},
    verificationCodeOnValueChange:(String)->Unit = {},
    retryGetVerificationCode: () -> Unit = {},
    login : () -> Unit = {}
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 50.dp, horizontal = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = usernameState.value,
            onValueChange = usernameOnValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            label = {
               Text("账号")
            },
            maxLines = 1,
            singleLine = true
        )
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = passwordOnValueChange,
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
                ,
            label = {
                Text("密码")
            },
            maxLines = 1,
            singleLine = true
        )
        CaptchaLine(
            verificationCodeState = verificationCodeState,
            verificationCodeOnValueChange = verificationCodeOnValueChange,
            verificationCodeText = verificationCodeTextState,
            verificationCode = verificationCode,
            retryGetVerificationCode = retryGetVerificationCode
        )
        Text(
            text = "登录则意味您接受西二在线的服务，并接受西二在线的监督。我们承诺对您的信息绝对保密，任何对您信息的使用我们都将经过您的同意，",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
        )
        val buttonState by remember {
            derivedStateOf{
                when(loginButtonState.value){
                    true -> ButtonState.Normal
                    false ->  ButtonState.Loading
                }
            }
        }
        LoadableButton(
            onClick = login,
            buttonState = buttonState,
            enabled = usernameState.value!="" && passwordState.value != "" && loginButtonState.value && verificationCodeTextState.value != "",
            normalContent = {
                Text(text = "LOGIN")
            },
            modifier = Modifier
                .padding(top = 30.dp),
            loadingContent = {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            },
            loadingIntSize = AnimationEffectData(50,50),
            normalIntSize = AnimationEffectData(150,50)
        )

//        ElevatedButton(
//            onClick = login,
//            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 10.dp),
//            modifier = Modifier
//                .padding(top = 50.dp),
//            enabled = usernameState.value!="" && passwordState.value != "" && loginButtonState.value && verificationCodeTextState.value != ""
//        ) {
//            Text(text = "LOGIN")
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CaptchaLine(
    verificationCodeState:State<WhetherVerificationCode> = remember {
        mutableStateOf(WhetherVerificationCode.FAIL)
    },
    verificationCodeOnValueChange:(String) ->Unit = {

    },
    verificationCodeText:State<String> =  remember {
        mutableStateOf("")
    },
    verificationCode: State<ImageBitmap?> = remember {
        mutableStateOf(null)
    },
    retryGetVerificationCode: ()->Unit={}
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 30.dp)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Crossfade(
            targetState = verificationCodeState.value,
            label = "",
            modifier = Modifier
                .padding(end = 10.dp)
                .weight(1f)
                .wrapContentHeight()
        ){
            when(it){
                WhetherVerificationCode.SUCCESS->{
                    Image(
                        bitmap = verificationCode.value!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable {
                                retryGetVerificationCode.invoke()
                            },
                        contentDescription = null
                    )
                }
                WhetherVerificationCode.FAIL->{
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.Red)
                            .clickable {
                                retryGetVerificationCode.invoke()
                            },
                    ){
                        Text(
                            text = "加载失败，点击重试",
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
                WhetherVerificationCode.LOADING->{
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.LightGray),
                    ){
                        Text(
                            text = "正在加载",
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
        TextField(
            value = verificationCodeText.value,
            onValueChange = verificationCodeOnValueChange,
            maxLines = 1,
            modifier = Modifier
                .weight(1f),
            label = {
                Text(text = "验证码")
            }
        )

    }

}

enum class WhetherVerificationCode{
    SUCCESS,
    FAIL,
    LOADING
}

@Composable
fun byteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    return bitmap.asImageBitmap()
}