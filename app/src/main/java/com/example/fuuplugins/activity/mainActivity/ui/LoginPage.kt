package com.example.fuuplugins.activity.mainActivity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.fuuplugins.activity.mainActivity.viewModel.LoginPageViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginPage (
    viewModel: LoginPageViewModel = viewModel()
) {
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
        verificationCodeState = viewModel.verificationCodeState.collectAsState(),
        verificationCodeTextState = viewModel.verificationCodeTextState.collectAsState(),
    )
}

@Composable
@Preview
fun LoginPagePreview(){
    LoginPage()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LoginDetailsPage(
    usernameState : State<String> = remember {
        mutableStateOf("")
    },
    passwordState : State<String> = remember {
        mutableStateOf("")
    },
    loginButtonState : State<Boolean> = remember {
        mutableStateOf(true)
    },
    verificationCodeState :State<WhetherVerificationCode> = remember {
        mutableStateOf(WhetherVerificationCode.YES)
    },
    verificationCodeTextState :State<String> = remember {
        mutableStateOf("")
    },
    usernameOnValueChange:(String)->Unit = {},
    passwordOnValueChange:(String)->Unit = {},
    verificationCodeOnValueChange:(String)->Unit = {}
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
            verificationCode = verificationCodeState,
            verificationCodeOnValueChange = verificationCodeOnValueChange,
            verificationCodeText = verificationCodeTextState
        )
        Text(
            text = "登录则意味您接受西二在线的服务，并接受西二在线的监督。我们承诺对您的信息绝对保密，任何对您信息的使用我们都将经过您的同意，",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
        )

        ElevatedButton(
            onClick = { /*TODO*/ },
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 10.dp),
            modifier = Modifier
                .padding(top = 50.dp),
            enabled = usernameState.value!="" && passwordState.value != "" && loginButtonState.value && verificationCodeTextState.value != ""
        ) {
            Text(text = "LOGIN")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CaptchaLine(
    verificationCode :State<WhetherVerificationCode> = remember {
        mutableStateOf(WhetherVerificationCode.YES)
    },
    verificationCodeOnValueChange:(String) ->Unit = {

    },
    verificationCodeText :State<String> =  remember {
        mutableStateOf("")
    }
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
            targetState = verificationCode.value,
            label = "",
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
        ){
            when(it){
                WhetherVerificationCode.YES->{

                }
                WhetherVerificationCode.NO->{

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
    YES,
    NO
}