package com.example.fuuplugins.activity.mainActivity.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fuuplugins.activity.mainActivity.data.bean.CourseBean
import com.example.fuuplugins.activity.mainActivity.data.dao.CourseDao.CourseDao
import com.example.fuuplugins.activity.mainActivity.data.dao.ExamDao.ExamDao
import com.example.fuuplugins.activity.mainActivity.data.dao.MassageDao.MassageDao
import com.example.fuuplugins.activity.mainActivity.data.bean.ExamBean
import com.example.fuuplugins.activity.mainActivity.data.bean.MassageBean
import com.example.fuuplugins.activity.mainActivity.data.bean.YearOptionsBean
import com.example.fuuplugins.activity.mainActivity.data.dao.yearOptions.YearOptionsDao

@Database(entities = [CourseBean::class, MassageBean::class, YearOptionsBean::class, ExamBean::class],exportSchema=false, version = 2)
abstract class FuuDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun massageDao(): MassageDao
    abstract fun yearOptionsDao(): YearOptionsDao
    abstract fun examDao() : ExamDao
}

