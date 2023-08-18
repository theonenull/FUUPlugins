package com.example.fuuplugins.activity.mainActivity.viewModel


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.activity.mainActivity.data.course.CourseBean
import com.example.fuuplugins.activity.mainActivity.repositories.ClassScheduleRepository
import com.example.fuuplugins.util.debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
class ClassScheduleViewModel:ViewModel() {

    private var course = MutableStateFlow<List<CourseBean>?>(null)
    val courseForShow = MutableStateFlow<List<CourseBean>?>(null)
    val pageState = MutableStateFlow<PagerState>(PagerState())
    val scrollState = MutableStateFlow<ScrollState>(ScrollState(initial = 0))

    val page = pageState
        .map {
            it.currentPage+1
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = pageState.value.currentPage
        )
    val courseDialog = MutableStateFlow<CourseBean?>(null)
    var courseDialogState = courseDialog
        .map {
            it != null
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCourseFromNetwork(initPage:Int){
        viewModelScope.launch(Dispatchers.IO) {
            ClassScheduleRepository.getCourseStateHTML()
                .flatMapConcat {
                    ClassScheduleRepository.getCourses("202301",it)
                }
                .flatMapConcat {
                    ClassScheduleRepository.getCoursesHTML(it,"202301")
                }
                .collectLatest {
                    it.forEach{
                        debug(it.toString())
                    }
                    course.value = it
                    changeWeek(initPage)
                }
        }
    }

    fun changeWeek(week:Int){
        viewModelScope.launch(Dispatchers.IO) {
            course.value?.let { courseBeans ->
                courseForShow.value = courseBeans.filter {
                    it.kcEndWeek >= week && it.kcStartWeek <= week
                }
            }
        }
    }

}