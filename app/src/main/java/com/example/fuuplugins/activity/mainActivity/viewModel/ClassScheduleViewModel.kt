package com.example.fuuplugins.activity.mainActivity.viewModel


import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.bean.CourseBean
import com.example.fuuplugins.activity.mainActivity.data.bean.YearOptionsBean
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository
import com.example.fuuplugins.activity.mainActivity.repositories.CourseRepository
import com.example.fuuplugins.activity.mainActivity.repositories.ExamRepository
import com.example.fuuplugins.activity.mainActivity.repositories.LoginResult
import com.example.fuuplugins.activity.mainActivity.repositories.OtherRepository
import com.example.fuuplugins.activity.mainActivity.repositories.OtherRepository.parseBeginDate
import com.example.fuuplugins.activity.mainActivity.repositories.WeekData
import com.example.fuuplugins.activity.mainActivity.ui.WhetherVerificationCode
import com.example.fuuplugins.config.dataStore.DataManagePreferencesKey
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.getDataManageDataStore
import com.example.fuuplugins.config.dataStore.setDataManageDataStore
import com.example.fuuplugins.config.dataStore.setUserDataStore
import com.example.fuuplugins.config.dataStore.setYearWeek
import com.example.fuuplugins.config.dataStore.userDataStore
import com.example.fuuplugins.util.catchWithMassage
import com.example.fuuplugins.util.easyToast
import com.example.fuuplugins.util.flowIO
import com.example.material.ButtonState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit


@OptIn(ExperimentalFoundationApi::class)
class ClassScheduleViewModel:ViewModel() {
    private var course = FuuApplication.db.courseDao().getAll()
    var currentYear = MutableStateFlow<String?>(null)
    var currentWeek = MutableStateFlow<Int>(1)
    val yearOptions = FuuApplication.db.yearOptionsDao().getAll()

    val scrollState = MutableStateFlow<ScrollState>(ScrollState(initial = 0))
    val pageState = MutableStateFlow(PagerState())
    val academicYearSelectsDialogState = MutableStateFlow(false)
    val courseDialog = MutableStateFlow<CourseBean?>(null)

    val refreshDialog = MutableStateFlow(false)
    var refreshDialogVerificationCode =  MutableStateFlow<ImageBitmap?>(null)
    val refreshVerificationCodeState  =  MutableStateFlow(WhetherVerificationCode.LOADING)
    val refreshClickAble = MutableStateFlow(true)
    val refreshButtonState = MutableStateFlow(ButtonState.Normal)

    val courseForShow = currentYear
        .combine(course){
            currentYear,course -> course.filter {
                "${it.kcYear}0${it.kcXuenian}" == currentYear
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            listOf()
        )

    fun refreshInitData(){
        viewModelScope.launch(Dispatchers.IO) {
            CourseRepository.getWeek()
                .collectLatest {
                    setYearWeek(
                        year = it.curYear.toString(), week = it.nowWeek.toString(), xuenian = it.curXuenian.toString()
                    )
                    currentYear.value = it.getXueQi()
                    currentWeek.value = it.nowWeek.toString().toInt()
                    OtherRepository.getSchoolCalendar(it.getXueQi())
                        .retry(10)
                        .map { result->
                            parseBeginDate(it.getXueQi(),result)
                        }.catchWithMassage{

                        }
                        .collectLatest {

                        }
                }
            if(checkCookieEffectiveness()){
                getCourseFromNetwork()
                getExamData()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCourseFromNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            with(CourseRepository){
                getExamData()
                getCourseStateHTML()
                .zip(
                    getWeek()
                ){ stateHTML, weekDataOnFlow ->
                    CourseData(stateHTML = stateHTML, weekData = weekDataOnFlow)
                }
                .collectLatest { courseData ->
                    val weekData = courseData.weekData
                    setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_WEEK,weekData.nowWeek.toString())
                    getCourses("${weekData.curYear}0${weekData.curXuenian}",courseData.stateHTML)
                        .flatMapConcat {
                            getCoursesHTML(
                                it,
                                "${weekData.curYear}0${weekData.curXuenian}",
                                onGetOptions = { yearOptionsFromNetwork ->
//                                    yearOptions.value = yearOptionsFromNetwork
                                    FuuApplication.db.yearOptionsDao().clearAll()
                                    FuuApplication.db.yearOptionsDao().insertYearOptions(
                                        yearOptionsFromNetwork.map {
                                            YearOptionsBean(yearOptionsName = it)
                                        }
                                    )
                                }
                            )
                        }
                        .catchWithMassage {

                        }
                        .collectLatest { initCourseBean ->
                            FuuApplication.db.courseDao().clearAll()
                            FuuApplication.db.courseDao().insertCourses(initCourseBean)
                            courseData.weekData.let{
                                setYearWeek(
                                    year = it.curYear.toString(), week = it.nowWeek.toString(), xuenian = it.curXuenian.toString()
                                )
                            }
                            getOtherCourseFromNetwork()
                        }
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getOtherCourseFromNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            with(CourseRepository){
                yearOptions.first().let { list ->
                    list.filter {
                        it.yearOptionsName != getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_ACADEMIC_YEAR,"").first()
                    }.map{
                        it.yearOptionsName
                    }.forEach { xq->
                        getCourseStateHTML()
                            .flatMapConcat { stateHtml ->
                                getCourses(xq,stateHtml)
                            }
                            .catchWithMassage {

                            }
                            .flatMapConcat {
                                getCoursesHTML(
                                    it,
                                    xq,
                                    onGetOptions = {
//                                        yearOptionsFromNetwork ->
//                                        yearOptions.value = yearOptionsFromNetwork
                                    }
                                )
                            }
                            .collectLatest { initCourseBean ->
                                FuuApplication.db.courseDao().clearByXq(xq.substring(0,4),xq.substring(5,6))
                                setUserDataStore(UserPreferencesKey.USER_DATA_VALIDITY_PERIOD,
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                FuuApplication.db.courseDao().insertCourses(initCourseBean)
                            }
                    }
                }

            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getExamData(){
        viewModelScope.launch(Dispatchers.IO) {
            val xq = getDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_ACADEMIC_YEAR,"").first()
            ExamRepository.apply {
                getExamStateHTML()
                    .catchWithMassage {

                    }
                    .map {
                        Log.d("html",it)
                        return@map(parseExamsHTML(it))
                    }
                    .collectLatest {
                        Log.d("exam",it.toString())
                        FuuApplication.db.examDao().clearAll()
                        FuuApplication.db.examDao().insertList(it)
                    }
            }
        }
    }

    fun refreshCourse(){
        viewModelScope.launch(Dispatchers.IO) {
            if(checkCookieEffectiveness()){
                getCourseFromNetwork()
            }
            else{
                refreshDialog.value = true
                BlockLoginPageRepository.getVerifyCode().catch {
                    refreshVerificationCodeState.value = WhetherVerificationCode.FAIL
                }.collectLatest {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    refreshDialogVerificationCode.value = bitmap.asImageBitmap()
                    refreshVerificationCodeState.value = WhetherVerificationCode.SUCCESS
                }
            }
        }
    }

    fun refreshWithVerificationCode(verification:String){
        viewModelScope.launch(Dispatchers.IO) {
            refreshButtonState.value = ButtonState.Loading
            val passwordState = FuuApplication.instance.userDataStore.data.map {
                it[UserPreferencesKey.USER_PASSWORD] ?: ""
            }.first()
            val usernameState = FuuApplication.instance.userDataStore.data.map {
                it[UserPreferencesKey.USER_USERNAME] ?: ""
            }.first()
            BlockLoginPageRepository.loginStudent(
                pass = passwordState,
                user = usernameState,
                captcha = verification,
                everyErrorAction = {
                    easyToast(it.throwable.message.toString())
                    refreshCourse()
                    refreshButtonState.value = ButtonState.Normal
                }
            )
                .flatMapConcat {
                    BlockLoginPageRepository.loginByTokenForIdInUrl(
                        result = it,
                        failedToGetAccount = {
                            easyToast(it.message.toString())
                            refreshButtonState.value = ButtonState.Normal
                        },
                        elseMistake = { error ->
                            easyToast(error.message.toString())
                            refreshButtonState.value = ButtonState.Normal
                        }
                    ).retryWhen{ error, tryTime ->
                        error.message == "获取account失败" && tryTime <= 3
                    }
                        .catchWithMassage {
                            if(it.message == "获取account失败"){
                                refreshButtonState.value = ButtonState.Normal
                                easyToast(it.message.toString())
                            }
                            else{
                                refreshButtonState.value = ButtonState.Normal
                                easyToast(it.message.toString())
                            }
                        }.flowIO()
                }
                .flatMapConcat {
                    BlockLoginPageRepository.loadCookieData(
                        queryMap = it,
                        user = usernameState
                    )
                }
                .flatMapConcat {
                    BlockLoginPageRepository.checkTheUserInformation(
                        user = usernameState,
                        serialNumberHandling = {

                        }
                    ).catchWithMassage {
                        refreshButtonState.value = ButtonState.Normal
                    }
                }
                .collect{ loginResult ->
                    when(loginResult){
                        LoginResult.LoginError->{
                            refreshButtonState.value = ButtonState.Normal
                            easyToast("刷新失败")
                        }
                        LoginResult.LoginSuccess->{
                            setUserDataStore(UserPreferencesKey.USER_DATA_VALIDITY_PERIOD,
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            easyToast("刷新成功")
                            refreshButtonState.value = ButtonState.Normal
                            getCourseFromNetwork()
                        }
                    }
                }
            }
        viewModelScope.launch(Dispatchers.IO) {
            getExamData()
        }
    }

    fun changeCurrentYear(newValue:String){
        viewModelScope.launch {
            currentYear.emit(newValue)
            OtherRepository.getSchoolCalendar(newValue)
                .retry(10)
                .map { result->
                    parseBeginDate(newValue,result)
                }
                .catchWithMassage {  }
                .collectLatest {

                }
        }
    }
    override fun onCleared() {
        super.onCleared()
        println("over_v")
    }

    data class CourseData(
        val stateHTML:String,
        val weekData: WeekData
    )

    suspend fun checkCookieEffectiveness():Boolean{
        val currentTime = (FuuApplication.instance.userDataStore.data.map {
            it[UserPreferencesKey.USER_DATA_VALIDITY_PERIOD]
        }.first() ?: "")
        if(currentTime == ""){
            return false
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 指定日期时间字符串的格式
        try {
            val parsedDateTime = LocalDateTime.parse(currentTime, formatter)
            println("解析后的日期时间: $parsedDateTime")
            return parsedDateTime.plusMinutes(15).isAfter(LocalDateTime.now())
        } catch (e: Exception) {
            println("日期时间字符串无效: $e")
            return false
        }
    }
}

fun test(){
    // 获取当前时间
    val currentDateTime = LocalDateTime.now()

    // 创建一个日期时间格式化器
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // 将当前时间转换为字符串
    val currentDateTimeStr = currentDateTime.format(formatter)

    println("当前时间: $currentDateTimeStr")

    // 在需要的时候，您可以再次获取当前时间并进行比较
    val anotherDateTime = LocalDateTime.now()
    val anotherDateTimeStr = anotherDateTime.format(formatter)

    println("另一个时间: $anotherDateTimeStr")

    // 示例比较
    if (currentDateTime.isBefore(anotherDateTime)) {
        println("当前时间早于另一个时间")
    } else if (currentDateTime.isAfter(anotherDateTime)) {
        println("当前时间晚于另一个时间")
    } else {
        println("当前时间和另一个时间相同")
    }
}