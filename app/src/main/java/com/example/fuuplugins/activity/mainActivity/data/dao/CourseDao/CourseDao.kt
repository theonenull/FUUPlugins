package com.example.fuuplugins.activity.mainActivity.data.dao.CourseDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig.CourseDaoTableName
import com.example.fuuplugins.activity.mainActivity.data.bean.CourseBean
import kotlinx.coroutines.flow.Flow


@Dao
interface CourseDao {

    @Query("SELECT * FROM $CourseDaoTableName")
    fun getAll(): Flow<List<CourseBean>>

    @Insert
    fun insertAll(vararg users: CourseBean)


    @Insert
    @JvmSuppressWildcards
    fun insertCourses(users: List<CourseBean>)


    @Query("DELETE FROM $CourseDaoTableName")
    fun clearAll()

    @Query("DELETE FROM $CourseDaoTableName WHERE kcYear = :year and kcXuenian = :xuenain")
    fun clearByXq(year: String,xuenain:String)
}
