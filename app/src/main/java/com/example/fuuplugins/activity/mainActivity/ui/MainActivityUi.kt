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


@Composable
fun MainActivityUi(
    mainActivityViewModel: MainActivityViewModel = viewModel()
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "startPage") {
        composable("startPage") {
            StartPage()
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

@Composable
fun MainFramework(){
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Songs", "Artists", "Playlists")
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackHandler {

        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index }
                )
            }
        }
    }

}




