package com.example.fuuplugins.activity.mainActivity.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import com.example.fuuplugins.activity.mainActivity.viewModel.MainActivityViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.dao.clearDb
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.setUserDataStore
import com.example.fuuplugins.config.dataStore.userDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun MainActivityUi(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    activityToMarkdownActivity: () -> Unit = {},
    activityToNetworkActivity: () -> Unit = {},
    downloadPlugin:(String,()->Unit,()->Unit,(Throwable)->Unit) -> Unit = { _, _,_, _ -> }
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "startPage") {
        composable("startPage") {
            val scope = rememberCoroutineScope()
            StartPage{
                scope.launch(Dispatchers.IO){
                    val isLogin = FuuApplication.instance.userDataStore.data.map {
                        it[UserPreferencesKey.USER_IS_LOGIN]?:false
                    }.first()
                    withContext(Dispatchers.Main){
                        navController.navigate(
                            if(isLogin ) "mainFramework" else "login"
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
            val scope = rememberCoroutineScope()
            MainFramework(
                navigationToLogin = {
                    scope.launch(Dispatchers.IO) {
                        clearDb()
                        setUserDataStore(UserPreferencesKey.USER_IS_LOGIN,false)
                        withContext(Dispatchers.Main){
                            navController.navigate("login"){
                                popUpTo("mainFramework") { inclusive = true }
                            }
                        }
                    }
                },
                activityToMarkdownActivity = activityToMarkdownActivity,
                downloadPlugin = downloadPlugin,
            )
        }
    }
}






