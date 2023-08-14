package com.example.fuuplugins

import android.app.Application

class FuuApplication: Application() {
    companion object{
        lateinit var instance : Application
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}