package com.example.fuuplugins.activity.composePluginActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.bean.CourseBean
import com.example.fuuplugins.ui.theme.FUUPluginsTheme
import com.example.fuuplugins.util.easyToast
import com.example.fuuplugins.util.normalToast
import com.example.inject.bean.ExamBean
import com.example.inject.bean.MassageBean
import com.example.inject.bean.YearOptionsBean
import com.example.inject.repository.Repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ComposePluginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val index = intent.getStringExtra("index")?:""
        if(index == ""){
            normalToast("插件加载失败")
            this.onDestroy()
        }
        val plugin = FuuApplication.plugins.value.get(index = index.toInt())
        setContent {
            FUUPluginsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    plugin.composeMethod?.invoke(
                        plugin.pluginObject,
                        ActionForPlugin(),
                        currentComposer,
                        0
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

 class ActionForPlugin(): Repository {
    override fun getCourse(): Flow<List<com.example.inject.bean.CourseBean>?> {
        return if(true){
            FuuApplication.db.courseDao().getAll().map { it ->
                it.map {
                    com.example.inject.bean.CourseBean(
                        kcName = it.kcName,
                        kcLocation = it.kcLocation,
                        kcStartTime = it.kcStartTime,
                        kcEndTime = it.kcEndTime,
                        kcStartWeek = it.kcStartWeek,
                        kcEndWeek = it.kcEndWeek,
                        kcIsDouble = it.kcIsDouble,
                        kcIsSingle = it.kcIsSingle,
                        kcWeekend = it.kcWeekend,
                        kcYear = it.kcYear,
                        kcXuenian = it.kcXuenian,
                        kcNote = it.kcNote,
                        kcBackgroundId = it.kcBackgroundId,
                        shoukeJihua = it.shoukeJihua,
                        jiaoxueDagang = it.jiaoxueDagang,
                        teacher = it.teacher,
                        priority = it.priority,
                        type = it.type
                    )
                }
            }
        }else{
            flow{
                emit(null)
            }
        }
    }

     override fun getExams(): Flow<List<ExamBean>?> {
         return if(true){
             FuuApplication.db.examDao().getAll().map { it ->
                 it.map {
                     com.example.inject.bean.ExamBean(
                         examId = it.examId, name = it.name, xuefen = it.xuefen, teacher = it.teacher, address = it.address, zuohao = it.zuohao
                     )
                 }
             }
         }else{
             flow{
                 emit(null)
             }
         }
     }

     override fun getMassage(): Flow<List<MassageBean>?> {
         return if(true){
             FuuApplication.db.massageDao().getAll().map { it ->
                 it.map {
                     MassageBean(
                         title = it.title, time = it.time, content = it.content, origin = it.origin
                     )
                 }
             }
         }else{
             flow{
                 emit(null)
             }
         }
     }
     override fun getYearOptions(): Flow<List<YearOptionsBean>?> {
         return if(true){
             FuuApplication.db.yearOptionsDao().getAll().map { it ->
                 it.map {
                     YearOptionsBean(
                         yearOptionsId = it.yearOptionsId, yearOptionsName = it.yearOptionsName
                     )
                 }
             }
         }else{
             flow{
                 emit(null)
             }
         }
     }
 }


