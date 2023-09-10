package com.example.fuuplugins.activity.mainActivity.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.R
import com.example.fuuplugins.activity.composePluginActivity.ComposePluginActivity
import com.example.fuuplugins.activity.mainActivity.network.bean.carousel.CarouselPicture
import com.example.fuuplugins.activity.mainActivity.repositories.test_server
import com.example.fuuplugins.activity.mainActivity.viewModel.PluginAlreadyDownloadedViewModel
import com.example.fuuplugins.activity.networkPluginActivity.NetworkPluginActivity
import com.example.fuuplugins.plugin.Plugin
import com.example.fuuplugins.plugin.PluginState
import com.example.fuuplugins.util.NetworkResult
import com.example.fuuplugins.util.ShowUiWithNetworkResult
import com.example.fuuplugins.util.normalToast
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import java.io.File
import java.util.Timer
import java.util.TimerTask



@Composable
@Preview
fun PluginTool(
    viewModel : PluginAlreadyDownloadedViewModel = viewModel()
){
    val showPlugin = remember {
        mutableStateOf<Plugin?>(null)
    }
    Box(modifier = Modifier
        .statusBarsPadding()
    ){
        PluginAlreadyDownloaded(
            pluginLongClick = {
                showPlugin.value = it
            },
            refreshCarousel = {
                viewModel.refreshCarouselData()
            },
            carouselState = viewModel.carouselData.collectAsStateWithLifecycle(),
        )
        showPlugin.value?.let {
            PluginDialog(
                it,
                onDismissRequest = {
                    showPlugin.value = null
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun Carousel(
    modifier: Modifier = Modifier,
    refreshCarousel:()->Unit = {},
    state: State<NetworkResult<CarouselPicture>>
) {
    val content = LocalContext.current
    val virtualCount = Int.MAX_VALUE
    val actualCount = 10
    //初始图片下标
    val initialIndex = virtualCount / 2
    val pageState = rememberPagerState(initialPage = initialIndex)
    //改变地方在这里
    val coroutineScope= rememberCoroutineScope()
    DisposableEffect(pageState.currentPage,state) {
        val timer = Timer()
        if(state.value is NetworkResult.SUCCESS ){
            if( (state.value as NetworkResult.SUCCESS<CarouselPicture>).data.data.size>1){
                timer.schedule(object : TimerTask(){
                    override fun run() {
                        coroutineScope.launch {
                            pageState.animateScrollToPage(pageState.currentPage+1)
                        }
                    }
                },4000,3000)
            }
        }
        onDispose {
            timer.cancel()
        }
    }
    ShowUiWithNetworkResult(
        state.value,
        successUi = {
            Box(modifier = modifier){
                HorizontalPager(
                    pageCount = virtualCount,
                    state = pageState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                ) { index ->
                    val context = LocalContext.current
                    AsyncImage(
                        model = "${test_server}/carousel/${it.data[index%it.data.size].icon_name}/icon",
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                if(it.data[index%it.data.size].carousel.web!=""){
                                    val intent = Intent(context, NetworkPluginActivity::class.java)
                                    intent.putExtra("url",it.data[index%it.data.size].carousel.web)
                                    content.startActivity(intent)
                                }
                            },
                        contentScale = ContentScale.FillBounds
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (0 until it.data.size).forEach { radioIndex ->
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .size(10.dp)
                                .clip(RoundedCornerShape(100))
                                .background(
                                    if (radioIndex == pageState.currentPage % it.data.size) Color.Blue else Color.White
                                )
                        )
                    }
                }
            }
        },
        errorUi = {
            Text(text = "加载失败")
        },
        unloadUi = { Text(text = "未加载") }, loadingUi = { Text("加载中") },

    )

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PluginAlreadyDownloaded(
    pluginLongClick:(Plugin)->Unit = {},
    refreshCarousel:()->Unit = {},
    carouselState: State<NetworkResult<CarouselPicture>>
){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
        ){
            val content = LocalContext.current
            val list = FuuApplication.plugins.collectAsState()
            Carousel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                refreshCarousel = refreshCarousel,
                state = carouselState,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ){
                items(list.value.size){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp)
                            .background(
                                if (list.value[it].state == PluginState.SUCCESS) {
                                    Color.Transparent
                                } else {
                                    Color.Red
                                }
                            )
                            .combinedClickable(
                                onClick = {
                                    if (list.value[it].state == PluginState.SUCCESS) {
                                        val intent =
                                            Intent(content, ComposePluginActivity::class.java)
                                        intent.putExtra("index", it.toString())
                                        content.startActivity(intent)
                                    } else {
                                        normalToast("插件加载失败")
                                    }
                                },
                                onLongClick = {
                                    pluginLongClick.invoke(list.value[it])
                                }
                            )
                    ){
                        if(list.value[it].iconPath != null){
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest
                                        .Builder(LocalContext.current)
                                        .data(data = Uri.fromFile(File(list.value[it].iconPath!!)))
                                        .build()
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(10.dp),
                                contentScale = ContentScale.FillBounds
                            )
                        }else{
                            Image(
                                imageVector = Icons.Filled.Close ,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(10.dp),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        Text(
                            text = list.value[it].pluginConfig.apkName?:"加载失败",
                            softWrap = false,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        val clickable = FuuApplication.isLoading.collectAsState()
        FloatingActionButton(
            onClick = { if (!clickable.value) FuuApplication.reloadPlugins() else (Unit) },
            modifier = Modifier
                .offset(x = (-15).dp, y = (-15).dp)
                .align(Alignment.BottomEnd)
        ) {
            Crossfade(targetState = clickable.value, label = "") {
                if(it){
                    Icon(imageVector = Icons.Filled.Build, contentDescription = null)
                }
                else{
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
            }
        }
    }

}

@Composable
fun PluginDialog(
    plugin:Plugin,
    onDismissRequest : () -> Unit
){
    Dialog(onDismissRequest = onDismissRequest) {
        Column (
            Modifier
                .fillMaxHeight(0.85f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(214, 214, 236))
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
        ){
            Row(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Image(
                    painter = if(plugin.iconPath == null) painterResource(id = R.drawable.img) else rememberAsyncImagePainter(
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(data = Uri.fromFile(File(plugin.iconPath!!)))
                            .build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(100)),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = plugin.pluginConfig.apkName ?: "加载失败",
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
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.7f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(100))
                        .background(Color.Gray)
                        .clickable { onDismissRequest.invoke() }
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
                    text =  if(plugin.pluginConfig.developer!=null) "版本:${plugin.pluginConfig.developer}" else "未知开发者",
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
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.blanch),
                    contentDescription = null,
                    modifier = Modifier
                        .height(30.dp)
                        .aspectRatio(1f)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(100)),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text =  if(plugin.pluginConfig.version!=null) "开发者:${plugin.pluginConfig.version}" else "未知版本",
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
            plugin.markdown?.let {
                PluginMarkdown(it)
            }
        }
    }
}

@Composable
fun PluginMarkdown(
    markdownContent : String = """  
	# <u>Sample<u>
	## Markdown  
	* [Link](https://example.com)
  
	    ![Image](https://picx.zhimg.com/80/v2-34a2b85cc01eb4991cb4154b8b5a3dac_720w.webp?source=1940ef5c)  
	
    <a href="https://www.google.com/">Google</a>  
    # Sample  
	## Markdown  
	* [Link](https://example.com)
  
	    ![Image](https://picx.zhimg.com/80/v2-34a2b85cc01eb4991cb4154b8b5a3dac_720w.webp?source=1940ef5c)  
	
    <a href="https://www.google.com/">Google</a>  
    # Sample  
    ## Markdown  
    * [Link](https://example.com)
    
        ![Image](https://picx.zhimg.com/80/v2-34a2b85cc01eb4991cb4154b8b5a3dac_720w.webp?source=1940ef5c)  
    
    <a href="https://www.google.com/">Google</a>  
""".trimIndent()
){
    Box(modifier = Modifier
        .fillMaxSize()
    ){
        MarkdownText(
            modifier = Modifier
                .fillMaxSize(),
            markdown = markdownContent
        )
    }

}

