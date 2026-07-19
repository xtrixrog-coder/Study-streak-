package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserStats::class, Subject::class, StudySession::class, Goal::class, Achievement::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao
}
