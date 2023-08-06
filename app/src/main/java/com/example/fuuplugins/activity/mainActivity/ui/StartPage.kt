package com.example.fuuplugins.activity.mainActivity.ui


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import androidx.palette.graphics.Palette
import com.example.fuuplugins.R
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun StartPage(

) {

}

@Composable
fun ImagesOnly(

){
    Box (
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .offset(x = (-10).dp, y = (-10).dp)
                .align(Alignment.BottomEnd),
            containerColor = Color(Palette.from(BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.img)).generate().getMutedColor(Color(217, 217, 238).toArgb())),
            contentColor = Color(Palette.from(BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.img)).generate().getMutedColor(Color(217, 217, 238).toArgb()))
        ) {
            Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
@Preview
fun ImageOnlyPreview(){
    ImagesOnly()
}

@Composable
fun Markdown(){

}

val markdownContent = """  
	# Sample  
	* Markdown  
	* [Link](https://example.com)  
	![Image](https://example.com/img.png)  
	<a href="https://www.google.com/">Google</a>  
"""

//Minimal example
@Preview
@Composable
fun MinimalExampleContent() {
    MarkdownText(markdown = markdownContent)
}

//Complex example
@Preview
@Composable
fun ComplexExampleContent() {
    MarkdownText(
        modifier = Modifier.padding(8.dp),
        markdown = markdownContent,
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        color = LocalContentColor.current,
        maxLines = 3
    )
}
