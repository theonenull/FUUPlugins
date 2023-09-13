package com.example.fuuplugins.weight

import android.content.Context
import androidx.glance.layout.*
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.bean.ExamBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.util.concurrent.TimeUnit


class ExamWeight() : GlanceAppWidget(){

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val list = MutableStateFlow<List<ExamBean>>(listOf())
        try {
            list.value = FuuApplication.db.examDao().getAll().first()
        }catch (e:Exception){

        }finally {
            println("weight")
        }
        provideContent {
            LazyColumn(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(10.dp)
                    .background(Color(217, 217, 238))
                    .padding(10.dp)
            ) {
                item{
                    Text(
                        text = "考试通知",
                        modifier = GlanceModifier
                            .padding(top = 3.dp)
                            .cornerRadius(5.dp)
                            .padding(5.dp),
                        style = TextStyle(color = ColorProvider(Color(36, 205, 185)),fontSize = 20.sp )
                    )
                }
                itemsIndexed(list.value){ index, item ->
                    Column {
                        Column (
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .cornerRadius(5.dp)
                                .background(ColorProvider(Color(46, 196, 211)))
                                .padding(5.dp)
                        ){
                            Text(text = item.name)
                            Text(text = item.teacher)
                            Text(text = if(item.address!="") item.address else "考试未确定")
                        }
                        Spacer(modifier = GlanceModifier.height(5.dp))
                    }

                }
            }

            SideEffect {
                WorkWorker.enqueue(context, list, id)
            }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        WorkWorker.cancel(context, glanceId = glanceId)
    }
}




class CourseWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = ExamWeight()
}


class WorkWorker(
     private val context: Context,
     workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
     companion object {
           private val uniqueWorkName = WorkWorker::class.java.simpleName
           // 排队进行工作
           fun enqueue(context: Context, size: MutableStateFlow<List<ExamBean>>, glanceId: GlanceId, force: Boolean = true) {
                 val manager = WorkManager.getInstance(context)
                 val requestBuilder = PeriodicWorkRequestBuilder<WorkWorker>(30,TimeUnit.MINUTES)
                     .addTag(glanceId.toString())

                 val workPolicy = if (force) {
                     ExistingPeriodicWorkPolicy.UPDATE
                      } else {
                     ExistingPeriodicWorkPolicy.KEEP
                  }

                 manager.enqueueUniquePeriodicWork(
                   uniqueWorkName + LocalTime.now() ,
                       workPolicy,
                       requestBuilder.build()
                    )
                }
         
           fun cancel(context: Context, glanceId: GlanceId) {
                 WorkManager.getInstance(context).cancelAllWorkByTag(glanceId.toString())
           }
     }

     override suspend fun doWork(): Result {
           // 需要执行的操作
         val manager = GlanceAppWidgetManager(context)
         val widget = ExamWeight()
         val glanceIds = manager.getGlanceIds(widget.javaClass)
         glanceIds.forEach { glanceId ->
             widget.update(context, glanceId)
         }
         return Result.success()
     }
}

