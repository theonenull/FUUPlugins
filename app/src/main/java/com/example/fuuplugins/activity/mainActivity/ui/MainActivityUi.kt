package com.example.fuuplugins.activity.mainActivity.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.fuuplugins.activity.mainActivity.viewModel.MainActivityViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.viewModel.LoginPageViewModel
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.userDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun MainActivityUi(
    mainActivityViewModel: MainActivityViewModel = viewModel()
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "startPage") {
        composable("startPage") {
            val scope = rememberCoroutineScope()
            StartPage{
                scope.launch(Dispatchers.IO){
                    val isLogin = FuuApplication.instance.userDataStore.data.map {
                        it[UserPreferencesKey.IS_LOGIN]?:false
                    }.first()
                    withContext(Dispatchers.Main){
                        navController.navigate(
                            if(isLogin) "mainFramework" else "login"
                        ) {
                            popUpTo("startPage") { inclusive = true }
                        }
                    }
                }
            }
        }
        composable("login") {
            LoginPage(
                navigationToMainFramework = {
                    navController.navigate("mainFramework"){
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("mainFramework") {
            MainFramework()
        }
    }
}

@Composable
@Preview
fun MainActivityUiPreview(){
    MainActivityUi()
}





