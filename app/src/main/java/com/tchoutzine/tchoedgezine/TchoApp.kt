package com.tchoutzine.tchoedgezine

import android.app.Application
import androidx.room.Room
import com.tchoutzine.tchoedgezine.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TchoApp : Application() {
    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "tcho_db").build()
    }
}
