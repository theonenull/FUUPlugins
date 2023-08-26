package com.example.fuuplugins.activity.mainActivity.data.dao.yearOptions

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig.YearOptionsDaoTableName
import com.example.fuuplugins.activity.mainActivity.data.yearOptions.YearOptionsBean
import kotlinx.coroutines.flow.Flow

@Dao
interface YearOptionsDao {

    @Query("SELECT * FROM $YearOptionsDaoTableName")
    fun getAll(): Flow<List<YearOptionsBean>>

    @Insert
    fun insertYearOptions(yearOptionsBean: List<YearOptionsBean>)

    @Query("DELETE FROM $YearOptionsDaoTableName")
    fun clearAll()

}