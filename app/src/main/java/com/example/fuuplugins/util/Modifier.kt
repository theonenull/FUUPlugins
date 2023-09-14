package com.example.fuuplugins.util

import android.provider.ContactsContract.Data
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalTextApi::class)
@Stable
fun Modifier.drawLabel(
    label: String,
    textStyle : TextStyle = TextStyle(fontSize = 10.sp),
    padding:PaddingValues = PaddingValues(3.dp),
    density : Density,
    textMeasurer : TextMeasurer
): Modifier = this.drawBehind {

    val textContentHeight = textMeasurer.measure(AnnotatedString(""), textStyle).size.height
    val topPadding = padding.calculateTopPadding()
    val bottomPadding = padding.calculateBottomPadding()
    val contentHeight =  with(density) { (topPadding + bottomPadding).toPx() } + textContentHeight

    if(size.height<contentHeight){
        return@drawBehind
    }
}

@Composable
fun <T>ShowUiWithNetworkResult(
    data:NetworkResult<T>,
    unloadUi:@Composable ()->Unit = {},
    errorUi:@Composable (error:Throwable)->Unit= {},
    loadingUi:@Composable ()->Unit= {},
    successUi:@Composable (data:T)->Unit = {},
) {
    Crossfade(data, label = ""){
        when(it){
            is NetworkResult.UNLOAD -> {
                unloadUi()
            }
            is NetworkResult.ERROR<T> -> {
                errorUi(it.error)
            }
            is NetworkResult.LOADING->{
                loadingUi()
            }
            is NetworkResult.SUCCESS<T>->{
                successUi(it.data)
            }
        }

    }
}