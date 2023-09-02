package com.example.fuuplugins.activity.mainActivity.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fuuplugins.R
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

@Composable
fun PluginPage(){
    NavHost(navController = rememberNavController(),"tool"){
        composable("store"){
            BackHandler {

            }
        }
        composable("tool"){
            BackHandler {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PluginTool(){
    Box(modifier = Modifier
        .statusBarsPadding()
    ){
        PluginAlreadyDownloaded()
        val show = remember {
            mutableStateOf(true)
        }
        PluginDialog(show) {
            show.value = false
        }
    }
}

@Preview(device = "spec:width=400dp,height=200dp")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun Carousel(
    modifier : Modifier = Modifier
) {
    val virtualCount = Int.MAX_VALUE
    val actualCount = 10
    //初始图片下标
    val initialIndex = virtualCount / 2
    val pageState = rememberPagerState(initialPage = initialIndex)
    //改变地方在这里
    val coroutineScope= rememberCoroutineScope()
    DisposableEffect(pageState.currentPage) {
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                coroutineScope.launch {
                    pageState.animateScrollToPage(pageState.currentPage+1)
                }
            }
        },4000,3000)
        onDispose {
            timer.cancel()
        }
    }
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
            Box(modifier = Modifier
                .fillMaxSize()
                .background(randomColor()))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            (0..9).forEach { radioIndex ->
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(10.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            if (radioIndex == pageState.currentPage % 10) Color.Blue else Color.White
                        )
                )
            }
        }
    }
}


@Preview
@Composable
fun PluginAlreadyDownloaded(){

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
        ){
            Carousel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .weight(1f)

            ){
                items(100){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(10.dp)
                                .background(randomColor())
                        )
                        Text(
                            text = "这是一个小插件",
                            softWrap = false,
                            maxLines = 1,
                            modifier = Modifier
                                .padding( top = 5.dp )
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .offset(x = (-15).dp, y = (-15).dp)
                .align(Alignment.BottomEnd)
        ) {

        }
    }

}

@Composable
@Preview
fun PluginDialog(
    show : State<Boolean> = remember {
        mutableStateOf(true)
    } ,
    onDismissRequest : () -> Unit = {}
){
    if(show.value){
        Dialog(onDismissRequest = onDismissRequest) {
            Column (
                Modifier
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(randomColor())
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
                PluginMarkdown()
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