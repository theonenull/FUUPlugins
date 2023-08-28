package com.example.fuuplugins.activity.mainActivity.data.dao.ExamDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig
import com.example.fuuplugins.activity.mainActivity.data.bean.ExamBean
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Query("SELECT * FROM ${RoomConfig.ExamDaoTableName}")
    fun getAll(): Flow<List<ExamBean>>

    @Insert
    fun insertAll(vararg users: ExamBean)

    @Query("DELETE FROM ${RoomConfig.ExamDaoTableName}")
    fun clearAll()

    @Delete
    fun deleteMassage(examBean: ExamBean)

}