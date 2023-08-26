package com.example.fuuplugins.activity.mainActivity.data.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.course.CourseBean
import com.example.fuuplugins.activity.mainActivity.data.dao.CourseDao.CourseDao
import com.example.fuuplugins.activity.mainActivity.data.dao.MassageDao.MassageDao
import com.example.fuuplugins.activity.mainActivity.data.dao.yearOptions.YearOptionsDao
import com.example.fuuplugins.activity.mainActivity.data.massage.MassageBean
import com.example.fuuplugins.activity.mainActivity.data.yearOptions.YearOptionsBean

@Database(entities = [CourseBean::class,MassageBean::class, YearOptionsBean::class], version = 1)
abstract class FuuDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun massageDao(): MassageDao
    abstract fun yearOptionsDao(): YearOptionsDao
}

