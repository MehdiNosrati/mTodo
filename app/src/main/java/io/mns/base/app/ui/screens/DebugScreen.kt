package io.mns.base.app.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.mns.base.app.data.Priority
import io.mns.base.app.notifications.AndroidReminderManager
import io.mns.base.app.notifications.ReminderReceiver
import kotlinx.coroutines.launch

private val DebugAmber = Color(0xFFF59E0B)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var testTitle by remember { mutableStateOf("Review PR & Submit Roadmap") }
    var testDescription by remember { mutableStateOf("Test reminder notification from mTodo Debug Dashboard") }
    var selectedPriority by remember { mutableStateOf(Priority.HIGH) }

    // Live permission states
    var hasPostNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val areNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val canScheduleExactAlarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager?.canScheduleExactAlarms() ?: false
    } else {
        true
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPostNotificationPermission = isGranted
        scope.launch {
            snackbarHostState.showSnackbar(
                if (isGranted) "Notification permission granted!" else "Notification permission denied."
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Debug & Diagnostics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DebugAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "DEBUG",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DebugAmber,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: System & Permission Diagnostics
            DebugCard(title = "Notification Diagnostics") {
                // Post Notifications Permission (Android 13+)
                DiagnosticRow(
                    icon = Icons.Default.Notifications,
                    label = "POST_NOTIFICATIONS",
                    status = if (hasPostNotificationPermission) "Granted" else "Missing",
                    isOk = hasPostNotificationPermission,
                    actionText = if (!hasPostNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) "Request" else null,
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // App-level Notifications Enabled
                DiagnosticRow(
                    icon = Icons.Default.CircleNotifications,
                    label = "App Notifications",
                    status = if (areNotificationsEnabled) "Enabled" else "Blocked in OS",
                    isOk = areNotificationsEnabled,
                    actionText = if (!areNotificationsEnabled) "Settings" else null,
                    onAction = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Exact Alarms (Android 12+)
                DiagnosticRow(
                    icon = Icons.Default.Alarm,
                    label = "Exact Alarms Permission",
                    status = if (canScheduleExactAlarms) "Granted" else "Restricted",
                    isOk = canScheduleExactAlarms,
                    actionText = if (!canScheduleExactAlarms && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) "Settings" else null,
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Channel Info
                DiagnosticRow(
                    icon = Icons.Default.Tune,
                    label = "Channel: ${AndroidReminderManager.CHANNEL_NAME}",
                    status = "Importance: HIGH",
                    isOk = true,
                    actionText = null,
                    onAction = null
                )
            }

            // Section 2: Send Test Reminder Notification
            DebugCard(title = "Test Reminder Notification") {
                OutlinedTextField(
                    value = testTitle,
                    onValueChange = { testTitle = it },
                    label = { Text("Notification Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = testDescription,
                    onValueChange = { testDescription = it },
                    label = { Text("Notification Description") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Priority Level",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Priority.entries.forEach { p ->
                        val isSelected = selectedPriority == p
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPriority = p },
                            label = { Text(p.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = p.color,
                                    modifier = Modifier.size(10.dp)
                                ) {}
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trigger Buttons
                Button(
                    onClick = {
                        val testId = "debug_${System.currentTimeMillis()}"
                        val intent = Intent(context, ReminderReceiver::class.java).apply {
                            action = AndroidReminderManager.ACTION_REMINDER
                            putExtra(AndroidReminderManager.EXTRA_TODO_ID, testId)
                            putExtra(AndroidReminderManager.EXTRA_TITLE, testTitle.ifBlank { "Test Task" })
                            putExtra(AndroidReminderManager.EXTRA_DESCRIPTION, testDescription.ifBlank { "Immediate test notification" })
                        }
                        context.sendBroadcast(intent)
                        scope.launch {
                            snackbarHostState.showSnackbar("Dispatched immediate test reminder notification!")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Test Reminder Now")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            scheduleTestAlarm(context, delaySeconds = 5, title = testTitle, description = testDescription)
                            scope.launch {
                                snackbarHostState.showSnackbar("Scheduled test notification in 5 seconds (lock or exit app to test)!")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("In 5 Seconds", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            scheduleTestAlarm(context, delaySeconds = 15, title = testTitle, description = testDescription)
                            scope.launch {
                                snackbarHostState.showSnackbar("Scheduled test notification in 15 seconds!")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("In 15 Seconds", fontSize = 12.sp)
                    }
                }
            }

            // Section 3: Notification Actions Info & Clear
            DebugCard(title = "Notification Interactive Actions") {
                Text(
                    text = "Delivered notifications include 3 interactive actions backed by ReminderReceiver:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                ActionExplanationRow("Done", "Marks task complete in database and dismisses notification.")
                ActionExplanationRow("+15m", "Snoozes the task reminder for 15 minutes via AlarmManager.")
                ActionExplanationRow("+1h", "Snoozes the task reminder for 1 hour via AlarmManager.")

                Spacer(modifier = Modifier.height(14.dp))

                FilledTonalButton(
                    onClick = {
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                        notificationManager?.cancelAll()
                        scope.launch {
                            snackbarHostState.showSnackbar("Cleared all active notifications.")
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All Notifications")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun scheduleTestAlarm(context: Context, delaySeconds: Int, title: String, description: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val testId = "debug_${System.currentTimeMillis()}"
    val triggerTime = System.currentTimeMillis() + delaySeconds * 1000L

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        action = AndroidReminderManager.ACTION_REMINDER
        putExtra(AndroidReminderManager.EXTRA_TODO_ID, testId)
        putExtra(AndroidReminderManager.EXTRA_TITLE, title.ifBlank { "Test Task" })
        putExtra(AndroidReminderManager.EXTRA_DESCRIPTION, description.ifBlank { "Scheduled after $delaySeconds seconds" })
    }

    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    val pendingIntent = PendingIntent.getBroadcast(context, testId.hashCode(), intent, flags)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    } else {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}

@Composable
private fun DebugCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
private fun DiagnosticRow(
    icon: ImageVector,
    label: String,
    status: String,
    isOk: Boolean,
    actionText: String?,
    onAction: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = if (isOk) SuccessGreen else ErrorRed
            )
        }
        if (actionText != null && onAction != null) {
            TextButton(
                onClick = onAction,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(actionText, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ActionExplanationRow(name: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
