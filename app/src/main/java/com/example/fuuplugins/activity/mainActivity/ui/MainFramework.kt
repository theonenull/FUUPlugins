package com.example.fuuplugins.activity.mainActivity.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
                PermanentDrawerSheet(Modifier.width(300.dp)) {
                    Spacer(Modifier.height(12.dp))
                    iconItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(item.name) },
                            selected = item == iconSelectedItem.value,
                            onClick = {
                                iconSelectedItem.value = item
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
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
                        }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
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