package com.example.fuuplugins.activity.mainActivity.data.dao.CourseDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig.CourseDaoTableName
import com.example.fuuplugins.activity.mainActivity.data.course.CourseBean
import kotlinx.coroutines.flow.Flow


@Dao
interface CourseDao {

    @Query("SELECT * FROM $CourseDaoTableName")
    fun getAll(): Flow<List<CourseBean>>

    @Insert
    fun insertAll(vararg users: CourseBean)

    @Query("DELETE FROM $CourseDaoTableName")
    fun clearAll()

}
