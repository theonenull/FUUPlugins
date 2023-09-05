package com.example.fuuplugins.weight

import android.content.Context
import android.widget.Toast
import androidx.glance.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.background
import androidx.glance.text.Text
import androidx.room.Room
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.bean.CourseBean
import com.example.fuuplugins.activity.mainActivity.data.dao.FuuDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CourseWeight(
    val list: List<CourseBean>
) : GlanceAppWidget(){
    @Composable
    override fun Content() {
        val db = Room.databaseBuilder(
            FuuApplication.instance,
            FuuDatabase::class.java,
            "fuu"
        ).build().courseDao().getAll().collectAsState(initial = list)

        LazyColumn(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(30.dp)
                .background(Color(217, 217, 238))
                .padding(10.dp)
        ) {
            item {
                Button(text = "sss", onClick = actionRunCallback<RefreshAction>())
            }
            itemsIndexed(db.value){ index, item ->
                Column (
                    modifier = GlanceModifier
                        .padding(top = 3.dp)
                        .cornerRadius(3.dp)
                        .padding(3.dp)
                ){
                    Text(text = item.kcName)
                    Text(text = item.kcLocation)
                    Text(text = "${item.kcStartTime}--${item.kcEndTime}")
                }
            }
        }

    }

}




class CourseWidgetReceiver : GlanceAppWidgetReceiver() {
    var list : List<CourseBean> = listOf()
    override val glanceAppWidget = CourseWeight(
        list
    )
}


class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val scope = CoroutineScope(Job())
        scope.launch (Dispatchers.IO){
            val db = Room.databaseBuilder(
                context,
                FuuDatabase::class.java,
                "fuu"
            ).build()
            val list = db.courseDao().getAll().first()
            withContext(Dispatchers.Main){
                Toast.makeText(context,list.toString(),Toast.LENGTH_SHORT).show()
            }

//            CourseWeight(list).update(context, glanceId)
        }
    }
}

//        val scope = CoroutineScope(Job())
//        scope.launch (Dispatchers.IO){
//            val db = Room.databaseBuilder(
//                context,
//                FuuDatabase::class.java,
//                "fuu"
//            ).build()
//            val list = db.courseDao().getAll().first()
////            withContext(Dispatchers.Main){
////                Toast.makeText(context,list.toString(),Toast.LENGTH_SHORT).show()
////            }
//            val manager = GlanceAppWidgetManager(context)
//            val widget = CourseWeight(list)
//            val glanceIds = manager.getGlanceIds(widget.javaClass)
//            glanceIds.forEach {
//                widget.update(context, it)
//            }
////            glanceIds.forEach { glanceId ->
////                widget.update(context, glanceId)
////            }
////            actionRunCallback<myActionRun>(
////                actionParametersOf(dataKey to list)
////            )
//
//        }