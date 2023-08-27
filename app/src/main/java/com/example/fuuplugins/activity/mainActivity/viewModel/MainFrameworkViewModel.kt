package com.example.fuuplugins.activity.mainActivity.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.course.computeTheXueNian
import com.example.fuuplugins.activity.mainActivity.data.massage.MassageBean
import com.example.fuuplugins.activity.mainActivity.ui.UserDataInPersonPage
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.userDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainFrameworkViewModel: ViewModel() {
    val personDataFlow = FuuApplication.instance.userDataStore.data.map { it ->
        val userName = it[UserPreferencesKey.USER_NAME] ?: ""
        val userMood = it[UserPreferencesKey.USER_MOOD] ?: "😀"
        val userNumber = it[UserPreferencesKey.USER_NUMBER] ?: ""
        val userAcademy = it[UserPreferencesKey.USER_ACADEMY] ?: ""
        UserDataInPersonPage(
            name = userName,
            mood = userMood,
            number = userNumber,
            academy = userAcademy
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UserDataInPersonPage()
    )

    val massageDataFlowFromCourse = FuuApplication.db.courseDao().getAll().map {
        it.filter {
            course -> course.kcNote != ""
        }.map { course ->
            //06周 星期3:3-4节 调至 15周 星期3:3-4节 旗山文2-204
            MassageBean(
                title = course.kcName,
                content = course.kcNote,
                time = "",
                origin = "福州大学教务处"
            )
        }
    }
    val massageDataFlowFromMassageDao = FuuApplication.db.massageDao().getAll()
}