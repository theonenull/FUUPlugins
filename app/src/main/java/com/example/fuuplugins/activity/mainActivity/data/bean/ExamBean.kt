package com.example.fuuplugins.activity.mainActivity.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fuuplugins.activity.mainActivity.config.RoomConfig

/**
 * Create by NOSAE on 2020/9/28
 */
@Entity(tableName = RoomConfig.ExamDaoTableName)
data class ExamBean(
    @PrimaryKey(autoGenerate = true)
    var examId: Long = 0,
    var name: String = "",
    var xuefen: String = "",
    var teacher: String = "",
    var address: String = "",
    var zuohao: String = ""
) {
    constructor(
        name: String,
        xuefen: String,
        teacher: String,
        address: String,
        zuohao: String
    ) : this(0, name, xuefen, teacher, address, zuohao)
}