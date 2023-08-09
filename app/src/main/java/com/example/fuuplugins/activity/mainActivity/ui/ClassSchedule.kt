package com.example.fuuplugins.activity.mainActivity.ui

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClassSchedule(){
    val gridState = rememberLazyGridState()
    val sidebarSlideState = rememberScrollState()
    val index by remember {
        derivedStateOf { gridState.firstVisibleItemIndex }
    }
    LaunchedEffect(index){
        sidebarSlideState.animateScrollTo(index)
    }
    Column {
        TimeOfWeekColumn()
        Row (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            Box(modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()){
                Sidebar(
                    sidebarSlideState
                )
            }
            HorizontalPager(
                pageCount = 10,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    CourseGrid(

                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ClassSchedulePreview(){
    ClassSchedule()
}
@Composable
fun ClassCard(){
    Column (
        modifier = Modifier
            .height(75.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 2.dp, horizontal = 2.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Gray)
            .padding(vertical = 10.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "计算机操作系统计算机操作系统计算机操作系统",
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp)
        )
        Text(
            text = "东",
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp)
        )
        Text(
            text = "203",
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
@Preview(device = "spec:width=200px,height=2340px,dpi=440")
fun EmptyClassCard(){
    Column (
        modifier = Modifier
            .height(75.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Transparent)
            .padding(vertical = 10.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

    }
}

@Composable
@Preview(device = "spec:width=200px,height=2340px,dpi=440")
fun ClassCardPreview(){
    ClassCard()
}

@Composable
fun Sidebar(
    sidebarSlideState: ScrollState = rememberScrollState()
){
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(20.dp)
            .verticalScroll(
                state = sidebarSlideState
            ),
    ){
        (1..11).forEachIndexed { _, item ->
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = item.toString(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 1.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
@Preview
fun SidebarPreview(){
    Sidebar()
}




@Composable
@Preview
fun ClassDialogPreview(){
    val showDialog = remember {
        mutableStateOf(true)
    }
    ClassDialog(title = "", message = "", showClassDialog = showDialog) {
        showDialog.value = false
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassDialog(
    title: String,
    message: String,
    backgroundColor: Color = Color(0xFFCCCCCC),
    showClassDialog : State<Boolean>,
    onDismissRequest: () -> Unit,
) {
    if (showClassDialog.value){
        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(backgroundColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    title = { /*TODO*/ },
                    actions = {
                        IconButton(onClick = { onDismissRequest.invoke() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .padding(10.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                )
                Text(
                    text = "人工智能",
                    color = Color.Blue,
                    style = TextStyle(fontSize = 30.sp)
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(0.7f)
                ){
                    ClassScheduleNotificationDisplayProperties.forEachIndexed { index, item ->
                        item{
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = if (index != 0) 10.dp else 0.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(3f),
                                    textAlign = TextAlign.End,
                                    text = item
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(start = 20.dp)
                                        .weight(6f)
                                        .wrapContentHeight(),
                                    textAlign = TextAlign.Start,
                                    text = item
                                )
                            }
                        }
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 40.dp, vertical = 10.dp)
                ){
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {

                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {

                    }
                }
            }
        }
    }

}

@Composable
fun CourseGrid(
    lazyGridState : LazyGridState = rememberLazyGridState()
){
    rememberLazyListState()
    val index by remember {
        derivedStateOf { lazyGridState.firstVisibleItemIndex }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
        state = lazyGridState
    ){
        items(77) {
            ClassCard()
        }
    }
}

@Composable
@Preview
fun CourseGridPreview(){
    CourseGrid()
}

@Composable

fun TimeOfWeekColumn(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(20.dp))

        WeekDay.values().forEach { item ->
            Text(
                text = item.chineseName,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Preview
fun TimeOfWeekColumnPreview(){
    TimeOfWeekColumn()
}

val ClassScheduleNotificationDisplayProperties = listOf("教室","教师","节数","周数","备注",)

enum class WeekDay(val chineseName: String, val englishName: String) {
    MONDAY("星期一", "Monday"),
    TUESDAY("星期二", "Tuesday"),
    WEDNESDAY("星期三", "Wednesday"),
    THURSDAY("星期四", "Thursday"),
    FRIDAY("星期五", "Friday"),
    SATURDAY("星期六", "Saturday"),
    SUNDAY("星期日", "Sunday"),
}