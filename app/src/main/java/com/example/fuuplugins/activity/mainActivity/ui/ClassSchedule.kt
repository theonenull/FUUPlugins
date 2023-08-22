package com.example.fuuplugins.activity.mainActivity.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fuuplugins.activity.mainActivity.viewModel.ClassScheduleViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fuuplugins.activity.mainActivity.data.course.CourseBean
import com.example.fuuplugins.config.LightColors
import com.example.fuuplugins.util.debug
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.example.material.ScrollSelection
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassSchedule(
    viewModel: ClassScheduleViewModel = viewModel(),
    openDrawer : ()->Unit = {}
){
    val sidebarSlideState by viewModel.scrollState.collectAsStateWithLifecycle()
    val courseDialog by viewModel.courseDialog.collectAsStateWithLifecycle()
    val academicYearSelectsDialogState by viewModel.academicYearSelectsDialogState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit){
        viewModel.getCourseFromNetwork(viewModel.pageState.currentPage+1)
    }
    LaunchedEffect(viewModel.pageState.currentPage){
        viewModel.changeWeek(viewModel.pageState.currentPage + 1)
    }

    Column {
        TopAppBar(
            navigationIcon = {

            },
            title = {
                Text(text = "第${viewModel.pageState.currentPage + 1}周")
            },
            actions = {
                IconButton(onClick = {
                    openDrawer.invoke()
                }) {
                    Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null)
                }
                IconButton(onClick = {
                    openDrawer.invoke()
                }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "学年") },
                            onClick = {
                                expanded = false
                                viewModel.academicYearSelectsDialogState.value = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                Text(viewModel.currentYear.collectAsStateWithLifecycle().value.toString(),
                                    textAlign = TextAlign.Center)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { /* Handle settings! */ },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Settings,
                                    contentDescription = null
                                )
                            })
                        DropdownMenuItem(
                            text = { Text("Send Feedback") },
                            onClick = { /* Handle send feedback! */ },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = { Text("F11", textAlign = TextAlign.Center) })
                    }
                }
            }
        )
        TimeOfWeekColumn()
        Row (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            Box(modifier = Modifier
                .padding(top = 20.dp)
                .wrapContentWidth()
                .fillMaxHeight()){
                Sidebar(
                    sidebarSlideState
                )
            }
            HorizontalPager(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                state = viewModel.pageState,
                pageCount = 10
            ){
                Column {
                    TimeOfMonthColumn()
                    Row(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(sidebarSlideState)
                    ) {
                        WeekDay.values().forEachIndexed { weekIndex, value ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .requiredHeight((11 * 75).dp)
                            ) {
                                viewModel.courseForShow.collectAsStateWithLifecycle().value?.let { courseBeans ->
                                    courseBeans.filter { courseBeanData ->
                                        courseBeanData.kcWeekend == weekIndex + 1
                                    }.sortedBy { courseBean ->
                                        courseBean.kcStartTime
                                    }.let {
                                        it.forEachIndexed { index, item ->
                                            if (index == 0) {
                                                EmptyClassCard(
                                                    item.kcStartTime - 1
                                                )
                                            } else {
                                                EmptyClassCard(
                                                    it[index].kcStartTime - it[index - 1].kcEndTime - 1
                                                )
                                            }
                                            ClassCard(
                                                item,
                                                detailAboutCourse = {
                                                    viewModel.courseDialog.value = it
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    courseDialog?.let {
        ClassDialog(
            courseBean = it,
            onDismissRequest = {
                viewModel.courseDialog.value = null
            }
        )
    }
    if(academicYearSelectsDialogState){
        AcademicYearSelectsDialog(
            onDismissRequest = {
                viewModel.academicYearSelectsDialogState.value = false
            },
            commit = {
                viewModel.currentYear.value = it
            }
        )
    }
}

@Composable
@Preview
fun ClassSchedulePreview(){
    ClassSchedule()
}


@Composable
fun ClassCard(
    courseBean: CourseBean,
    detailAboutCourse:(CourseBean)->Unit = {}
) {
    Column (
        modifier = Modifier
            .height(
                ((courseBean.kcEndTime - courseBean.kcStartTime + 1) * 75).dp
            )
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 2.dp, horizontal = 2.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(LightColors.values()[courseBean.kcBackgroundId].color)
            .clickable {
                detailAboutCourse.invoke(courseBean)
            }
            .padding(vertical = 10.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = courseBean.kcName,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp),
            fontSize = 12.sp,
        )
        Text(
            text = courseBean.kcLocation,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp),
            fontSize = 10.sp
        )
//        Text(
//            text = "203",
//            maxLines = 3,
//            overflow = TextOverflow.Ellipsis,
//            textAlign = TextAlign.Center
//        )
    }
}
@Composable
@Preview(device = "spec:width=200px,height=2340px,dpi=440")
fun EmptyClassCard(
    weight:Int = 0
){
    LaunchedEffect(Unit){

    }
    Column (
        modifier = Modifier
            .height((75 * weight).dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Transparent)
            .padding(vertical = 10.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

    }
}

//@Composable
//@Preview(device = "spec:width=200px,height=2340px,dpi=440")
//fun ClassCardPreview(){
//    ClassCard()
//}

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
                    .height(75.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = item.toString(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
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




//@Composable
//@Preview
//fun ClassDialogPreview(){
//    val showDialog = remember {
//        mutableStateOf(true)
//    }
//    ClassDialog(title = "", message = "", showClassDialog = showDialog) {
//        showDialog.value = false
//    }
//}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassDialog(
    courseBean: CourseBean,
    backgroundColor: Color = Color(0xFFCCCCCC),
    onDismissRequest: () -> Unit,
) {
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
                text = courseBean.kcName,
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
                                text = when(index){
                                    0->courseBean.kcLocation
                                    1->courseBean.teacher
                                    2->"${courseBean.kcStartTime}~${courseBean.kcEndTime}"
                                    3->"${courseBean.kcStartWeek}周~${courseBean.kcEndWeek}周"
                                    4->if(courseBean.kcNote == "") "无" else courseBean.kcNote
                                    else -> ""
                                }
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

//@Composable
//fun CourseGrid(
//    lazyGridState : LazyGridState = rememberLazyGridState()
//){
//    rememberLazyListState()
//    val index by remember {
//        derivedStateOf { lazyGridState.firstVisibleItemIndex }
//    }
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(7),
//        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
//        state = lazyGridState
//    ){
//        items(77) {
//            ClassCard(courseBeans[it-1])
//        }
//    }
//}
//
//@Composable
//@Preview
//fun CourseGridPreview(){
//    CourseGrid()
//}

@Composable

fun TimeOfWeekColumn(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .width(20.dp)
                .wrapContentHeight(),
            text = "8月",
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
        )
        WeekDay.values().forEach { item ->
            Text(
                text = item.chineseName,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

@Composable

fun TimeOfMonthColumn(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WeekDay.values().forEach { _ ->
            Text(
                text = Random.nextInt(1,30).toString(),
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

@Composable
@Preview
fun AcademicYearSelectsDialog(
    onDismissRequest : ()->Unit = {},
    list: List<String> = listOf("1","2","3"),
    commit : (String) -> Unit  = {}
){
    var data by remember {
        mutableStateOf(list[0])
    }
    val state = rememberLazyListState()

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(Color.LightGray)
                .padding(10.dp)
        ) {
            ScrollSelection(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textList = list,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(217, 217, 238))
                    )
                },
                onItemSelected = { _ ,item->
                    data = item
                    debug(item)
                },
                state = state
            )
            ElevatedButton(
                onClick = {
                    commit.invoke(data)
                    onDismissRequest.invoke()
                },
                modifier = Modifier
                    .padding(top = 20.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 30.dp)
            ) {
                Text(text = "确定")
            }
        }
    }
}