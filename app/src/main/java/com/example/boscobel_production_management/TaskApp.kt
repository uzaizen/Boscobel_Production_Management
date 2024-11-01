package com.example.boscobel_production_management

import android.app.Application
import android.util.Log
import io.realm.Realm

class TaskApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}