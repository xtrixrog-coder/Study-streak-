package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Subject
import com.example.ui.StudyViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: StudyViewModel,
    onNavigateBack: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    
    LaunchedEffect(subjects) {
        if (selectedSubject == null && subjects.isNotEmpty()) {
            selectedSubject = subjects.first()
        }
    }

    var totalTimeMinutes by remember { mutableStateOf(25) }
    var timeRemainingSeconds by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeRemainingSeconds) {
        if (isRunning && timeRemainingSeconds > 0) {
            delay(1000)
            timeRemainingSeconds--
        } else if (isRunning && timeRemainingSeconds == 0) {
            isRunning = false
            selectedSubject?.let {
                viewModel.finishStudySession(it.id, totalTimeMinutes)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Timer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Subject Selector
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedSubject?.name ?: "Select Subject",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(subject.name) },
                            onClick = {
                                selectedSubject = subject
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Circular Timer
            val progress = timeRemainingSeconds.toFloat() / (totalTimeMinutes * 60).toFloat()
            val primaryColor = MaterialTheme.colorScheme.primary
            val trackColor = MaterialTheme.colorScheme.surfaceVariant

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(250.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = timeRemainingSeconds / 60
                    val seconds = timeRemainingSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Focus",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            isRunning = false
                            timeRemainingSeconds = totalTimeMinutes * 60
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(32.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { isRunning = !isRunning },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
