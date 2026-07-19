package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val username: String = "Student",
    val xp: Int = 0,
    val level: Int = 1,
    val coins: Int = 0,
    val currentStreak: Int = 0,
    val lastStudyDate: Long = 0L
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val totalStudyTimeMinutes: Int = 0
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val date: Long,
    val durationMinutes: Int,
    val xpEarned: Int
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetMinutes: Int,
    val isWeekly: Boolean = false,
    val isCompleted: Boolean = false
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false
)
