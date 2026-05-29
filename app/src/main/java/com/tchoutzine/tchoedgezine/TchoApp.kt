package com.tchoutzine.tchoedgezine

import android.app.Application
import android.content.ComponentCallbacks2
import androidx.room.Room
import com.tchoutzine.tchoedgezine.ai.GemmaInference
import com.tchoutzine.tchoedgezine.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TchoApp : Application() {
    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "tcho_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    // Libère le LLM seulement sous pression mémoire critique — pas à chaque mise en arrière-plan
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            GemmaInference.getInstance(this).release()
        }
    }
}
