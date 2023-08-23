package com.example.fuuplugins.activity.mainActivity.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.R
import com.example.fuuplugins.activity.mainActivity.data.massage.MassageBean
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.userDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PersonPage(
    userDataState: State<UserDataInPersonPage> = remember {
        mutableStateOf(UserDataInPersonPage())
    }
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(vertical = 20.dp, horizontal = 10.dp)
    ){
        ProfileArea(
            userDataState
        )
        StatusBarArea(
            userDataState.value.mood
        )
        MemorandumArea()
        ExamNotificationArea()
        RibbonArea()
    }
}

@Composable
@Preview
fun StatusBarArea(
    emoji : String = "😀"
){
    var isClick = remember {
        mutableStateOf(true)
    }
    val scope = rememberCoroutineScope()
    Column {
        Text(
            text = emoji,
            modifier = Modifier
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(100))
                .clickable {
                    isClick.value = !isClick.value
                },
            fontSize = 25.sp
        )
        AnimatedVisibility(visible = !isClick.value){
            LazyRow(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                emojiList.forEachIndexed { index ,item ->
                    item {
                        Text(
                            text = item,
                            modifier = Modifier
                                .padding(start = if (index == 0) 0.dp else 5.dp)
                                .clip(RoundedCornerShape(100))
                                .clickable(
                                    enabled = !isClick.value
                                ) {
                                    scope.launch {
                                        FuuApplication.instance.userDataStore.edit { preferences ->
                                            preferences[UserPreferencesKey.USER_MOOD] = item
                                        }
                                    }
                                },
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
    Divider(
        modifier = Modifier
            .padding(top = 10.dp),
        color = Color.LightGray
    )
}

val emojiList = listOf(
    "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE06",
    "\uD83D\uDE05", "\uD83D\uDE04", "\uD83E\uDD23", "\uD83E\uDD32", "\uD83E\uDD39",
    "\u263A\uFE0F", "\uD83D\uDE0A", "\uD83D\uDE07", "\uD83D\uDE42", "\uD83D\uDE09",
    "\uD83D\uDE0C", "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE1A", "\uD83D\uDE19",
    "\uD83D\uDE1B", "\uD83D\uDE1C", "\uD83D\uDE1D", "\uD83E\uDD2A", "\uD83E\uDD2B",
    "\uD83E\uDD2C", "\uD83E\uDD2E", "\uD83E\uDD2F", "\uD83E\uDD34", "\uD83E\uDD37",
    "\uD83E\uDD38", "\uD83E\uDD3C", "\uD83E\uDD3E", "\uD83E\uDD3F", "\uD83E\uDD4F",
    "\uD83E\uDD52", "\uD83E\uDD54", "\uD83E\uDD55", "\uD83E\uDD57", "\uD83E\uDD59",
    "\uD83E\uDD5B", "\uD83E\uDD5D", "\uD83E\uDD5F", "\uD83E\uDD60", "\uD83E\uDD61",
    "\uD83E\uDD64", "\uD83E\uDD65", "\uD83E\uDD66", "\uD83E\uDD67", "\uD83E\uDD68",
    "\uD83E\uDD69", "\uD83E\uDD6A", "\uD83E\uDD6C", "\uD83E\uDD6D", "\uD83E\uDD6E",
    "\uD83E\uDD6F", "\uD83E\uDD70", "\uD83E\uDD71", "\uD83E\uDD72", "\uD83E\uDD73",
    "\uD83E\uDD74", "\uD83E\uDD75", "\uD83E\uDD76", "\uD83E\uDD77", "\uD83E\uDD7A",
    "\uD83E\uDD7B", "\uD83E\uDD7C", "\uD83E\uDD7E", "\uD83E\uDD80", "\uD83E\uDD81",
    "\uD83E\uDD82", "\uD83E\uDD83", "\uD83E\uDD84", "\uD83E\uDD85", "\uD83E\uDD86",
    "\uD83E\uDD87", "\uD83E\uDD88", "\uD83E\uDD89", "\uD83E\uDD8A", "\uD83E\uDD8B",
    "\uD83E\uDD8C", "\uD83E\uDD8D", "\uD83E\uDD8E", "\uD83E\uDD8F", "\uD83E\uDD90",
    "\uD83E\uDD91", "\uD83E\uDD92", "\uD83E\uDD93", "\uD83E\uDD94", "\uD83E\uDD95",
    "\uD83E\uDD96", "\uD83E\uDD97", "\uD83E\uDD98", "\uD83E\uDD99", "\uD83E\uDD9A",
    "\uD83E\uDD9B", "\uD83E\uDD9C", "\uD83E\uDD9D", "\uD83E\uDD9E", "\uD83E\uDD9F",
    "\uD83E\uDDA0", "\uD83E\uDDA1", "\uD83E\uDDA2", "\uD83E\uDDA3", "\uD83E\uDDA4",
    "\uD83E\uDDA5", "\uD83E\uDDA6", "\uD83E\uDDA7", "\uD83E\uDDA8", "\uD83E\uDDA9",
    "\uD83E\uDDAA", "\uD83E\uDDAB", "\uD83E\uDDAC", "\uD83E\uDDAD", "\uD83E\uDDAE",
    "\uD83E\uDDAF", "\uD83E\uDDF4", "\uD83E\uDDF6", "\uD83E\uDDF7", "\uD83E\uDDF8",
    "\uD83E\uDDF9", "\uD83E\uDDFA", "\uD83E\uDDFB", "\uD83E\uDDFC", "\uD83E\uDDFD",
    "\uD83E\uDDFE", "\uD83E\uDDFF", "\u2615\uFE0F", "\uD83C\uDF75", "\uD83C\uDF76",
    "\uD83C\uDF7E", "\uD83C\uDF77", "\uD83C\uDF78", "\uD83C\uDF79", "\uD83C\uDF7A",
    "\uD83C\uDF7B", "\uD83C\uDF7C", "\uD83C\uDF7D", "\uD83C\uDF7F", "\uD83C\uDF80",
    "\uD83C\uDF81", "\uD83C\uDF82", "\uD83C\uDF83", "\uD83C\uDF84", "\uD83C\uDF85",
    "\uD83C\uDF86", "\uD83C\uDF87", "\uD83C\uDF88", "\uD83C\uDF89", "\uD83C\uDF8A",
    "\uD83C\uDF8B", "\uD83C\uDF8C", "\uD83C\uDF8D", "\uD83C\uDF8E", "\uD83C\uDF8F",
    "\uD83C\uDF90", "\uD83C\uDF91", "\uD83C\uDF92"
)


@Composable
@Preview
fun ProfileArea(
    userDataState: State<UserDataInPersonPage> = remember {
        mutableStateOf(UserDataInPersonPage())
    }
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ){
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(100)),
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.Center,
        )

        Text(
            text = userDataState.value.name ,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .padding( top = 20.dp)
        )
        Text(
            text = userDataState.value.academy ,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .padding( top = 10.dp)
        )
        Text(
            text = userDataState.value.number ,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            modifier = Modifier
                .padding( top = 10.dp),
            textDecoration = TextDecoration.Underline
        )
//        Text(
//            text = "2023年第一学期" ,
//            fontWeight = FontWeight(300),
//            fontSize = 15.sp,
//            modifier = Modifier
//                .padding( top = 10.dp),
//            textDecoration = TextDecoration.None
//        )
        Divider(
            modifier = Modifier
                .padding(top = 10.dp),
            color = Color.LightGray
        )
    }
}

@Composable
@Preview
fun DataDisplayArea(){

}

@Composable
@Preview
fun MemorandumArea(){
    val massageDao = FuuApplication.db.massageDao()
    val scope = rememberCoroutineScope()
    val massages = massageDao.getAll().collectAsStateWithLifecycle(listOf())
    Column {
        massages.value.forEach {
            PoopRaft(it)
        }
        FloatingActionButton(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    massageDao.clearAll()
                    massageDao.insertAll(
                        MassageBean(
                            massageId = 0, title = "Damion", time = "2023 01 01", content = "Talena", origin = "Krystalyn"
                        )
                    )
                }
            },
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.End)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "")
        }
        Divider(
            modifier = Modifier
                .padding(top = 10.dp),
            color = Color.LightGray
        )
    }
}

@Composable
@Preview
fun PoopRaft(
    massageBean: MassageBean = MassageBean()
) {
    Box(modifier = Modifier
        .padding(top = 20.dp)
        .fillMaxWidth()
        .wrapContentHeight()
        .clip(AbsoluteCutCornerShape(bottomRight = 10.dp))
        .background(Color(242, 230, 255))
    ){
        Column(modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(10.dp)
        ){
            Text(
                text = massageBean.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(bottom = 10.dp, end = 10.dp)
            )
            Text(
                text = massageBean.content
            )
            Text(
                text = "来源 ${massageBean.origin}",
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.End)
            )
            Text(
                text = massageBean.time,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .align(Alignment.End)
            )
        }
        Box(
            modifier = Modifier
                .size(10.dp)
                .align(Alignment.BottomEnd)
                .clip(AbsoluteCutCornerShape(bottomRight = 10.dp))
                .background(Color(229, 205, 216))
        )
    }
}


@Composable
@Preview
fun ExamNotificationArea(){

    Column(){
        repeat(10){
            Row (
                modifier = Modifier
                    .padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                ExamLabel(
                    when{
                        it%5 == 1 -> remember {
                            mutableStateOf(ExamLabelType.Elective)
                        }
                        it%4 == 1 -> remember {
                            mutableStateOf(ExamLabelType.EXPERIMENT)
                        }
                        else -> remember {
                            mutableStateOf(ExamLabelType.COMPULSORY)
                        }
                    }
                )
                Text(
                    text = "距离计算机网络考试还有",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1.5f),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "6",
                    color = Color.Red,
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(horizontal = 10.dp),
                    textAlign = TextAlign.Center
                )
                Text(text = "天")
            }
        }
        Divider(
            modifier = Modifier
                .padding(top = 10.dp),
            color = Color.LightGray
        )
    }
}


@Composable
fun ExamLabel(
    examLabelType: State<ExamLabelType> = remember {
        mutableStateOf(ExamLabelType.Elective)
    }
){
    when(examLabelType.value){
        ExamLabelType.EXPERIMENT->{
            Text(
                text = "实验",
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(124, 186, 228, 255))
                    .padding(vertical = 2.dp, horizontal = 5.dp)
                ,
                fontSize = 10.sp
            )
        }

        ExamLabelType.Elective->{
            Text(
                text = "必修",
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(81, 223, 87, 255))
                    .padding(vertical = 2.dp, horizontal = 5.dp)
                ,
                fontSize = 10.sp
            )
        }
        ExamLabelType.COMPULSORY->{
            Text(
                text = "选修",
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(214, 224, 113, 255))
                    .padding(vertical = 2.dp, horizontal = 5.dp)
                ,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
@Preview
fun ExamLabelPreview(){
    Column {
        ExamLabel(
            remember {
                mutableStateOf(ExamLabelType.Elective)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExamLabel(
            remember {
                mutableStateOf(ExamLabelType.COMPULSORY)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExamLabel(
            remember {
                mutableStateOf(ExamLabelType.EXPERIMENT)
            }
        )
    }
}

@Stable
enum class ExamLabelType{
    EXPERIMENT,
    COMPULSORY,
    Elective
}

@Composable
@Preview
fun RibbonArea(){
    Column {
        RibbonButton.values().forEach{ item ->
            ExtendedFloatingActionButton(
                onClick = { /* do something */ },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                contentColor = item.contentColor,
                containerColor = item.containerColor
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(0.5f)
                )
                Icon(
                    imageVector = item.icon ,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = item.itemName,
                    modifier = Modifier
                        .weight(1f)
                )
                Spacer(
                    modifier = Modifier
                        .weight(0.5f)
                )
            }
        }
    }
}

enum class RibbonButton(
    val icon : ImageVector,
    val itemName : String,
    val containerColor : Color = Color(242, 230, 255),
    val contentColor: Color = Color(33, 0, 93)
){
    Setting(Icons.Filled.Settings,"设置" ),
    HelpAndFeedback(Icons.Filled.Edit,"帮助与反馈"),
    AboutUs(Icons.Filled.Person,"关于我们"),
    SignOut(Icons.Filled.ExitToApp,"退出登录", containerColor = Color(248, 69, 69, 255), contentColor = Color.Black)
}

data class UserDataInPersonPage(
    val mood : String = "😀",
    val number : String = "",
    val name : String = "" ,
    val academy : String = ""
)