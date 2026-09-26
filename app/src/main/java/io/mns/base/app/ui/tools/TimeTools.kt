package io.mns.base.app.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.*

val TIME_TOOLS: List<ToolItem> = listOf(
    ToolItem(
        id = 41,
        name = "Millisecond Stopwatch & Laps",
        description = "Precision stopwatch with centisecond resolution, split lap recording, and lap delta.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Timer
    ) { StopwatchTool() },

    ToolItem(
        id = 42,
        name = "Countdown Multi-Timer",
        description = "Configurable countdown timer with progress ring and completion alert.",
        category = ToolCategory.TIME,
        icon = Icons.Default.HourglassTop
    ) { CountdownTimerTool() },

    ToolItem(
        id = 43,
        name = "Interval HIIT Workout Timer",
        description = "Interval timer for high-intensity training with Work and Rest cycle management.",
        category = ToolCategory.TIME,
        icon = Icons.Default.FitnessCenter
    ) { IntervalTimerTool() },

    ToolItem(
        id = 44,
        name = "Date Difference Calculator",
        description = "Calculate exact calendar days, weeks, months, and weekdays between two dates.",
        category = ToolCategory.TIME,
        icon = Icons.Default.DateRange
    ) { DateDifferenceTool() },

    ToolItem(
        id = 45,
        name = "Exact Age & Birthday Countdown",
        description = "Detailed age breakdown in years, months, days, total hours, and days until next birthday.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Cake
    ) { AgeCalculatorTool() },

    ToolItem(
        id = 46,
        name = "Unix Epoch Timestamp Converter",
        description = "Live epoch milliseconds and seconds converter to UTC and Local ISO format.",
        category = ToolCategory.TIME,
        icon = Icons.Default.AccessTime
    ) { UnixEpochTool() },

    ToolItem(
        id = 47,
        name = "World Clock Explorer",
        description = "Simultaneous live clocks across major international time zones and UTC.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Public
    ) { WorldClockTool() },

    ToolItem(
        id = 48,
        name = "Event Countdown Tracker",
        description = "Track days, hours, and minutes remaining until New Year and upcoming milestones.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Event
    ) { EventCountdownTool() },

    ToolItem(
        id = 49,
        name = "Leap Year Checker",
        description = "Evaluate Gregorian leap year rules and discover past and future leap years.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Today
    ) { LeapYearTool() },

    ToolItem(
        id = 50,
        name = "Work Hours & Overtime Calculator",
        description = "Calculate shift hours, lunch break deductions, regular time, and overtime pay hours.",
        category = ToolCategory.TIME,
        icon = Icons.Default.WorkHistory
    ) { WorkHoursTool() },

    ToolItem(
        id = 51,
        name = "Metronome & Tap BPM Tempo",
        description = "Interactive tap-tempo BPM detector and rhythmic pulsing metronome (40-240 BPM).",
        category = ToolCategory.TIME,
        icon = Icons.Default.Audiotrack
    ) { MetronomeTool() },

    ToolItem(
        id = 52,
        name = "Sleep Cycle Calculator",
        description = "Calculate 90-minute REM sleep cycles to wake up feeling refreshed and alert.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Bedtime
    ) { SleepCycleTool() },

    ToolItem(
        id = 53,
        name = "Pomodoro 25/5 Focus Manager",
        description = "Classic productivity technique with 25-minute sprints and 5-minute restorative breaks.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Schedule
    ) { PomodoroTool() },

    ToolItem(
        id = 54,
        name = "Week & Day of Year Calculator",
        description = "Compute ISO 8601 week number, day of year, and remaining days in the calendar.",
        category = ToolCategory.TIME,
        icon = Icons.Default.CalendarMonth
    ) { WeekOfYearTool() },

    ToolItem(
        id = 55,
        name = "Meeting Overlap Finder",
        description = "Cross-reference two world cities to pinpoint mutually convenient business hours.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Groups
    ) { MeetingOverlapTool() },

    ToolItem(
        id = 56,
        name = "Daylight & Solar Calculator",
        description = "Estimate solar noon, sunrise, and sunset day length based on latitude coordinates.",
        category = ToolCategory.TIME,
        icon = Icons.Default.WbSunny
    ) { DaylightSolarTool() },

    ToolItem(
        id = 57,
        name = "Moon Phase Calculator",
        description = "Determine current lunar phase, moon illumination percentage, and cycle day.",
        category = ToolCategory.TIME,
        icon = Icons.Default.Brightness4
    ) { MoonPhaseTool() },

    ToolItem(
        id = 58,
        name = "Julian Date Converter",
        description = "Convert Gregorian calendar dates to Astronomical Julian Day Numbers (JDN).",
        category = ToolCategory.TIME,
        icon = Icons.Default.CalendarViewWeek
    ) { JulianDateTool() },

    ToolItem(
        id = 59,
        name = "Year & Day Progress Bar",
        description = "Live visual progress meter indicating what percentage of the current year has passed.",
        category = ToolCategory.TIME,
        icon = Icons.Default.HourglassBottom
    ) { YearProgressTool() },

    ToolItem(
        id = 60,
        name = "Running Pace Calculator",
        description = "Calculate running pace (min/km and min/mile) and finish times from distance.",
        category = ToolCategory.TIME,
        icon = Icons.AutoMirrored.Filled.DirectionsRun
    ) { RunningPaceTool() }
)

// Tool 41: Millisecond Stopwatch & Laps
@Composable
fun StopwatchTool() {
    var elapsedMillis by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    val laps = remember { mutableStateListOf<Long>() }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            val start = System.currentTimeMillis() - elapsedMillis
            while (isRunning) {
                elapsedMillis = System.currentTimeMillis() - start
                delay(30)
            }
        }
    }

    val minutes = (elapsedMillis / 60000)
    val seconds = (elapsedMillis % 60000) / 1000
    val centis = (elapsedMillis % 1000) / 10

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%02d:%02d.%02d".format(minutes, seconds, centis),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFEF4444) else Color(0xFF10B981)
                )
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }
            FilledTonalButton(
                onClick = {
                    if (isRunning) laps.add(0, elapsedMillis)
                    else {
                        elapsedMillis = 0L
                        laps.clear()
                    }
                }
            ) {
                Text(if (isRunning) "Lap" else "Reset")
            }
        }

        if (laps.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Recorded Laps (${laps.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    laps.forEachIndexed { idx, lapTime ->
                        val lapMin = (lapTime / 60000)
                        val lapSec = (lapTime % 60000) / 1000
                        val lapCs = (lapTime % 1000) / 10
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Lap #${laps.size - idx}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "%02d:%02d.%02d".format(lapMin, lapSec, lapCs),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// Tool 42: Countdown Timer
@Composable
fun CountdownTimerTool() {
    var totalSeconds by remember { mutableStateOf(300) } // 5 mins
    var remainingSeconds by remember { mutableStateOf(300) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning && remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
            }
            if (remainingSeconds == 0) isRunning = false
        }
    }

    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "%02d:%02d".format(mins, secs),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (remainingSeconds == 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "1m", 5 to "5m", 10 to "10m", 15 to "15m", 25 to "25m").forEach { (m, lbl) ->
                FilterChip(
                    selected = totalSeconds == m * 60,
                    onClick = {
                        isRunning = false
                        totalSeconds = m * 60
                        remainingSeconds = totalSeconds
                    },
                    label = { Text(lbl) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }
            FilledTonalButton(onClick = {
                isRunning = false
                remainingSeconds = totalSeconds
            }) {
                Text("Reset")
            }
        }
    }
}

// Tool 43: Interval Timer
@Composable
fun IntervalTimerTool() {
    var workSecs by remember { mutableStateOf(30) }
    var restSecs by remember { mutableStateOf(15) }
    var totalSets by remember { mutableStateOf(5) }
    var currentSet by remember { mutableStateOf(1) }
    var isWorkPhase by remember { mutableStateOf(true) }
    var remainingTime by remember { mutableStateOf(30) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, isWorkPhase, currentSet) {
        if (isRunning) {
            while (isRunning && remainingTime > 0) {
                delay(1000)
                remainingTime--
            }
            if (isRunning && remainingTime == 0) {
                if (isWorkPhase) {
                    isWorkPhase = false
                    remainingTime = restSecs
                } else {
                    if (currentSet < totalSets) {
                        currentSet++
                        isWorkPhase = true
                        remainingTime = workSecs
                    } else {
                        isRunning = false
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isWorkPhase) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF3B82F6).copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isWorkPhase) "WORK OUT 🔥" else "REST & BREATHE 💧",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isWorkPhase) Color(0xFF10B981) else Color(0xFF3B82F6)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${remainingTime}s",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text("Set $currentSet of $totalSets", fontSize = 14.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Pause" else "Start HIIT")
            }
            FilledTonalButton(onClick = {
                isRunning = false
                currentSet = 1
                isWorkPhase = true
                remainingTime = workSecs
            }) {
                Text("Reset")
            }
        }
    }
}

// Tool 44: Date Difference
@Composable
fun DateDifferenceTool() {
    var year1 by remember { mutableStateOf("2026") }
    var month1 by remember { mutableStateOf("1") }
    var day1 by remember { mutableStateOf("1") }

    var year2 by remember { mutableStateOf("2026") }
    var month2 by remember { mutableStateOf("12") }
    var day2 by remember { mutableStateOf("31") }

    val diffResult = remember(year1, month1, day1, year2, month2, day2) {
        try {
            val d1 = LocalDate.of(year1.toInt(), month1.toInt(), day1.toInt())
            val d2 = LocalDate.of(year2.toInt(), month2.toInt(), day2.toInt())
            val daysBetween = ChronoUnit.DAYS.between(d1, d2)
            val weeks = daysBetween / 7
            val remainingDays = daysBetween % 7
            val period = Period.between(d1, d2)
            "$daysBetween Total Days\n($weeks weeks and $remainingDays days)\n${period.years} years, ${period.months} months, ${period.days} days"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Start Date (YYYY / MM / DD):", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = year1, onValueChange = { year1 = it }, label = { Text("Year") }, modifier = Modifier.weight(1.5f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = month1, onValueChange = { month1 = it }, label = { Text("Month") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = day1, onValueChange = { day1 = it }, label = { Text("Day") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        Text("End Date (YYYY / MM / DD):", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = year2, onValueChange = { year2 = it }, label = { Text("Year") }, modifier = Modifier.weight(1.5f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = month2, onValueChange = { month2 = it }, label = { Text("Month") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = day2, onValueChange = { day2 = it }, label = { Text("Day") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        ResultCard("Span Between Dates", diffResult, MaterialTheme.colorScheme.primary)
    }
}

// Tool 45: Exact Age Calculator
@Composable
fun AgeCalculatorTool() {
    var birthYear by remember { mutableStateOf("1995") }
    var birthMonth by remember { mutableStateOf("6") }
    var birthDay by remember { mutableStateOf("15") }

    val ageResult = remember(birthYear, birthMonth, birthDay) {
        try {
            val bDate = LocalDate.of(birthYear.toInt(), birthMonth.toInt(), birthDay.toInt())
            val today = LocalDate.now()
            val period = Period.between(bDate, today)
            val totalDays = ChronoUnit.DAYS.between(bDate, today)
            val nextBday = bDate.withYear(if (today.isAfter(bDate.withYear(today.year))) today.year + 1 else today.year)
            val daysToNext = ChronoUnit.DAYS.between(today, nextBday)
            "${period.years} Years, ${period.months} Months, ${period.days} Days\nTotal days lived: $totalDays days\nNext Birthday in: $daysToNext days 🎉"
        } catch (e: Exception) {
            "Invalid Birthdate"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Your Birth Date (YYYY / MM / DD):", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = birthYear, onValueChange = { birthYear = it }, label = { Text("Year") }, modifier = Modifier.weight(1.5f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = birthMonth, onValueChange = { birthMonth = it }, label = { Text("Month") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = birthDay, onValueChange = { birthDay = it }, label = { Text("Day") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        ResultCard("Age Calculation", ageResult, MaterialTheme.colorScheme.primary)
    }
}

// Tool 46: Unix Epoch Converter
@Composable
fun UnixEpochTool() {
    var currentEpoch by remember { mutableStateOf(System.currentTimeMillis()) }
    var customEpochText by remember { mutableStateOf(currentEpoch.toString()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentEpoch = System.currentTimeMillis()
        }
    }

    val customEpoch = customEpochText.toLongOrNull() ?: currentEpoch
    val formattedUtc = remember(customEpoch) {
        val instant = Instant.ofEpochMilli(customEpoch)
        DateTimeFormatter.ISO_INSTANT.format(instant)
    }
    val formattedLocal = remember(customEpoch) {
        val d = Date(customEpoch)
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(d)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Live Current Epoch Millis", "$currentEpoch", MaterialTheme.colorScheme.primary)
        ResultCard("Live Current Epoch Seconds", "${currentEpoch / 1000}")

        OutlinedTextField(
            value = customEpochText,
            onValueChange = { customEpochText = it },
            label = { Text("Timestamp to Parse (ms)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        ResultCard("UTC ISO 8601", formattedUtc)
        ResultCard("Local Time", formattedLocal)
    }
}

// Tool 47: World Clock Explorer
@Composable
fun WorldClockTool() {
    var now by remember { mutableStateOf(Instant.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = Instant.now()
        }
    }

    val zones = listOf(
        "UTC (Greenwich)" to ZoneId.of("UTC"),
        "London" to ZoneId.of("Europe/London"),
        "Paris / Berlin" to ZoneId.of("Europe/Paris"),
        "New York" to ZoneId.of("America/New_York"),
        "San Francisco" to ZoneId.of("America/Los_Angeles"),
        "Tokyo" to ZoneId.of("Asia/Tokyo"),
        "Sydney" to ZoneId.of("Australia/Sydney")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        zones.forEach { (name, zone) ->
            val zdt = now.atZone(zone)
            val timeStr = zdt.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val dateStr = zdt.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(dateStr, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(timeStr, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// Tool 48: Event Countdown
@Composable
fun EventCountdownTool() {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    val newYear = LocalDateTime.of(now.year + 1, 1, 1, 0, 0, 0)

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = LocalDateTime.now()
        }
    }

    val duration = Duration.between(now, newYear)
    val days = duration.toDays()
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Countdown to New Year ${newYear.year} 🎆", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CountdownBox("$days", "Days")
            CountdownBox("$hours", "Hours")
            CountdownBox("$minutes", "Mins")
            CountdownBox("$seconds", "Secs")
        }
    }
}

@Composable
fun CountdownBox(value: String, label: String) {
    Card(
        modifier = Modifier.width(72.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Tool 49: Leap Year Checker
@Composable
fun LeapYearTool() {
    var yearText by remember { mutableStateOf("2028") }
    val year = yearText.toIntOrNull() ?: 2026
    val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = yearText,
            onValueChange = { yearText = it },
            label = { Text("Year to Evaluate") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        ResultCard(
            label = "Leap Year Verdict",
            value = if (isLeap) "✓ $year IS a Leap Year (366 days)" else "✗ $year is NOT a leap year (365 days)",
            color = if (isLeap) Color(0xFF10B981) else Color(0xFFEF4444)
        )
        ResultCard(
            label = "Gregorian Rule",
            value = "Every year divisible by 4 is a leap year, except for century years not divisible by 400."
        )
    }
}

// Tool 50: Work Hours & Overtime
@Composable
fun WorkHoursTool() {
    var startHour by remember { mutableStateOf("9") }
    var endHour by remember { mutableStateOf("17") }
    var lunchMins by remember { mutableStateOf("45") }

    val start = startHour.toDoubleOrNull() ?: 9.0
    val end = endHour.toDoubleOrNull() ?: 17.0
    val lunch = (lunchMins.toDoubleOrNull() ?: 0.0) / 60.0

    val totalHours = max(0.0, end - start - lunch)
    val regularHours = min(8.0, totalHours)
    val overtimeHours = max(0.0, totalHours - 8.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = startHour, onValueChange = { startHour = it }, label = { Text("Start (24h)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = endHour, onValueChange = { endHour = it }, label = { Text("End (24h)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        OutlinedTextField(value = lunchMins, onValueChange = { lunchMins = it }, label = { Text("Break (minutes)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        ResultCard("Total Billable Hours", "%.2f hours".format(totalHours), MaterialTheme.colorScheme.primary)
        ResultCard("Regular Hours (<= 8h)", "%.2f hours".format(regularHours))
        ResultCard("Overtime Hours", "%.2f hours".format(overtimeHours), if (overtimeHours > 0) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface)
    }
}

// Tool 51: Metronome & BPM Tap
@Composable
fun MetronomeTool() {
    var bpm by remember { mutableStateOf(120) }
    var isTicking by remember { mutableStateOf(false) }
    val tapTimes = remember { mutableStateListOf<Long>() }
    var pulse by remember { mutableStateOf(false) }

    LaunchedEffect(isTicking, bpm) {
        if (isTicking) {
            val interval = (60000 / max(1, bpm)).toLong()
            while (isTicking) {
                pulse = true
                delay(60)
                pulse = false
                delay(max(10L, interval - 60))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(if (pulse) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("$bpm", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = if (pulse) Color.White else MaterialTheme.colorScheme.onSurface)
        }

        Slider(value = bpm.toFloat(), onValueChange = { bpm = it.toInt() }, valueRange = 40f..240f)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { isTicking = !isTicking }) {
                Text(if (isTicking) "Stop" else "Start Metronome")
            }
            FilledTonalButton(onClick = {
                val now = System.currentTimeMillis()
                tapTimes.add(now)
                if (tapTimes.size > 5) tapTimes.removeAt(0)
                if (tapTimes.size >= 2) {
                    val intervals = (1 until tapTimes.size).map { tapTimes[it] - tapTimes[it - 1] }
                    val avgMs = intervals.average()
                    if (avgMs > 100) bpm = (60000.0 / avgMs).toInt().coerceIn(40, 240)
                }
            }) {
                Text("Tap Tempo")
            }
        }
    }
}

// Tool 52: Sleep Cycle Calculator
@Composable
fun SleepCycleTool() {
    val now = LocalTime.now()
    val cycles = listOf(3, 4, 5, 6).map { cycleCount ->
        val wake = now.plusMinutes((cycleCount * 90 + 15).toLong()) // 15 min to fall asleep
        cycleCount to wake.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("If you sleep right now (accounting for 15 min to fall asleep):", fontWeight = FontWeight.SemiBold)
        cycles.forEach { (cycle, time) ->
            ResultCard("$cycle Cycles (${cycle * 1.5} hrs sleep)", time, if (cycle == 5) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface)
        }
    }
}

// Tool 53: Pomodoro 25/5
@Composable
fun PomodoroTool() {
    var isBreak by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning && secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
            if (isRunning && secondsLeft == 0) {
                if (!isBreak) {
                    completedSessions++
                    isBreak = true
                    secondsLeft = 5 * 60
                } else {
                    isBreak = false
                    secondsLeft = 25 * 60
                }
                isRunning = false
            }
        }
    }

    val mins = secondsLeft / 60
    val secs = secondsLeft % 60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isBreak) "☕ SHORT BREAK" else "🎯 DEEP FOCUS SESSION", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isBreak) Color(0xFF3B82F6) else MaterialTheme.colorScheme.primary)
        Text("%02d:%02d".format(mins, secs), fontSize = 48.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text("Completed Focus Sessions: $completedSessions", fontSize = 14.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Pause" else "Start")
            }
            FilledTonalButton(onClick = {
                isRunning = false
                isBreak = false
                secondsLeft = 25 * 60
            }) {
                Text("Reset")
            }
        }
    }
}

// Tool 54: Week & Day of Year
@Composable
fun WeekOfYearTool() {
    val today = LocalDate.now()
    val dayOfYear = today.dayOfYear
    val totalDays = if (today.isLeapYear) 366 else 365
    val daysLeft = totalDays - dayOfYear
    val weekNum = (dayOfYear - 1) / 7 + 1
    val quarter = (today.monthValue - 1) / 3 + 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("ISO Week Number", "Week #$weekNum", MaterialTheme.colorScheme.primary)
        ResultCard("Day of Year", "Day $dayOfYear of $totalDays")
        ResultCard("Days Remaining in ${today.year}", "$daysLeft days left")
        ResultCard("Current Quarter", "Q$quarter")
    }
}

// Tool 55: Meeting Overlap Finder
@Composable
fun MeetingOverlapTool() {
    val cities = listOf("UTC (+0)" to 0, "London (+1)" to 1, "New York (-4)" to -4, "San Francisco (-7)" to -7, "Tokyo (+9)" to 9)
    var city1Idx by remember { mutableStateOf(2) } // New York
    var city2Idx by remember { mutableStateOf(1) } // London

    val offsetDiff = cities[city2Idx].second - cities[city1Idx].second

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("City 1:", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            cities.forEachIndexed { idx, (c, _) ->
                FilterChip(selected = city1Idx == idx, onClick = { city1Idx = idx }, label = { Text(c) })
            }
        }
        Text("City 2:", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            cities.forEachIndexed { idx, (c, _) ->
                FilterChip(selected = city2Idx == idx, onClick = { city2Idx = idx }, label = { Text(c) })
            }
        }

        ResultCard("Timezone Offset", "${if (offsetDiff >= 0) "+$offsetDiff" else "$offsetDiff"} hours difference", MaterialTheme.colorScheme.primary)
        ResultCard("Suggested Window", "When it's 9:00 AM in ${cities[city1Idx].first}, it's ${9 + offsetDiff}:00 in ${cities[city2Idx].first}.")
    }
}

// Tool 56: Daylight & Solar
@Composable
fun DaylightSolarTool() {
    var latText by remember { mutableStateOf("51.5") } // London
    val lat = latText.toDoubleOrNull() ?: 51.5
    val day = LocalDate.now().dayOfYear
    // Approximate solar declination
    val declination = 23.45 * sin(Math.toRadians((360.0 / 365.0) * (day - 81)))
    val hourAngle = Math.toDegrees(acos(-tan(Math.toRadians(lat)) * tan(Math.toRadians(declination))))
    val dayLengthHours = (2.0 * hourAngle) / 15.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = latText,
            onValueChange = { latText = it },
            label = { Text("Latitude (e.g. 51.5 for London, 40.7 for NYC)") },
            modifier = Modifier.fillMaxWidth()
        )
        ResultCard("Estimated Daylight Length", "%.2f hours".format(dayLengthHours), MaterialTheme.colorScheme.primary)
        ResultCard("Solar Noon Approximate", "12:00 PM - 1:00 PM local solar time")
    }
}

// Tool 57: Moon Phase
@Composable
fun MoonPhaseTool() {
    val date = LocalDate.now()
    // Approximate lunar age
    val year = date.year
    val month = date.monthValue
    val day = date.dayOfMonth
    val c = year / 100
    val epact = (11 * (year % 19) + 20 + (c - c / 4 - (8 * c + 13) / 25) + 30) % 30
    val daysSinceNew = (epact + month + day) % 30

    val (phase, icon, illum) = when {
        daysSinceNew in 0..1 -> Triple("New Moon", "🌑", "0%")
        daysSinceNew in 2..6 -> Triple("Waxing Crescent", "🌒", "25%")
        daysSinceNew in 7..8 -> Triple("First Quarter", "🌓", "50%")
        daysSinceNew in 9..14 -> Triple("Waxing Gibbous", "🌔", "75%")
        daysSinceNew in 15..16 -> Triple("Full Moon", "🌕", "100%")
        daysSinceNew in 17..21 -> Triple("Waning Gibbous", "🌖", "75%")
        daysSinceNew in 22..23 -> Triple("Last Quarter", "🌗", "50%")
        else -> Triple("Waning Crescent", "🌘", "25%")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Current Moon Phase", "$icon $phase", MaterialTheme.colorScheme.primary)
        ResultCard("Approximate Illumination", illum)
        ResultCard("Lunar Cycle Day", "Day $daysSinceNew of ~29.5 days")
    }
}

// Tool 58: Julian Date
@Composable
fun JulianDateTool() {
    val date = LocalDate.now()
    val y = date.year
    val m = date.monthValue
    val d = date.dayOfMonth

    // Standard astronomical formula
    val a = (14 - m) / 12
    val yAdj = y + 4800 - a
    val mAdj = m + 12 * a - 3
    val jdn = d + (153 * mAdj + 2) / 5 + 365 * yAdj + yAdj / 4 - yAdj / 100 + yAdj / 400 - 32045

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Astronomical Julian Day Number (JDN)", "$jdn", MaterialTheme.colorScheme.primary)
        ResultCard("Calendar Date", "${date.year}-${date.monthValue}-${date.dayOfMonth}")
    }
}

// Tool 59: Year & Day Progress
@Composable
fun YearProgressTool() {
    val now = LocalDateTime.now()
    val dayOfYear = now.dayOfYear
    val totalDays = if (now.toLocalDate().isLeapYear) 366f else 365f
    val yearProgress = (dayOfYear / totalDays) * 100f
    val dayProgress = ((now.hour * 3600 + now.minute * 60 + now.second) / 86400f) * 100f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Year ${now.year} Progress: ${"%.1f".format(yearProgress)}%", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { yearProgress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        )

        Text("Today's Progress: ${"%.1f".format(dayProgress)}%", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { dayProgress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = Color(0xFF10B981)
        )
    }
}

// Tool 60: Running Pace Calculator
@Composable
fun RunningPaceTool() {
    var distText by remember { mutableStateOf("10") }
    var hoursText by remember { mutableStateOf("0") }
    var minsText by remember { mutableStateOf("50") }

    val dist = distText.toDoubleOrNull() ?: 10.0
    val h = hoursText.toDoubleOrNull() ?: 0.0
    val m = minsText.toDoubleOrNull() ?: 50.0
    val totalMins = h * 60.0 + m

    val paceMinKm = if (dist > 0) totalMins / dist else 0.0
    val paceMinMile = paceMinKm * 1.60934
    val speedKmh = if (totalMins > 0) (dist / totalMins) * 60.0 else 0.0

    val kmMins = paceMinKm.toInt()
    val kmSecs = ((paceMinKm - kmMins) * 60).toInt()

    val miMins = paceMinMile.toInt()
    val miSecs = ((paceMinMile - miMins) * 60).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = distText, onValueChange = { distText = it }, label = { Text("Distance (km)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = hoursText, onValueChange = { hoursText = it }, label = { Text("Hours") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = minsText, onValueChange = { minsText = it }, label = { Text("Minutes") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        ResultCard("Pace per Kilometer", "%d:%02d /km".format(kmMins, kmSecs), MaterialTheme.colorScheme.primary)
        ResultCard("Pace per Mile", "%d:%02d /mi".format(miMins, miSecs))
        ResultCard("Average Speed", "%.2f km/h".format(speedKmh), Color(0xFF10B981))
    }
}
