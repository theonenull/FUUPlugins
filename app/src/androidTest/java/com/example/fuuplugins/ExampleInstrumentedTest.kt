package com.example.fuuplugins

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fuuplugins.activity.mainActivity.repositories.CourseRepository
import com.example.fuuplugins.util.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

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
}