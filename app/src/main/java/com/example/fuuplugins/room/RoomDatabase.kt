package com.example.fuuplugins.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
//
//@Database(entities = [CourseBean::class, Exam::class, ExperimentBean::class, FDScore::class,
//    HomeworkBean::class, MemoBean::class, UserStatistics::class], version = 25, exportSchema = true)
//@TypeConverters(RoomConverter::class)
//abstract class RoomDB : RoomDatabase() {
//
//    abstract fun courseDao(): CourseDao
////    abstract fun examDao(): ExamDao
////    abstract fun experimentDao(): ExperimentDao
////    abstract fun fdScoreDao(): FDScoreDao
////    abstract fun homeworkDao(): HomeworkDao
////    abstract fun memoDao(): MemoDao
////    abstract fun userStatisticsDao(): UserStatisticsDao
//
//    companion object {
//        private const val DB_NAME = "fzu_db"
//
//        lateinit var instance: RoomDB
//
//        fun init(context: Context) {
//            instance = Room.databaseBuilder(
//                context.applicationContext,
//                RoomDB::class.java,
//                DB_NAME
//            )
//                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
//                .addMigrations(*RoomMigration.build())
//                .build()
//        }
//    }
//}