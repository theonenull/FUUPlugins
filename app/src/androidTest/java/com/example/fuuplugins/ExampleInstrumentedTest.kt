package com.example.fuuplugins

import android.util.Log
import androidx.compose.ui.input.key.Key.Companion.Sleep
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fuuplugins.activity.mainActivity.repositories.CourseRepository
import com.example.fuuplugins.activity.mainActivity.repositories.PluginRepository
import com.example.fuuplugins.util.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.io.File
import java.io.FileFilter

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.fuuplugins", appContext.packageName)
        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch(Dispatchers.IO) {
            CourseRepository.getCourseStateHTML()
                .flatMapConcat {
                    CourseRepository.getCourses("202202023",it)
                }
                .flatMapConcat {
                    CourseRepository.getCoursesHTML(it,"202202023")
                }
                .collectLatest {
                    debug(it.toString())
                }
        }
    }

    @Test
    fun testPluginsLoad(){
        val dir = File(FuuApplication.pluginsPathForApk)
        val fileFilter = FileFilter { file -> file.isDirectory && file.name.contains("plugin") }
        val files = dir.listFiles(fileFilter)
        println(files.size)
        if (files.size == 0) {
            println("目录不存在或它不是一个目录")
        } else {
            for (i in files.indices) {
                val filename = files[i]
                println(filename.toString())
            }
        }
    }

    @Test
    fun testPluginsList(){
        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            Log.d("sssssssssssss","data------------------------------------------")
            val  data = PluginRepository.getPluginList()
                .collect{
                    Log.d("sssssssssssss",it.toString())
                }

        }
        runBlocking {
            delay(10000)
        }
    }
}