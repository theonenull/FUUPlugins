package com.example.fuuplugins.activity.mainActivity.data.yearOptions

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig

@Entity(tableName = RoomConfig.YearOptionsDaoTableName)
data class YearOptionsBean(
    @PrimaryKey(autoGenerate = true)
    var yearOptionsId : Long = 0,
    var yearOptionsName : String = "",
)