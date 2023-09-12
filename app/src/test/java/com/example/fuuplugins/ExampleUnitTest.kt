package com.example.fuuplugins

import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val currentDate = LocalDate.now()
        val dayOfWeek = currentDate.dayOfWeek.value

        println("今天是星期$dayOfWeek")
    }
}