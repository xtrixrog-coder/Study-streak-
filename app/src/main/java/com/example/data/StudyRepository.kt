package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class StudyRepository(private val dao: StudyDao) {
    val userStats: Flow<UserStats?> = dao.getUserStats()
    val subjects: Flow<List<Subject>> = dao.getAllSubjects()
    val sessions: Flow<List<StudySession>> = dao.getAllSessions()
    val goals: Flow<List<Goal>> = dao.getAllGoals()
    val achievements: Flow<List<Achievement>> = dao.getAllAchievements()

    suspend fun initializeDefaultData() {
        if (dao.getUserStats().firstOrNull() == null) {
            dao.insertUserStats(UserStats())
            val initialAchievements = listOf(
                Achievement(name = "First Step", description = "Complete your first study session", iconName = "star"),
                Achievement(name = "On Fire", description = "Reach a 3-day streak", iconName = "local_fire_department"),
                Achievement(name = "Bookworm", description = "Study for 10 hours total", iconName = "menu_book"),
                Achievement(name = "Master", description = "Reach level 10", iconName = "military_tech")
            )
            dao.insertAchievements(initialAchievements)
            dao.insertSubject(Subject(name = "Mathematics", colorHex = "#FF5722"))
            dao.insertSubject(Subject(name = "Science", colorHex = "#4CAF50"))
            dao.insertSubject(Subject(name = "History", colorHex = "#2196F3"))
            
            dao.insertGoal(Goal(title = "Study Math for 2h", targetMinutes = 120))
            dao.insertGoal(Goal(title = "Read History", targetMinutes = 60))
        }
    }

    suspend fun addStudySession(subjectId: Int, durationMinutes: Int, xpEarned: Int) {
        val session = StudySession(
            subjectId = subjectId,
            date = System.currentTimeMillis(),
            durationMinutes = durationMinutes,
            xpEarned = xpEarned
        )
        dao.insertSession(session)

        val subject = subjects.firstOrNull()?.find { it.id == subjectId }
        if (subject != null) {
            dao.updateSubject(subject.copy(totalStudyTimeMinutes = subject.totalStudyTimeMinutes + durationMinutes))
        }

        val stats = userStats.firstOrNull() ?: UserStats()
        var newXp = stats.xp + xpEarned
        var newLevel = stats.level
        var newCoins = stats.coins + (xpEarned / 10)
        
        while (newXp >= newLevel * 100) {
            newXp -= newLevel * 100
            newLevel++
            newCoins += 50
        }
        
        // Update streak logic (simplified)
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val lastStudyDateDay = stats.lastStudyDate / (1000 * 60 * 60 * 24)
        
        val newStreak = if (today - lastStudyDateDay == 1L) {
            stats.currentStreak + 1
        } else if (today - lastStudyDateDay > 1L) {
            1
        } else {
            stats.currentStreak.coerceAtLeast(1)
        }

        dao.updateUserStats(
            stats.copy(
                xp = newXp,
                level = newLevel,
                coins = newCoins,
                currentStreak = newStreak,
                lastStudyDate = System.currentTimeMillis()
            )
        )
        
        // Check for achievements
        checkAchievements(newLevel, newStreak)
    }

    private suspend fun checkAchievements(level: Int, streak: Int) {
        val currentAchievements = achievements.firstOrNull() ?: return
        
        currentAchievements.forEach { ach ->
            if (!ach.isUnlocked) {
                var shouldUnlock = false
                if (ach.name == "First Step") shouldUnlock = true
                if (ach.name == "On Fire" && streak >= 3) shouldUnlock = true
                if (ach.name == "Master" && level >= 10) shouldUnlock = true
                
                if (shouldUnlock) {
                    dao.updateAchievement(ach.copy(isUnlocked = true))
                }
            }
        }
    }
}
