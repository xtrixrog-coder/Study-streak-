package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Achievement
import com.example.data.Goal
import com.example.data.StudyRepository
import com.example.data.StudySession
import com.example.data.Subject
import com.example.data.UserStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyViewModel(private val repository: StudyRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDefaultData()
        }
    }

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val subjects: StateFlow<List<Subject>> = repository.subjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySession>> = repository.sessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<Goal>> = repository.goals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<Achievement>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun finishStudySession(subjectId: Int, durationMinutes: Int) {
        viewModelScope.launch {
            val xpEarned = durationMinutes * 2 // 2 XP per minute
            repository.addStudySession(subjectId, durationMinutes, xpEarned)
        }
    }
}
