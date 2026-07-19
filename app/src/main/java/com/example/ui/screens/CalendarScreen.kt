package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.example.data.StudySession
import com.example.ui.StudyViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: StudyViewModel) {
    val sessions by viewModel.sessions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Activity Graph",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            }
            item {
                ActivityGraph(sessions = sessions)
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Recent Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            }
            
            val recentSessions = sessions.take(10)
            items(recentSessions.size) { index ->
                val session = recentSessions[index]
                SessionCard(session)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (recentSessions.isEmpty()) {
                item {
                    Text(
                        text = "No study sessions yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityGraph(sessions: List<StudySession>) {
    // Basic representation of a GitHub style graph. 
    // Usually 7 rows (days of week) and N columns (weeks).
    // For simplicity, we just calculate the last 70 days.
    
    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)
    val todayTime = today.timeInMillis
    
    val sessionCounts = mutableMapOf<Long, Int>()
    sessions.forEach { session ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = session.date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dayTime = cal.timeInMillis
        sessionCounts[dayTime] = (sessionCounts[dayTime] ?: 0) + session.durationMinutes
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "ACTIVITY MAP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            val columns = 10
            val rows = 7
            
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (c in 0 until columns) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (r in 0 until rows) {
                            val daysAgo = (columns - 1 - c) * 7 + (6 - r)
                            val dayTime = todayTime - (daysAgo * 24 * 60 * 60 * 1000L)
                            
                            val duration = sessionCounts[dayTime] ?: 0
                            
                            val color = when {
                                duration == 0 -> Color.White.copy(alpha = 0.05f)
                                duration < 30 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                duration < 60 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                duration < 90 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                else -> MaterialTheme.colorScheme.primary
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                            )
                        }
                    }
                }
                
                Column(
                    modifier = Modifier.weight(1f).height((16 * 7 + 6 * 6).dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("More", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Less", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun SessionCard(session: StudySession) {
    val cal = Calendar.getInstance()
    cal.timeInMillis = session.date
    val dateString = "${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.YEAR)}"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Subject ID: ${session.subjectId}", fontWeight = FontWeight.Bold) // Ideally join with Subject name
                Text(dateString, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "+${session.xpEarned} XP",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

