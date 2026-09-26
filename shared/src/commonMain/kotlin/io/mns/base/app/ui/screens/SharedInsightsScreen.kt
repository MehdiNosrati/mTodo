package io.mns.base.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.data.Priority
import io.mns.base.app.data.stats.TaskStatistics
import io.mns.base.app.ui.viewmodels.InsightsViewModel
import org.koin.compose.viewmodel.koinViewModel

private val Brand1 = Color(0xFF6366F1)
private val Brand2 = Color(0xFFA78BFA)
private val BrandBrush = Brush.linearGradient(
    colors = listOf(Brand1, Brand2),
    start = Offset.Zero,
    end = Offset.Infinite
)
private val GreenBrand = Color(0xFF10B981)
private val OrangeBrand = Color(0xFFF59E0B)

@Composable
fun SharedInsightsScreen(
    viewModel: InsightsViewModel = koinViewModel()
) {
    val stats by viewModel.statistics.collectAsState()

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Insights",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Track your productivity and habits",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Motivational Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Brand1,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(BrandBrush)
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stats.motivationalTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stats.motivationalMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Stats Quick Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Streak",
                        value = "${stats.currentStreakDays} days",
                        icon = "🔥",
                        color = OrangeBrand,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Completion",
                        value = "${stats.completionRate.toInt()}%",
                        icon = "📈",
                        color = GreenBrand,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Today",
                        value = "${stats.completedToday}/${stats.dailyGoal}",
                        icon = "🎯",
                        color = Brand1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weekly Activity Chart
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Weekly Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val maxCount = stats.weeklyActivity.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            stats.weeklyActivity.forEach { day ->
                                val heightFraction = (day.count.toFloat() / maxCount).coerceIn(0.08f, 1f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (day.count > 0) {
                                        Text(
                                            text = day.count.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (day.isToday) Brand1 else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(22.dp)
                                            .fillMaxHeight(heightFraction)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                if (day.isToday) BrandBrush else Brush.linearGradient(
                                                    listOf(
                                                        MaterialTheme.colorScheme.outlineVariant,
                                                        MaterialTheme.colorScheme.outlineVariant
                                                    )
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = day.dayLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (day.isToday) Brand1 else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Priority Breakdown Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Priority Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        stats.priorityBreakdown.filter { it.priority != Priority.NONE }.forEach { pStat ->
                            val color = when (pStat.priority) {
                                Priority.HIGH -> Color(0xFFEF4444)
                                Priority.MEDIUM -> Color(0xFFF59E0B)
                                Priority.LOW -> Color(0xFF10B981)
                                else -> MaterialTheme.colorScheme.outline
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = pStat.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "${pStat.activeCount} active · ${pStat.doneCount} done",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
