package com.example.fuuplugins.activity.mainActivity.data.massage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig


@Entity(tableName = RoomConfig.MassageDaoTableName)
data class MassageBean(
    @PrimaryKey(autoGenerate = true)
    var massageId : Long = 0,
    var title : String = "",
    var time : String = "",
    var content : String = "",
    var origin : String = "",
)