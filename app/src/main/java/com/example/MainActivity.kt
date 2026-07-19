package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.StudyRepository
import com.example.ui.MainApp
import com.example.ui.StudyViewModel
import com.example.ui.theme.StudyStreakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as StudyApplication
        val repository = StudyRepository(app.database.studyDao())
        
        val viewModel: StudyViewModel by viewModels { ViewModelFactory(repository) }
        
        setContent {
            StudyStreakTheme {
                MainApp(viewModel)
            }
        }
    }
}

