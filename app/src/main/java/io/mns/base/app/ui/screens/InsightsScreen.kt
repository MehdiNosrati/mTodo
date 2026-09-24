package io.mns.base.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.R
import io.mns.base.app.data.Priority
import io.mns.base.app.data.stats.DayActivity
import io.mns.base.app.data.stats.PriorityStat
import io.mns.base.app.data.stats.TaskStatistics
import io.mns.base.app.ui.viewmodels.InsightsViewModel
import org.koin.androidx.compose.koinViewModel

private val Brand1 = Color(0xFF6366F1)
private val Brand2 = Color(0xFFA78BFA)
private val BrandBrush = Brush.linearGradient(
    colors = listOf(Brand1, Brand2),
    start = Offset.Zero,
    end = Offset.Infinite
)
private val GreenBrand = Color(0xFF10B981)
private val OrangeBrand = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onSettingsClick: () -> Unit,
    viewModel: InsightsViewModel = koinViewModel()
) {
    val statistics by viewModel.statistics.observeAsState(initial = TaskStatistics())
    InsightsScreenContent(
        statistics = statistics,
        onSettingsClick = onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreenContent(
    statistics: TaskStatistics,
    onSettingsClick: () -> Unit = {},
    animate: Boolean = true
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.insights),
                        style = TextStyle(
                            brush = BrandBrush,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Ambient orbs
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = 70.dp, y = (-50).dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(colors = listOf(Brand1.copy(alpha = 0.08f), Color.Transparent)),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = (-60).dp, y = 30.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.radialGradient(colors = listOf(Brand2.copy(alpha = 0.06f), Color.Transparent)),
                        CircleShape
                    )
            )

            if (statistics.isEmpty) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    InsightsEmptyState(animate = animate)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Motivational Banner
                    item(key = "motivational_banner") {
                        MotivationalCard(statistics = statistics)
                    }

                    // 2. 2x2 Metric Cards Grid
                    item(key = "kpi_grid") {
                        MetricKpiGrid(statistics = statistics, animate = animate)
                    }

                    // 3. Weekly Activity Bar Chart
                    item(key = "weekly_chart") {
                        WeeklyActivityChart(
                            activity = statistics.weeklyActivity,
                            completedThisWeek = statistics.completedThisWeek,
                            animate = animate
                        )
                    }

                    // 4. Productivity Summary
                    item(key = "productivity_summary") {
                        ProductivitySummaryCard(statistics = statistics)
                    }

                    // 5. Priority Distribution Card
                    if (statistics.priorityBreakdown.isNotEmpty()) {
                        item(key = "priority_breakdown") {
                            PriorityDistributionCard(
                                breakdown = statistics.priorityBreakdown,
                                animate = animate
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MotivationalCard(statistics: TaskStatistics) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Brand1.copy(alpha = 0.20f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(BrandBrush)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = if (statistics.currentStreakDays >= 3) {
                    Icons.Default.Star
                } else if (statistics.completionRate >= 80f) {
                    Icons.Default.ThumbUp
                } else {
                    Icons.AutoMirrored.Filled.TrendingUp
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statistics.motivationalTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = statistics.motivationalMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.90f)
                )
            }
        }
    }
}

@Composable
private fun MetricKpiGrid(statistics: TaskStatistics, animate: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Completion Rate
            KpiCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.completion_rate),
                value = "${statistics.completionRate.toInt()}%",
                subtitle = "${statistics.totalDone} of ${statistics.totalTasks} done",
                accentColor = Brand1,
                icon = Icons.Default.Done
            )

            // Current Streak
            KpiCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.current_streak),
                value = "${statistics.currentStreakDays} days",
                subtitle = if (statistics.currentStreakDays > 0) "Consecutive days" else "Complete 1 today",
                accentColor = OrangeBrand,
                icon = Icons.Default.DateRange
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Completed Tasks
            KpiCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.completed),
                value = "${statistics.totalDone}",
                subtitle = if (statistics.completedToday > 0) "+${statistics.completedToday} today" else "Finished tasks",
                accentColor = GreenBrand,
                icon = Icons.Default.Done
            )

            // Active Tasks
            KpiCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.active),
                value = "${statistics.totalActive}",
                subtitle = "In progress",
                accentColor = Brand2,
                icon = Icons.Default.Info
            )
        }
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = accentColor.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = accentColor
            )
        }
    }
}

@Composable
private fun WeeklyActivityChart(
    activity: List<DayActivity>,
    completedThisWeek: Int,
    animate: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Brand1.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.weekly_activity),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$completedThisWeek tasks completed in the last 7 days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val maxCount = (activity.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                activity.forEach { day ->
                    val fraction = if (maxCount > 0) (day.count.toFloat() / maxCount.toFloat()).coerceIn(0f, 1f) else 0f
                    val animatedFraction by animateFloatAsState(
                        targetValue = if (animate) fraction else fraction,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "barHeight_${day.dayName}"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Count label on top of bar
                        Text(
                            text = if (day.count > 0) day.count.toString() else "·",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (day.isToday) Brand1 else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Vertical Bar
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            val barHeightFraction = if (day.count > 0) animatedFraction.coerceAtLeast(0.12f) else 0f
                            if (barHeightFraction > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(barHeightFraction)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (day.isToday) BrandBrush else Brush.verticalGradient(
                                                listOf(Brand1.copy(alpha = 0.85f), Brand2.copy(alpha = 0.70f))
                                            )
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Day label (e.g. M, T, W)
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.Medium
                            ),
                            color = if (day.isToday) Brand1 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductivitySummaryCard(statistics: TaskStatistics) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Brand1.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.productivity_summary),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = OrangeBrand,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.best_day),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = statistics.bestDayOfWeek,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = GreenBrand,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Total Managed Tasks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${statistics.totalTasks}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun InsightsEmptyState(
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    var visible by remember { mutableStateOf(!animate) }
    LaunchedEffect(Unit) {
        if (animate) visible = true
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible || !animate,
        enter = fadeIn(tween(400)) + scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape, spotColor = Brand1.copy(alpha = 0.3f))
                    .background(BrandBrush, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.no_insights_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.no_insights_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PriorityDistributionCard(breakdown: List<PriorityStat>, animate: Boolean = true) {
    val nonNoneStats = breakdown.filter { it.priority != Priority.NONE }
    val totalPriorityTasks = nonNoneStats.sumOf { it.activeCount + it.doneCount }
    if (totalPriorityTasks == 0) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brand1.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = Brand1,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Priority Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$totalPriorityTasks prioritized tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                nonNoneStats.reversed().forEach { stat ->
                    val total = stat.activeCount + stat.doneCount
                    val rate = if (total > 0) stat.doneCount.toFloat() / total else 0f
                    val animatedProgress by animateFloatAsState(
                        targetValue = if (animate) rate else rate,
                        animationSpec = tween(600),
                        label = "priorityProgress"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(stat.priority.color, CircleShape)
                                )
                                Text(
                                    text = stat.priority.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${stat.doneCount} done · ${stat.activeCount} active",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = stat.priority.color,
                            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

