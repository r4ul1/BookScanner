package com.example.mybookscanner

import android.app.Application

class MainApplication: Application() {

    companion object{
        var token = ""
    }

    override fun onCreate() {
        super.onCreate()
    }
}