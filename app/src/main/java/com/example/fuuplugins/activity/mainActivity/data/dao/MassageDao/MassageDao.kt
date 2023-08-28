package com.example.fuuplugins.activity.mainActivity.data.dao.MassageDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig.MassageDaoTableName
import com.example.fuuplugins.activity.mainActivity.data.bean.MassageBean
import kotlinx.coroutines.flow.Flow


@Dao
interface MassageDao {

    @Query("SELECT * FROM $MassageDaoTableName")
    fun getAll(): Flow<List<MassageBean>>

    @Insert
    fun insertAll(vararg users: MassageBean)

    @Query("DELETE FROM $MassageDaoTableName")
    fun clearAll()

    @Delete
    fun deleteMassage(massage: MassageBean)

}