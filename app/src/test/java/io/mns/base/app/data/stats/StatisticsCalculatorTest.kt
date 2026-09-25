package io.mns.base.app.data.stats

import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class StatisticsCalculatorTest {

    private val zoneId = ZoneId.of("UTC")
    // Reference date: 2026-04-15 12:00:00 UTC (Wednesday)
    private val referenceDate = LocalDate.of(2026, 4, 15)
    private val nowMs = referenceDate.atTime(12, 0).atZone(zoneId).toInstant().toEpochMilli()

    @Test
    fun calculate_emptyInputs_returnsEmptyStatistics() {
        val stats = StatisticsCalculator.calculate(emptyList(), emptyList(), nowMs, zoneId)

        assertTrue(stats.isEmpty)
        assertEquals(0, stats.totalActive)
        assertEquals(0, stats.totalDone)
        assertEquals(0f, stats.completionRate, 0.01f)
        assertEquals(0, stats.completedToday)
        assertEquals(0, stats.completedThisWeek)
        assertEquals(0, stats.currentStreakDays)
        assertEquals("None", stats.bestDayOfWeek)
    }

    @Test
    fun calculate_withActiveAndCompleted_computesAccurateCompletionRate() {
        val todos = listOf(
            TodoItem("1", nowMs, "Active Task 1"),
            TodoItem("2", nowMs, "Active Task 2")
        )
        val done = listOf(
            DoneItem("d1", nowMs, "Done Task 1"),
            DoneItem("d2", nowMs, "Done Task 2")
        )

        val stats = StatisticsCalculator.calculate(todos, done, nowMs, zoneId)

        assertFalse(stats.isEmpty)
        assertEquals(2, stats.totalActive)
        assertEquals(2, stats.totalDone)
        assertEquals(4, stats.totalTasks)
        assertEquals(50f, stats.completionRate, 0.01f)
        assertEquals(2, stats.completedToday)
        assertEquals(2, stats.completedThisWeek)
    }

    @Test
    fun calculate_allDone_showsAllCaughtUp() {
        val todos = emptyList<TodoItem>()
        val done = listOf(
            DoneItem("d1", nowMs, "Done Task 1")
        )

        val stats = StatisticsCalculator.calculate(todos, done, nowMs, zoneId)

        assertEquals(100f, stats.completionRate, 0.01f)
        assertTrue(stats.motivationalTitle.contains("Caught Up"))
    }

    @Test
    fun calculate_weeklyActivity_populatesSevenDaysRollingWindow() {
        val yesterdayMs = referenceDate.minusDays(1).atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()
        val twoDaysAgoMs = referenceDate.minusDays(2).atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()

        val done = listOf(
            DoneItem("d1", nowMs, "Task Today 1"),
            DoneItem("d2", nowMs, "Task Today 2"),
            DoneItem("d3", yesterdayMs, "Task Yesterday"),
            DoneItem("d4", twoDaysAgoMs, "Task 2 Days Ago")
        )

        val stats = StatisticsCalculator.calculate(emptyList(), done, nowMs, zoneId)

        assertEquals(7, stats.weeklyActivity.size)
        val todayActivity = stats.weeklyActivity.last()
        assertTrue(todayActivity.isToday)
        assertEquals(2, todayActivity.count)

        val yesterdayActivity = stats.weeklyActivity[stats.weeklyActivity.size - 2]
        assertFalse(yesterdayActivity.isToday)
        assertEquals(1, yesterdayActivity.count)

        assertEquals(4, stats.completedThisWeek)
    }

    @Test
    fun calculate_streak_computesConsecutiveDays() {
        val day0Ms = nowMs // Today
        val day1Ms = referenceDate.minusDays(1).atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()
        val day2Ms = referenceDate.minusDays(2).atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()
        // Day 3 skipped
        val day4Ms = referenceDate.minusDays(4).atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()

        val done = listOf(
            DoneItem("d1", day0Ms, "T0"),
            DoneItem("d2", day1Ms, "T1"),
            DoneItem("d3", day2Ms, "T2"),
            DoneItem("d4", day4Ms, "T4")
        )

        val stats = StatisticsCalculator.calculate(emptyList(), done, nowMs, zoneId)

        // Streak is 3 (today, yesterday, 2 days ago)
        assertEquals(3, stats.currentStreakDays)
    }

    @Test
    fun calculate_bestDayOfWeek_identifiesDayWithHighestVolume() {
        // Wednesday (today)
        val wednesdayMs = nowMs
        // Tuesday (yesterday)
        val tuesdayMs = referenceDate.minusDays(1).atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()

        val done = listOf(
            DoneItem("d1", tuesdayMs, "T1"),
            DoneItem("d2", tuesdayMs, "T2"),
            DoneItem("d3", tuesdayMs, "T3"),
            DoneItem("d4", wednesdayMs, "T4")
        )

        val stats = StatisticsCalculator.calculate(emptyList(), done, nowMs, zoneId)

        assertEquals("Tuesday", stats.bestDayOfWeek)
    }

    @Test
    fun calculate_dailyGoal_calculatesTargetAndProgress() {
        val done = listOf(
            DoneItem("d1", nowMs, "T1"),
            DoneItem("d2", nowMs, "T2")
        )
        val stats = StatisticsCalculator.calculate(emptyList(), done, nowMs, zoneId, dailyGoal = 4)
        assertEquals(4, stats.dailyGoal)
        assertEquals(2, stats.completedToday)
        assertEquals(0.5f, stats.dailyGoalProgress, 0.01f)
    }
}
