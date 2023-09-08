package com.example.fuuplugins.activity.mainActivity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fuuplugins.R
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fuuplugins.activity.mainActivity.network.bean.Data
import com.example.fuuplugins.activity.mainActivity.repositories.test_server
import com.example.fuuplugins.activity.mainActivity.viewModel.PluginStoreViewModel

@Composable
@Preview
fun PluginsStore (
    viewModel: PluginStoreViewModel = viewModel()
){
    Box(modifier = Modifier.statusBarsPadding()){
        val navHostController = rememberNavController()
        val pluginListState = viewModel.pluginListFromNetwork.collectAsStateWithLifecycle()
        NavHost(navController = navHostController, startDestination = "list"){
            composable("list"){
                PluginIntroductionList(
                    pluginListFromNetwork = pluginListState
                ){
                    navHostController.navigate("detail")
                }
            }
            composable("detail"){
                PluginDetail()
            }
        }
    }
}

@Composable
@Preview
fun PluginDetail(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(randomColor())
            .clip(RoundedCornerShape(10.dp))
            .padding(10.dp)
    ){
        Row(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(100)),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = "空教室",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .align(Alignment.CenterVertically),
                softWrap = false,
                maxLines = 1,
                textAlign = TextAlign.End,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
            Image(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(100))
                    .background(Color.Gray)
                    .padding(5.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.FillBounds
            )
        }
        Row(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(100)),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = "开发者：theonenull",
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                softWrap = false,
                maxLines = 1,
                textAlign = TextAlign.Start,
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            PluginMarkdown()
        }
    }
}

@Composable
fun PluginIntroductionList(
    pluginListFromNetwork: State<List<Data>?>,
    click: () -> Unit = {},
){
    pluginListFromNetwork.value?.let { pluginList ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ){
            items(pluginList.size){
                PluginIntroductionItem(
                    pluginList[it],
                    click
                )
            }
        }
    }
}

@Composable
fun PluginIntroductionItem(
    plugin: Data,
    click: () -> Unit = {}
){
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                click.invoke()
            }
            .background(Color(215, 215, 237))
            .padding(10.dp)
    ){
        Row(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            AsyncImage(
                model = "${test_server}/${plugin.iconPath}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(100)),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = plugin.apkName?:"无应用名",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .align(Alignment.CenterVertically),
                softWrap = false,
                maxLines = 1,
                textAlign = TextAlign.End,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis
            )
            Image(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(100))
                    .background(Color.Gray)
                    .padding(5.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.FillBounds
            )
        }
        Row(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(100)),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = "开发者：${plugin.developer}",
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                softWrap = false,
                maxLines = 1,
                textAlign = TextAlign.Start,
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = plugin.description ?: "该插件无介绍",
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}