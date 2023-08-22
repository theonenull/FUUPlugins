package com.example.fuuplugins.activity.mainActivity.viewModel


import android.os.Debug
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.data.course.CourseBean
import com.example.fuuplugins.activity.mainActivity.repositories.ClassScheduleRepository
import com.example.fuuplugins.activity.mainActivity.repositories.WeekData
import com.example.fuuplugins.util.debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
class ClassScheduleViewModel:ViewModel() {
    private var course = MutableStateFlow<Map<String,List<CourseBean>?>>(hashMapOf())
    var currentYear = MutableStateFlow<String?>(null)
    val scrollState = MutableStateFlow<ScrollState>(ScrollState(initial = 0))
    val pageState = MutableStateFlow(PagerState())
    val academicYearSelectsDialogState = MutableStateFlow(false)
    val courseDialog = MutableStateFlow<CourseBean?>(null)
    val yearOptions = MutableStateFlow<List<String>?>(null)
    val courseForShow = currentYear
        .map {
            debug("courseForShow $it")
            course.value[it]
        }
        .filter { it ->
            it?.forEach {
                debug("Test $it")
            }
            true
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    var courseDialogState = courseDialog
        .map {
            it != null
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )
    init {
        getCourseFromNetwork()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCourseFromNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            with(ClassScheduleRepository){
                getCourseStateHTML()
                .zip(
                    getWeek()
                ){ stateHTML, weekDataOnFlow ->
                    CourseData(stateHTML = stateHTML, weekData = weekDataOnFlow)
                }
                .collectLatest { courseData ->
                    val weekData = courseData.weekData
                    getCourses("${weekData.curYear}0${weekData.curXuenian}",courseData.stateHTML)
                        .flatMapConcat {
                            getCoursesHTML(
                                it,
                                "${weekData.curYear}0${weekData.curXuenian}",
                                onGetOptions = { yearOptionsFromNetwork ->
                                    yearOptions.value = yearOptionsFromNetwork
                                }
                            )
                        }
                        .collectLatest { initCourseBean ->
                            val data = course.value.toMutableMap()
                            course.emit(data.apply {
                                this[weekData.getXueQi()] = initCourseBean
                            }.toMap())
                            currentYear.emit(weekData.getXueQi())
                            debug("---------------")
                            getOtherCourseFromNetwork()
                        }
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getOtherCourseFromNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            with(ClassScheduleRepository){
                yearOptions.value?.let { list ->
                    list.filter {
                        it != currentYear.value
                    }.forEach { xq->
                        getCourseStateHTML()
                            .flatMapConcat { stateHtml ->
                                getCourses(xq,stateHtml)
                            }
                            .flatMapConcat {
                                getCoursesHTML(
                                    it,
                                    xq,
                                    onGetOptions = { yearOptionsFromNetwork ->
                                        yearOptions.value = yearOptionsFromNetwork
                                    }
                                )
                            }
                            .collectLatest { initCourseBean ->
                                val data = course.value.toMutableMap()
                                course.emit(data.apply {
                                    this[xq] = initCourseBean
                                }.toMap())
                            }
                    }
                }

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
}