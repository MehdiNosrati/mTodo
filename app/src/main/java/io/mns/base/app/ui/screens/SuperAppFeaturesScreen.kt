package io.mns.base.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SuperFeature(
    val id: Int,
    val title: String,
    val description: String,
    val pillar: FeaturePillar,
    val icon: ImageVector,
    val isLive: Boolean = true
)

enum class FeaturePillar(val label: String, val color: Color) {
    CORE("Core Engine", Color(0xFF6366F1)),
    FOCUS("Focus & Time", Color(0xFFEC4899)),
    INPUT("Voice & Input", Color(0xFF3B82F6)),
    SYSTEM("Android System", Color(0xFF10B981)),
    NOTIFICATIONS("Notifications", Color(0xFFF59E0B)),
    ANALYTICS("Analytics", Color(0xFF8B5CF6)),
    PRIVACY("Privacy & Security", Color(0xFF14B8A6)),
    UI_UX("UI/UX Design", Color(0xFFF43F5E)),
    DIAGNOSTICS("Diagnostics", Color(0xFFEAB308)),
    PERFORMANCE("Store Readiness", Color(0xFF06B6D4))
}

val ALL_100_SUPER_FEATURES: List<SuperFeature> = listOf(
    // Pillar 1: Core Engine
    SuperFeature(1, "Offline-First SQLite Architecture", "Fully local Room database engine requiring zero network connectivity.", FeaturePillar.CORE, Icons.Default.Storage),
    SuperFeature(2, "Subtasks & Checklists Engine", "Break down tasks into granular checklist steps with automatic progress calculation.", FeaturePillar.CORE, Icons.Default.Checklist),
    SuperFeature(3, "Smart Recurrence Calculator", "Computes next due timestamps for daily, weekday, weekly, and monthly recurrence.", FeaturePillar.CORE, Icons.Default.Repeat),
    SuperFeature(4, "Pinned Tasks Priority Stacking", "Stick crucial tasks to the top of list views regardless of current sort.", FeaturePillar.CORE, Icons.Default.PushPin),
    SuperFeature(5, "Four-Tier Priority Matrix", "Visual classification: High (Red), Medium (Amber), Low (Sky), None (Slate).", FeaturePillar.CORE, Icons.Default.Flag),
    SuperFeature(6, "Dynamic Multi-Category Taxonomy", "Organize tasks by General, Work, Personal, Shopping, Health, and custom namespaces.", FeaturePillar.CORE, Icons.Default.Folder),
    SuperFeature(7, "Freeform Color-Coded Tags", "Label and cross-reference tasks with instant hashtag classification.", FeaturePillar.CORE, Icons.Default.Label),
    SuperFeature(8, "Full-Text Instant Search", "Live reactive querying indexing titles, notes, categories, and tags simultaneously.", FeaturePillar.CORE, Icons.Default.Search),
    SuperFeature(9, "Recycle Bin Soft Deletion", "Two-stage deletion with safe item recovery before permanent purging.", FeaturePillar.CORE, Icons.Default.DeleteOutline),
    SuperFeature(10, "Atomic Batch Task Operations", "Select multiple tasks for one-tap batch completion, restoration, or shredding.", FeaturePillar.CORE, Icons.Default.DoneAll),

    // Pillar 2: Focus & Time
    SuperFeature(11, "Integrated Pomodoro Timer", "Built-in 25-minute focus session manager with break intervals and audio alerts.", FeaturePillar.FOCUS, Icons.Default.Timer),
    SuperFeature(12, "Exact Due Date & Time Picker", "Material 3 modal date and time selection with epoch precision.", FeaturePillar.FOCUS, Icons.Default.Event),
    SuperFeature(13, "Time-Segmented Organization", "Auto-sorts into Today, Tomorrow, Upcoming, and Overdue chronological buckets.", FeaturePillar.FOCUS, Icons.Default.Schedule),
    SuperFeature(14, "Natural Date Range Formatter", "Humanized formatting: 'Today 3:00 PM', 'Tomorrow', 'Overdue · Yesterday'.", FeaturePillar.FOCUS, Icons.Default.CalendarToday),
    SuperFeature(15, "Auto-Overdue Threat Badging", "High-visibility warning indicators for tasks past their deadline.", FeaturePillar.FOCUS, Icons.Default.Warning),
    SuperFeature(16, "Focus Session Completion Log", "Track successful pomodoro cycles directly tied to task milestones.", FeaturePillar.FOCUS, Icons.Default.SportsScore),
    SuperFeature(17, "Task Duration Estimator", "Plan realistic working blocks before starting task execution.", FeaturePillar.FOCUS, Icons.Default.HourglassBottom),
    SuperFeature(18, "Quick Reschedule Shortcuts", "One-tap deferral to tomorrow or next week without opening date picker.", FeaturePillar.FOCUS, Icons.Default.FastForward),
    SuperFeature(19, "Daily Completion Goal Engine", "Custom targets (1 to 20 tasks/day) with real-time visual progress trackers.", FeaturePillar.FOCUS, Icons.Default.TrackChanges),
    SuperFeature(20, "Distraction-Free Read Mode", "Clean, uneditable inspection view for completed and archived items.", FeaturePillar.FOCUS, Icons.Default.MenuBook),

    // Pillar 3: Voice & Input
    SuperFeature(21, "Integrated Voice Dictation", "Native speech-to-text integration for effortless hands-free task creation.", FeaturePillar.INPUT, Icons.Default.Mic),
    SuperFeature(22, "Inline Quick-Add Bar", "Fast capture drawer at the bottom of the home screen without page navigation.", FeaturePillar.INPUT, Icons.Default.AddCircleOutline),
    SuperFeature(23, "Full-Screen Detail Composer", "Expansive canvas for comprehensive task briefs, notes, and deadlines.", FeaturePillar.INPUT, Icons.Default.EditNote),
    SuperFeature(24, "Keyboard IME Next Advancement", "Seamless soft keyboard focus management jumping directly from title to notes.", FeaturePillar.INPUT, Icons.Default.Keyboard),
    SuperFeature(25, "Draft Title Auto-Transfer", "Seamlessly carries quick-add input into full composer without losing text.", FeaturePillar.INPUT, Icons.Default.Input),
    SuperFeature(26, "Haptic Quick Checkoff", "Satisfying instant tactile check feedback when ticking tasks.", FeaturePillar.INPUT, Icons.Default.Check),
    SuperFeature(27, "Long-Press Multi-Select Mode", "Intuitive long-press gesture initiates batch selection toolbar.", FeaturePillar.INPUT, Icons.Default.TouchApp),
    SuperFeature(28, "Swipe Action Gestures", "Flick items left or right for quick complete or delete workflows.", FeaturePillar.INPUT, Icons.Default.Swipe),
    SuperFeature(29, "Suggested Tags Auto-Chips", "Context-aware quick tags: Work, Urgent, Health, Study with single tap.", FeaturePillar.INPUT, Icons.Default.SmartButton),
    SuperFeature(30, "Category Filter Carousel", "Horizontal scrolling chips for instant category switching.", FeaturePillar.INPUT, Icons.Default.ViewCarousel),

    // Pillar 4: Android System
    SuperFeature(31, "Native Home Screen AppWidget", "RemoteViews home screen widget rendering active tasks and deadlines.", FeaturePillar.SYSTEM, Icons.Default.Widgets),
    SuperFeature(32, "AppWidget 1-Tap Checkoff", "Complete tasks directly from your Android launcher without opening the app.", FeaturePillar.SYSTEM, Icons.Default.CheckCircle),
    SuperFeature(33, "AppWidget Quick-Add Deep Link", "Tap widget '+' button to instantly launch task capture screen.", FeaturePillar.SYSTEM, Icons.Default.AddBox),
    SuperFeature(34, "AlarmManager Exact Scheduling", "RTC_WAKEUP alarms ensure reminder fires precisely even in battery saver.", FeaturePillar.SYSTEM, Icons.Default.Alarm),
    SuperFeature(35, "Doze Mode Battery Exemption", "Uses setExactAndAllowWhileIdle to punch through Android Doze mode.", FeaturePillar.SYSTEM, Icons.Default.BatteryChargingFull),
    SuperFeature(36, "Boot Completed Rescheduler", "Automatically reconstitutes all scheduled alarms following device reboot.", FeaturePillar.SYSTEM, Icons.Default.RestartAlt),
    SuperFeature(37, "Predictive Back Navigation", "Smooth Compose back handler adhering to Android 14+ gesture ergonomics.", FeaturePillar.SYSTEM, Icons.Default.ArrowBack),
    SuperFeature(38, "Edge-to-Edge System Insets", "Draws behind status bar and navigation bar with dynamic inset calculation.", FeaturePillar.SYSTEM, Icons.Default.Fullscreen),
    SuperFeature(39, "Adaptive Launcher Icon", "Themed and dynamic vector launcher icon matching system wallpaper.", FeaturePillar.SYSTEM, Icons.Default.Android),
    SuperFeature(40, "Direct OS App Settings Jump", "One-tap direct navigation into system app notification preferences.", FeaturePillar.SYSTEM, Icons.Default.SettingsApplications),

    // Pillar 5: Notification Intelligence
    SuperFeature(41, "High-Importance Reminder Channel", "Channel configured with IMPORTANCE_HIGH for sound, heads-up, and vibration.", FeaturePillar.NOTIFICATIONS, Icons.Default.NotificationImportant),
    SuperFeature(42, "Heads-Up Banner Alerts", "Prominent floating banners over other apps when tasks become due.", FeaturePillar.NOTIFICATIONS, Icons.Default.NotificationsActive),
    SuperFeature(43, "Notification 'Done' Action Button", "Mark task complete right inside the notification tray.", FeaturePillar.NOTIFICATIONS, Icons.Default.CheckCircleOutline),
    SuperFeature(44, "Notification '+15m Snooze' Action", "Reschedules alarm 15 minutes ahead with a single notification button press.", FeaturePillar.NOTIFICATIONS, Icons.Default.Snooze),
    SuperFeature(45, "Notification '+1h Snooze' Action", "Reschedules alarm 1 hour ahead directly from notification tray.", FeaturePillar.NOTIFICATIONS, Icons.Default.HourglassTop),
    SuperFeature(46, "Notification Deep Linking", "Tap notification body to open MainActivity directly into the target task.", FeaturePillar.NOTIFICATIONS, Icons.Default.OpenInNew),
    SuperFeature(47, "Auto-Dismiss on Task Done", "Dismisses active notification immediately when task is checked off anywhere.", FeaturePillar.NOTIFICATIONS, Icons.Default.Clear),
    SuperFeature(48, "Bulk Notification Cleaner", "One-tap tool to purge all app-issued alerts from status bar.", FeaturePillar.NOTIFICATIONS, Icons.Default.ClearAll),
    SuperFeature(49, "Android 13+ Permission Flow", "Graceful runtime POST_NOTIFICATIONS permission prompt and handling.", FeaturePillar.NOTIFICATIONS, Icons.Default.Security),
    SuperFeature(50, "Exact Alarm Permission Probe", "Verifies SCHEDULE_EXACT_ALARM capability on Android 12+ devices.", FeaturePillar.NOTIFICATIONS, Icons.Default.VerifiedUser),

    // Pillar 6: Analytics & Gamification
    SuperFeature(51, "Daily Habit Streak Tracker", "Calculates unbroken consecutive days of task completion.", FeaturePillar.ANALYTICS, Icons.Default.LocalFireDepartment),
    SuperFeature(52, "Weekly Completion Velocity", "Measures output velocity comparing current 7-day volume to prior periods.", FeaturePillar.ANALYTICS, Icons.Default.TrendingUp),
    SuperFeature(53, "Daily Goal Progress Ring", "Visual circular progress bar tracking goal accomplishment in real time.", FeaturePillar.ANALYTICS, Icons.Default.DonutLarge),
    SuperFeature(54, "Priority Distribution Chart", "Analytics breakdowns highlighting workload volume by urgency.", FeaturePillar.ANALYTICS, Icons.Default.PieChart),
    SuperFeature(55, "Category Volume Analytics", "Identifies which life areas (Work, Personal, Health) command the most focus.", FeaturePillar.ANALYTICS, Icons.Default.BarChart),
    SuperFeature(56, "Completed Tasks History", "Searchable done archive keeping a permanent record of past successes.", FeaturePillar.ANALYTICS, Icons.Default.History),
    SuperFeature(57, "Productivity Index Score", "Formulaic computation assessing completion rate against deadlines.", FeaturePillar.ANALYTICS, Icons.Default.Speed),
    SuperFeature(58, "Zero-Task Inbox Zero Badge", "Delightful empty state artwork celebrating an empty backlog.", FeaturePillar.ANALYTICS, Icons.Default.SentimentSatisfiedAlt),
    SuperFeature(59, "Dynamic Motivation Quotes", "Encouraging contextual micro-copy upon completing daily goals.", FeaturePillar.ANALYTICS, Icons.Default.FormatQuote),
    SuperFeature(60, "Weekly Summary Insights", "Consolidated insights view highlighting productivity peaks.", FeaturePillar.ANALYTICS, Icons.Default.Insights),

    // Pillar 7: Privacy & Security
    SuperFeature(61, "100% Zero-Cloud Storage", "All tasks, notes, and metadata remain strictly on-device in local storage.", FeaturePillar.PRIVACY, Icons.Default.Lock),
    SuperFeature(62, "Zero Third-Party SDK Trackers", "No Firebase, no AdMob, no analytics SDKs, zero telemetry.", FeaturePillar.PRIVACY, Icons.Default.Shield),
    SuperFeature(63, "Full JSON Backup Export", "Export complete database snapshot to an open, human-readable JSON format.", FeaturePillar.PRIVACY, Icons.Default.CloudUpload),
    SuperFeature(64, "SAF Document Sharing", "Uses Android Storage Access Framework to export to Google Drive, SD, or Files.", FeaturePillar.PRIVACY, Icons.Default.Share),
    SuperFeature(65, "Lossless JSON Backup Restore", "Import backup archives safely merging or restoring task collections.", FeaturePillar.PRIVACY, Icons.Default.CloudDownload),
    SuperFeature(66, "Recycle Bin Sandbox", "Deleted tasks stay segregated from active feeds until permanent purge.", FeaturePillar.PRIVACY, Icons.Default.Archive),
    SuperFeature(67, "Permanent File Shredder", "Hard deletion eliminates SQLite records permanently with zero trace.", FeaturePillar.PRIVACY, Icons.Default.DeleteForever),
    SuperFeature(68, "Room Schema Hash Safety", "Deterministic schema hashing protects database against data corruption.", FeaturePillar.PRIVACY, Icons.Default.DataArray),
    SuperFeature(69, "Offline Privacy Guarantee", "Never requires internet permission in AndroidManifest.xml.", FeaturePillar.PRIVACY, Icons.Default.WifiOff),
    SuperFeature(70, "Encrypted Local File Sandbox", "App sandbox enforces private read/write permissions per Android OS rules.", FeaturePillar.PRIVACY, Icons.Default.FolderSpecial),

    // Pillar 8: UI/UX Craftsmanship
    SuperFeature(71, "OLED Pure Dark Mode", "Deep midnight blue (#0F172A) palette designed for battery savings and eyes.", FeaturePillar.UI_UX, Icons.Default.DarkMode),
    SuperFeature(72, "Clean Daytime Light Mode", "High-contrast airy light theme with crisp typography.", FeaturePillar.UI_UX, Icons.Default.LightMode),
    SuperFeature(73, "Indigo & Violet Gradient Identity", "Signature linear gradient accents defining navigation and primary actions.", FeaturePillar.UI_UX, Icons.Default.Palette),
    SuperFeature(74, "Spring Physics Animations", "Natural bouncy transitions powered by Compose spring specs.", FeaturePillar.UI_UX, Icons.Default.Animation),
    SuperFeature(75, "Staggered Section Entrances", "Cascading appearance animations giving screens polished fluid entrance.", FeaturePillar.UI_UX, Icons.Default.AutoAwesomeMotion),
    SuperFeature(76, "Curved Navigation Bar", "Modern floating dock style bottom navigation with rounded corners.", FeaturePillar.UI_UX, Icons.Default.Navigation),
    SuperFeature(77, "Roborazzi Visual Regression Suite", "18 automated visual golden tests guarding against UI layout breakage.", FeaturePillar.UI_UX, Icons.Default.CameraAlt),
    SuperFeature(78, "Adaptive Screen Ergonomics", "Scales seamlessly from compact smartphones to foldables and tablets.", FeaturePillar.UI_UX, Icons.Default.Devices),
    SuperFeature(79, "Predictive Horizontal Transitions", "Smooth tab sliding animations preserving navigational spatial mental model.", FeaturePillar.UI_UX, Icons.Default.Transform),
    SuperFeature(80, "Color-Coded Priority Pills", "Immediate visual color cues throughout list views and detail screens.", FeaturePillar.UI_UX, Icons.Default.FiberManualRecord),

    // Pillar 9: Diagnostics Powerhouse
    SuperFeature(81, "Gated Debug Dashboard", "Dedicated diagnostic suite compiling exclusively into BuildConfig.DEBUG.", FeaturePillar.DIAGNOSTICS, Icons.Default.BugReport),
    SuperFeature(82, "Live POST_NOTIFICATIONS Probe", "Real-time query of Android 13 notification permission state.", FeaturePillar.DIAGNOSTICS, Icons.Default.ChecklistRtl),
    SuperFeature(83, "OS Notification Toggle Probe", "Detects if notifications were muted at the operating system level.", FeaturePillar.DIAGNOSTICS, Icons.Default.NotificationsOff),
    SuperFeature(84, "Exact Alarm Permission Probe", "Verifies whether app is allowed to schedule exact alarms.", FeaturePillar.DIAGNOSTICS, Icons.Default.AccessTimeFilled),
    SuperFeature(85, "Immediate Test Reminder Trigger", "Fires live heads-up reminder notification instantaneously.", FeaturePillar.DIAGNOSTICS, Icons.Default.Send),
    SuperFeature(86, "5-Second Delayed Alarm Test", "Validates background wakeups when device is locked or app is closed.", FeaturePillar.DIAGNOSTICS, Icons.Default.Timer),
    SuperFeature(87, "15-Second Delayed Alarm Test", "Validates longer interval alarm scheduling and wake locks.", FeaturePillar.DIAGNOSTICS, Icons.Default.HourglassEmpty),
    SuperFeature(88, "Interactive Action Explanation", "In-app documentation of receiver callbacks and notification flows.", FeaturePillar.DIAGNOSTICS, Icons.Default.HelpOutline),
    SuperFeature(89, "System Notification Settings Link", "Direct intent launcher to the Android system settings panel for mTodo.", FeaturePillar.DIAGNOSTICS, Icons.Default.Launch),
    SuperFeature(90, "Active Notification Purge Probe", "Clears mock notifications from tray to reset test state.", FeaturePillar.DIAGNOSTICS, Icons.Default.CleaningServices),

    // Pillar 10: Store Readiness & Packaging
    SuperFeature(91, "Optimized Android App Bundle (.aab)", "Ready for Google Play Console upload with dynamic split APK delivery.", FeaturePillar.PERFORMANCE, Icons.Default.AppShortcut),
    SuperFeature(92, "R8 ProGuard Tree-Shaking", "Aggressive bytecode shrinking cutting binary size by over 50%.", FeaturePillar.PERFORMANCE, Icons.Default.Compress),
    SuperFeature(93, "Baseline Startup Profile", "Pre-compiled ART compilation profile delivering instantaneous app starts.", FeaturePillar.PERFORMANCE, Icons.Default.FlashOn),
    SuperFeature(94, "Native NDK Symbol Tables", "Symbol table preservation for accurate crash diagnostics and traces.", FeaturePillar.PERFORMANCE, Icons.Default.Terminal),
    SuperFeature(95, "Target SDK 36 (Android 15+)", "Engineered to target latest Android platform security & design APIs.", FeaturePillar.PERFORMANCE, Icons.Default.NewReleases),
    SuperFeature(96, "Strict Dependency Locking", "Reproducible builds with Gradle version catalog and pinned libraries.", FeaturePillar.PERFORMANCE, Icons.Default.LockClock),
    SuperFeature(97, "64 Automated Unit Tests", "Comprehensive JUnit suite covering repositories, converters, and view models.", FeaturePillar.PERFORMANCE, Icons.Default.FactCheck),
    SuperFeature(98, "Release Keystore Signing Pipeline", "Configured release signing config for automated Play Store signing.", FeaturePillar.PERFORMANCE, Icons.Default.Key),
    SuperFeature(99, "Koin Dependency Injection", "Clean, lightweight, reflection-free dependency injection container.", FeaturePillar.PERFORMANCE, Icons.Default.AccountTree),
    SuperFeature(100, "Kotlin Multiplatform (KMP) Core", "Shared business logic, database, and repository layer unifying iOS & Android.", FeaturePillar.PERFORMANCE, Icons.Default.AllInclusive)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAppFeaturesScreen(
    onBack: () -> Unit,
    onOpenToolbox: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPillar by remember { mutableStateOf<FeaturePillar?>(null) }

    val filteredFeatures = remember(searchQuery, selectedPillar) {
        ALL_100_SUPER_FEATURES.filter { feature ->
            val matchesPillar = selectedPillar == null || feature.pillar == selectedPillar
            val matchesSearch = searchQuery.isBlank() ||
                feature.title.contains(searchQuery, ignoreCase = true) ||
                feature.description.contains(searchQuery, ignoreCase = true) ||
                feature.pillar.label.contains(searchQuery, ignoreCase = true)
            matchesPillar && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Super App Capabilities",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF6366F1).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "100 FEATURES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6366F1),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (onOpenToolbox != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenToolbox),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "⚡ 100 Interactive Tools Suite",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "Calculators, converters, text & hardware tools",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            FilledTonalButton(
                                onClick = onOpenToolbox,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Launch", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 100 super features...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Category Pills Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedPillar == null,
                            onClick = { selectedPillar = null },
                            label = { Text("All (100)") }
                        )
                    }
                    items(FeaturePillar.entries) { pillar ->
                        val count = ALL_100_SUPER_FEATURES.count { it.pillar == pillar }
                        FilterChip(
                            selected = selectedPillar == pillar,
                            onClick = { selectedPillar = if (selectedPillar == pillar) null else pillar },
                            label = { Text("${pillar.label} ($count)") },
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = pillar.color,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                            }
                        )
                    }
                }
            }

            item {
                // Stats Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(value = "${filteredFeatures.size}", label = "Shown")
                        VerticalDivider(modifier = Modifier.height(28.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        StatItem(value = "10", label = "Pillars")
                        VerticalDivider(modifier = Modifier.height(28.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        StatItem(value = "100%", label = "Active")
                        VerticalDivider(modifier = Modifier.height(28.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        StatItem(value = "8.7 MB", label = "AAB Size")
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(filteredFeatures, key = { it.id }) { feature ->
                FeatureCard(feature)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureCard(feature: SuperFeature) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = feature.pillar.color.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = null,
                        tint = feature.pillar.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${feature.id} ${feature.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = feature.pillar.color.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = feature.pillar.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = feature.pillar.color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
