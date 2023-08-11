package com.example.fuuplugins.activity.mainActivity.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainFramework(){
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val mainFrameworkScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val gesturesEnabled by remember {
            derivedStateOf{
                drawerState.isOpen
            }
        }
        val iconItems = listOf(Icons.Default.Favorite, Icons.Default.Face, Icons.Default.Email)
        val iconSelectedItem = remember { mutableStateOf(iconItems[0]) }
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            drawerContent  = {
                PersonPage()
            },
            content = {
                var selectedItem by remember { mutableStateOf(0) }
                Column {
                    TopAppBar(
                        navigationIcon = {

                        },
                        title = {
                            Text(text = ListOfPages[selectedItem])
                        },
                        actions = {
                            IconButton(onClick = {
                                mainFrameworkScope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null)
                            }
                            IconButton(onClick = {
                                mainFrameworkScope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                            }
                        }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ){
                        AnimatedContent(
                            targetState = selectedItem,
                            label = "",
                            modifier = Modifier,
                            transitionSpec = {
                                // Compare the incoming number with the previous number.
                                if (targetState > initialState) {
                                    // If the target number is larger, it slides up and fades in
                                    // while the initial (smaller) number slides up and fades out.
                                    slideInHorizontally { width -> width } + fadeIn() with
                                            slideOutHorizontally { width -> -width } + fadeOut()
                                } else {
                                    // If the target number is smaller, it slides down and fades in
                                    // while the initial number slides down and fades out.
                                    slideInHorizontally { width -> -width } + fadeIn() with
                                            slideOutHorizontally { width -> width } + fadeOut()
                                }.using(
                                    // Disable clipping since the faded slide-in/out should
                                    // be displayed out of bounds.
                                    SizeTransform(clip = false)
                                )
                            }
                        ) {
                            when (it){
                                0 -> {
                                    ClassSchedule()
                                }
                                1 -> {
                                    PluginTool()
                                }
                                2 -> {
                                    PluginsStore()
                                }
                            }
                        }
                    }
                    NavigationBar {
                        ListOfPages.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    when(index){
                                        0 -> Icon(Icons.Filled.DateRange, contentDescription = item)
                                        1 -> Icon(Icons.Filled.Share, contentDescription = item)
                                        2 -> Icon(Icons.Filled.Favorite, contentDescription = item)
                                    }
                                },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
@Preview
fun MainFrameworkPreview(){
    MainFramework()
}

val ListOfPages = listOf("课表", "工具箱", "表白墙")