package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase

class StudyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "study_database"
        ).build()
    }
}
