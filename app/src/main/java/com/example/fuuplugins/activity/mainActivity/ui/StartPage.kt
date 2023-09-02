package com.example.fuuplugins.activity.mainActivity.ui


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fuuplugins.activity.mainActivity.viewModel.StartPageViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random


@Composable
fun StartPage(
    startPageViewModel: StartPageViewModel = viewModel(),
    jumpOver:()->Unit,
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit){
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                scope.launch {
                    jumpOver.invoke()
                }
            }
        }
        timer.schedule(task, 5000, 5000)
        onDispose {
            task.cancel()
        }
    }
    LaunchedEffect(Unit){
        startPageViewModel.getStartPageData { jumpOver.invoke() }
    }
    when(startPageViewModel.currentPage.collectAsState().value){
        StartPageShowType.Loading->{
            Loading()
        }
        StartPageShowType.MarkDown->{
            ImagesOnly(
                startPageViewModel.imageUrl.collectAsState().value,
                jumpOver = jumpOver
            )
        }
        StartPageShowType.ImageOnly->{
            Log.d("==========",startPageViewModel.imageUrl.collectAsState().value)
            ImagesOnly(
                startPageViewModel.imageUrl.collectAsState().value,
                jumpOver = jumpOver
            )
        }
    }
}

@Composable
fun ImagesOnly(
    context: String,
    jumpOver:()->Unit = {}
){
    Box (
        modifier = Modifier
            .fillMaxSize()
    ){
        val color = remember {
            mutableStateOf(Color(216, 216, 237, 100))
        }
        AsyncImage(
            model = ImageRequest.Builder(
                LocalContext.current,
            )
                .allowHardware(true)
                .allowConversionToBitmap(true)
                .data(context)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )

        FloatingActionButton(
            onClick = { jumpOver.invoke() },
            modifier = Modifier
                .systemBarsPadding()
                .offset(x = (-10).dp, y = (-10).dp)
                .align(Alignment.BottomEnd),
            containerColor = color.value,
            contentColor = color.value
        ) {
            Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
@Preview
fun ImageOnlyPreview(){
    ImagesOnly("")
}

@Composable
fun Markdown(
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
""".trimIndent(),
    onclick: () -> Unit
){
    Box(modifier = Modifier
        .fillMaxSize()
    ){
        Column (
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ){
            MarkdownText(
                modifier = Modifier
                    .fillMaxSize(),
                markdown = markdownContent
            )
        }
        ExtendedFloatingActionButton(
            onClick = onclick,
            modifier = Modifier
                .offset(x = (-10).dp, y = (-20).dp)
                .align(Alignment.BottomEnd),
            icon = {
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
            },
            text = {
                Text(text = "跳过")
            }
        )
    }

}

@Composable
@Preview
fun MarkDownPreview(){
    Markdown(){

    }
}


@Composable
@Preview
fun Loading(){
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val remote by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            imageVector = Icons.Filled.Refresh,
            contentDescription = null,
            modifier = Modifier
                .rotate(remote)
                .size(50.dp, 50.dp)
                .align(Alignment.Center)
        )
        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .offset(x = (-10).dp, y = (-10).dp)
                .align(Alignment.BottomEnd),
        ) {
            Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }

}
enum class StartPageShowType{
    MarkDown,
    ImageOnly,
    Loading
}



private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    var width = drawable.intrinsicWidth
    width = if (width > 0) width else 1
    var height = drawable.intrinsicHeight
    height = if (height > 0) height else 1
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    drawable.draw(canvas)
    return bitmap
}

fun randomColor() = Color(Random.nextInt(0,255),Random.nextInt(0,255),Random.nextInt(0,255))