package com.example.fuuplugins

import android.app.Application
import androidx.room.Room
import com.example.fuuplugins.activity.mainActivity.data.dao.FuuDatabase

class FuuApplication: Application() {
    companion object{
        lateinit var instance : Application
        lateinit var db : FuuDatabase
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            this,
            FuuDatabase::class.java,
            "fuu"
        ).build()
    }

    override fun onTerminate() {
        super.onTerminate()
        db.close()
    }
}