package com.example.fuuplugins.activity.mainActivity.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import com.example.fuuplugins.activity.mainActivity.data.bean.CourseBean
import com.example.fuuplugins.config.LightColors
import kotlin.random.Random
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect

import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.datastore.dataStore
import com.example.fuuplugins.activity.mainActivity.data.bean.YearOptionsBean
import com.example.fuuplugins.config.dataStore.DataManagePreferencesKey
import com.example.fuuplugins.config.dataStore.getDataManageDataStore
import com.example.material.ButtonState
import com.example.material.LoadableButton
import com.example.material.ScrollSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import java.util.Calendar


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassSchedule(
    viewModel: ClassScheduleViewModel = viewModel(),
    openDrawer : ()->Unit = {}
){
//    val sidebarSlideState = viewModel.scrollState
    val courseDialog by viewModel.courseDialog.collectAsStateWithLifecycle()
    val academicYearSelectsDialogState by viewModel.academicYearSelectsDialogState.collectAsStateWithLifecycle()
    val refreshDialogState by viewModel.refreshDialog.collectAsStateWithLifecycle()
    val refreshDialogVerificationCode = viewModel.refreshDialogVerificationCode.collectAsStateWithLifecycle()
    val yearOptionsBean by viewModel.yearOptions.collectAsStateWithLifecycle(listOf())
    val currentWeek by viewModel.currentWeek.collectAsStateWithLifecycle()
    LaunchedEffect(currentWeek){
        viewModel.pageState.value.animateScrollToPage(viewModel.currentWeek.value - 1)
    }
    LaunchedEffect(Unit){
        viewModel.refreshInitData()
    }

    Column {
        TopAppBar(
            navigationIcon = {

            },
            title = {
                Text(text = "第${viewModel.pageState.collectAsStateWithLifecycle().value.currentPage + 1}周")
            },
            actions = {
                IconButton(onClick = {
                    openDrawer.invoke()
                }) {
                    Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null)
                }
                IconButton(onClick = {
                    viewModel.refreshCourse()
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
        TimeOfWeekColumn(viewModel.pageState.collectAsStateWithLifecycle().value.currentPage)
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
                    viewModel.scrollState
                )
            }
            HorizontalPager(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                state = viewModel.pageState.collectAsStateWithLifecycle().value,
                pageCount = 30
            ){ page->
                Column {
                    TimeOfMonthColumn(page+1)
                    Row(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(viewModel.scrollState)
                    ) {
                        WeekDay.values().forEachIndexed { weekIndex, value ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .requiredHeight((11 * 75).dp)
                            ) {
                                viewModel.courseForShow.collectAsStateWithLifecycle().value.let { courseBeans ->
                                    courseBeans
                                        .filter {
                                            it.kcStartWeek <= page+1 && it.kcEndWeek >= page+1 && (it.kcIsDouble == ((page+1)%2 == 0) || it.kcIsSingle == ((page+1)%2 == 1))
                                        }
                                        .filter { courseBeanData ->
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
                                                if(index == 0){
                                                    ClassCard(
                                                        item,
                                                        detailAboutCourse = {
                                                            viewModel.courseDialog.value = it
                                                        }
                                                    )
                                                } else if(it[index].kcStartTime > it[index - 1].kcEndTime){
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
                viewModel.changeCurrentYear(it)
            },
            list = yearOptionsBean
        )
    }
    if(refreshDialogState){
        val text = remember {
            mutableStateOf("")
        }
        ToRefreshDialog(
            verificationCode = refreshDialogVerificationCode,
            onDismissRequest = {
                viewModel.refreshDialog.value = false
            },
            verificationCodeState = viewModel.refreshVerificationCodeState.collectAsStateWithLifecycle(),
            verificationCodeOnValueChange = { text.value = it },
            verificationCodeText = text,
            retryGetVerificationCode = { viewModel.refreshCourse() },
            refresh = { viewModel.refreshWithVerificationCode(text.value) },
            buttonState = viewModel.refreshButtonState.collectAsStateWithLifecycle(),
            clickAble = viewModel.refreshClickAble.collectAsStateWithLifecycle(),
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
            .padding(vertical = 3.dp, horizontal = 3.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = courseBean.kcName,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            fontSize = 10.sp,
            lineHeight = 11.sp
        )
        Text(
            text = courseBean.kcLocation,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 3.dp)
                .wrapContentHeight(),
            fontSize = 10.sp,
            lineHeight = 11.sp
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
            .height((75 * if (weight > 0) weight else 0).dp)
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
    backgroundColor: Color = Color(217, 217, 239),
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
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
                    .height(50.dp),
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    backgroundColor,
                    backgroundColor,
                    backgroundColor,
                    backgroundColor,
                    Color.Black,
                )
            )
            Text(
                text = courseBean.kcName,
                color = Color.Blue,
                style = TextStyle(fontSize = 27.sp),
                modifier = Modifier
                    .background(backgroundColor)
                    .padding(horizontal = 10.dp),
                textAlign = TextAlign.Center
            )
            LazyColumn(
                modifier = Modifier
                    .background(backgroundColor)
                    .padding(vertical = 20.dp)
                    .weight(1f)
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
                    .padding(bottom = 20.dp)
                    .background(backgroundColor)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 40.dp, vertical = 10.dp)
            ){
                FloatingActionButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    contentColor = backgroundColor,
                    containerColor = backgroundColor
                ) {

                }
                Spacer(modifier = Modifier.width(20.dp))
                FloatingActionButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    contentColor = backgroundColor,
                    containerColor = backgroundColor
                ) {

                }
            }
        }
    }
}



@Composable
fun TimeOfWeekColumn(week:Int){
    val startMonth = getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_MONTH,1).collectAsStateWithLifecycle(
        initialValue = 0
    ).value
    val startYear = getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_YEAR,1).collectAsStateWithLifecycle(
        initialValue = 0
    ).value
    val startDay = getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_DAY,1).collectAsStateWithLifecycle(
        initialValue = 0
    ).value
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
            text = "${getMonthByWeek(week,startYear,startMonth,startDay)}",
            fontSize = 12.sp,
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
fun TimeOfMonthColumn(week:Int){
    val startMonth = getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_MONTH,1).collectAsStateWithLifecycle(
        initialValue = 0
    ).value
    val startYear = getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_YEAR,1).collectAsStateWithLifecycle(
        initialValue = 0
    ).value
    val startDay = getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_DAY,1).collectAsStateWithLifecycle(
        initialValue = 0
    ).value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WeekDay.values().forEachIndexed{ index,_ ->
            Text(
                text = getDataByWeek(week,index, startYear,startMonth,startDay).toString(),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
        }
    }
}


fun getDataByWeek(week: Int,day:Int,startYear:Int, startMonth:Int,startDay:Int,):Int{
    //创建一个自定义年月日的日期，使用Calendar.set
    val calendar = Calendar.getInstance()
    calendar.set(startYear,startMonth-1,startDay)
    calendar.add(Calendar.WEEK_OF_YEAR,week-1)
    calendar.add(Calendar.DAY_OF_MONTH,day)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)+1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return day
}

fun getMonthByWeek(week: Int,startYear:Int, startMonth:Int,startDay:Int,):Int{
    //创建一个自定义年月日的日期，使用Calendar.set
    val calendar = Calendar.getInstance()
    calendar.set(startYear,startMonth-1,startDay)
    calendar.add(Calendar.WEEK_OF_YEAR,week)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return month+1
}

@Composable
@Preview
fun TimeOfWeekColumnPreview(){
    TimeOfWeekColumn(1)
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
fun AcademicYearSelectsDialog(
    onDismissRequest: ()->Unit = {},
    list: List<YearOptionsBean> = listOf("1","2","3")
        .map { year ->
            YearOptionsBean(
                yearOptionsName = year,
            )
        },
    commit: (String) -> Unit
){
    var data = MutableStateFlow("null")

    LaunchedEffect(Unit){
        data.value = if(list.isNotEmpty()) list[0].yearOptionsName else "null"
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
                textList = list.map {
                     year -> year.yearOptionsName
                },
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(217, 217, 238))
                    )
                },
                onItemSelected = { _ ,item->
                    Log.d("data commit",data.value)
                    data.value = item
                },
                state = state
            )
            ElevatedButton(
                onClick = {
                    Log.d("data value",data.value)
                    commit.invoke(data.value)
                    onDismissRequest.invoke()
                },
                enabled = data.collectAsStateWithLifecycle().value != "null",
                modifier = Modifier
                    .padding(top = 20.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 30.dp)
            ) {
                Text(text = "确定")
            }
        }
    }
}


@Composable
@Preview
fun ToRefreshDialog(
    onDismissRequest : ()->Unit = {},
    verificationCodeState:State<WhetherVerificationCode> = remember {
        mutableStateOf(WhetherVerificationCode.FAIL)
    },
    verificationCodeOnValueChange:(String) ->Unit = {

    },
    verificationCodeText:State<String> =  remember {
        mutableStateOf("")
    },
    verificationCode: State<ImageBitmap?> = remember {
        mutableStateOf(null)
    },
    retryGetVerificationCode: ()->Unit={},
    refresh: ()->Unit={},
    buttonState: State<ButtonState> = remember {
        mutableStateOf(ButtonState.Normal)
    },
    clickAble : State<Boolean> = remember {
        mutableStateOf(false)
    }
){
    Dialog(onDismissRequest = onDismissRequest ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(216, 216, 237))
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CaptchaLine(
                verificationCodeState = verificationCodeState,
                verificationCodeOnValueChange = verificationCodeOnValueChange,
                verificationCodeText = verificationCodeText,
                verificationCode = verificationCode,
                retryGetVerificationCode = retryGetVerificationCode
            )
            LoadableButton(
                modifier = Modifier
                    .padding(top = 20.dp),
                onClick = refresh,
                buttonState = buttonState.value,
                normalContent = {
                    Text(text = "REFRESH")
                },
                enabled = buttonState.value == ButtonState.Normal && verificationCodeText.value!="",
                loadingContent = {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            )
        }
    }
}
