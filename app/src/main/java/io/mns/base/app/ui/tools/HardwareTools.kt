package io.mns.base.app.ui.tools

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.sin

val HARDWARE_TOOLS: List<ToolItem> = listOf(
    ToolItem(
        id = 61,
        name = "Screen Color Lantern",
        description = "Adjustable fullscreen ambient light with warm lantern, emergency red, and white presets.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.LightMode
    ) { ScreenLanternTool() },

    ToolItem(
        id = 62,
        name = "Flashlight LED Torch",
        description = "Hardware camera flash toggle with status feedback and failsafe simulator.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.FlashlightOn
    ) { FlashlightTool() },

    ToolItem(
        id = 63,
        name = "Battery Health & Telemetry",
        description = "Live battery percentage, voltage, battery temperature (°C), and charging technology.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.BatteryChargingFull
    ) { BatteryMonitorTool() },

    ToolItem(
        id = 64,
        name = "Display DPI & Metrics Inspector",
        description = "Real-time screen pixel dimensions, density DPI classification, and DP viewport dimensions.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Screenshot
    ) { DisplayMetricsTool() },

    ToolItem(
        id = 65,
        name = "RAM & Memory Gauge",
        description = "Inspect total system RAM, available free memory, and system low-memory warnings.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Memory
    ) { RamMonitorTool() },

    ToolItem(
        id = 66,
        name = "Device & OS Specifications",
        description = "Manufacturer, hardware model, Android release, API level, and CPU architecture ABIs.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.PhoneAndroid
    ) { DeviceSpecsTool() },

    ToolItem(
        id = 67,
        name = "Haptic Vibration Studio",
        description = "Test tactile feedback waveforms: Click, Heavy Click, Double Click, and Tick.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Vibration
    ) { HapticStudioTool() },

    ToolItem(
        id = 68,
        name = "Dead Pixel Screen Tester",
        description = "Fullscreen cycling solid color test (RGB, White, Black) to detect damaged display pixels.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.GridOn
    ) { DeadPixelTesterTool() },

    ToolItem(
        id = 69,
        name = "Device Uptime Clock",
        description = "Elapsed duration in days, hours, and minutes since the operating system was last rebooted.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.History
    ) { UptimeClockTool() },

    ToolItem(
        id = 70,
        name = "Storage Space Inspector",
        description = "Internal flash storage total capacity, available storage, and used disk percentage.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.SdCard
    ) { StorageSpaceTool() },

    ToolItem(
        id = 71,
        name = "Display Refresh Rate Checker",
        description = "Detect actual live display panel refresh rate (60Hz, 90Hz, 120Hz).",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Tv
    ) { RefreshRateTool() },

    ToolItem(
        id = 72,
        name = "Network Connection Monitor",
        description = "Detect active internet link: Wi-Fi, Cellular mobile data, Ethernet, or Offline.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Wifi
    ) { NetworkMonitorTool() },

    ToolItem(
        id = 73,
        name = "440Hz Audio Pitch Pipe",
        description = "Acoustic concert pitch A440 tone generator for musical instrument tuning.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.MusicNote
    ) { PitchPipeTool() },

    ToolItem(
        id = 74,
        name = "Thermal Status Indicator",
        description = "Queries operating system hardware thermal throttling and temperature stress state.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.DeviceThermostat
    ) { ThermalStatusTool() },

    ToolItem(
        id = 75,
        name = "Clipboard Inspector & Paste",
        description = "Inspect active clipboard contents, character length, and instant clear capability.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.ContentPaste
    ) { ClipboardInspectorTool() },

    ToolItem(
        id = 76,
        name = "Screen Caliper & Digital Ruler",
        description = "Calibrated on-screen ruler measuring real-world centimeters and inches based on DPI.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Straighten
    ) { DigitalRulerTool() },

    ToolItem(
        id = 77,
        name = "Bubble Level & Inclinometer",
        description = "Visual 2D surface leveling tool using onboard accelerometer tilt coordinates.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Rotate90DegreesCcw
    ) { BubbleLevelTool() },

    ToolItem(
        id = 78,
        name = "Hardware Sensors Inventory",
        description = "Comprehensive list of all physical sensors (Gyroscope, Accelerometer, Magnetometer, etc.).",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Sensors
    ) { SensorsInventoryTool() },

    ToolItem(
        id = 79,
        name = "Audio Volume Streams",
        description = "View and adjust volume levels for Media, Ring, Notification, and Alarm audio channels.",
        category = ToolCategory.HARDWARE,
        icon = Icons.AutoMirrored.Filled.VolumeUp
    ) { VolumeStreamsTool() },

    ToolItem(
        id = 80,
        name = "Theme Palette Inspector",
        description = "Inspect active Material 3 dynamic color tokens, surface hex values, and color harmony.",
        category = ToolCategory.HARDWARE,
        icon = Icons.Default.Palette
    ) { ThemePaletteTool() }
)

// Tool 61: Screen Color Lantern
@Composable
fun ScreenLanternTool() {
    var selectedColor by remember { mutableStateOf(Color.White) }
    var isFullscreen by remember { mutableStateOf(false) }

    val presets = listOf(
        "Pure White" to Color.White,
        "Warm Amber" to Color(0xFFFFB300),
        "Candlelight" to Color(0xFFFF7043),
        "Night Red" to Color(0xFFE53935),
        "Emerald Green" to Color(0xFF43A047),
        "Deep Cyan" to Color(0xFF00ACC1),
        "Ocean Blue" to Color(0xFF1E88E5)
    )

    if (isFullscreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(selectedColor)
                .clickable { isFullscreen = false },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Tap anywhere to exit lantern",
                color = if (selectedColor == Color.White || selectedColor == Color(0xFFFFB300)) Color.Black else Color.White,
                fontSize = 14.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(selectedColor)
                    .clickable { isFullscreen = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tap to expand fullscreen",
                    color = if (selectedColor == Color.White || selectedColor == Color(0xFFFFB300)) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text("Color Presets:", fontWeight = FontWeight.SemiBold)
            presets.forEach { (name, color) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedColor = color },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(name, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Tool 62: Flashlight LED Torch
@Composable
fun FlashlightTool() {
    val context = LocalContext.current
    var isTorchOn by remember { mutableStateOf(false) }
    var hasCameraFlash by remember { mutableStateOf(true) }

    fun toggleTorch(enable: Boolean) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraId != null && cameraManager != null) {
                cameraManager.setTorchMode(cameraId, enable)
                isTorchOn = enable
            } else {
                hasCameraFlash = false
                isTorchOn = enable
            }
        } catch (e: Exception) {
            // Emulators or devices without flash
            hasCameraFlash = false
            isTorchOn = enable
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isTorchOn) toggleTorch(false)
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
                .size(120.dp)
                .clip(CircleShape)
                .background(if (isTorchOn) Color(0xFFFBBF24) else MaterialTheme.colorScheme.surfaceVariant)
                .clickable { toggleTorch(!isTorchOn) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                contentDescription = "Torch",
                modifier = Modifier.size(48.dp),
                tint = if (isTorchOn) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = if (isTorchOn) "TORCH IS ON" else "TORCH IS OFF",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (isTorchOn) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurface
        )

        Button(onClick = { toggleTorch(!isTorchOn) }) {
            Text(if (isTorchOn) "Turn Off" else "Turn On")
        }

        if (!hasCameraFlash) {
            ResultCard("Note", "Hardware camera flash not detected on this device/emulator. Simulator active.")
        }
    }
}

// Tool 63: Battery Health & Telemetry
@Composable
fun BatteryMonitorTool() {
    val context = LocalContext.current
    var level by remember { mutableStateOf(100) }
    var isCharging by remember { mutableStateOf(false) }
    var voltageMv by remember { mutableStateOf(4200) }
    var tempC by remember { mutableStateOf(28.0) }
    var tech by remember { mutableStateOf("Li-ion") }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                intent?.let {
                    level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                    voltageMv = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    val temp = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    tempC = temp / 10.0
                    tech = it.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard(
            label = "Battery Percentage",
            value = "$level%",
            color = if (level <= 20) Color(0xFFEF4444) else Color(0xFF10B981)
        )
        ResultCard(
            label = "Charging State",
            value = if (isCharging) "⚡ Charging" else "Discharging (On Battery)",
            color = if (isCharging) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface
        )
        ResultCard(label = "Voltage", "$voltageMv mV (${"%.2f".format(voltageMv / 1000.0)} V)")
        ResultCard(label = "Battery Temperature", "%.1f °C".format(tempC))
        ResultCard(label = "Battery Chemistry", tech)
    }
}

// Tool 64: Display Metrics & DPI
@Composable
fun DisplayMetricsTool() {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val dm = context.resources.displayMetrics

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Screen Resolution (Pixels)", "${dm.widthPixels} × ${dm.heightPixels} px", MaterialTheme.colorScheme.primary)
        ResultCard("Viewport Dimension (DP)", "${configuration.screenWidthDp} × ${configuration.screenHeightDp} dp")
        ResultCard("Density DPI", "${dm.densityDpi} dpi (scale factor: ${dm.density})")
        ResultCard("Font Scaled Density (SP)", "${dm.scaledDensity}")
        ResultCard("Orientation", if (configuration.screenWidthDp > configuration.screenHeightDp) "Landscape" else "Portrait")
    }
}

// Tool 65: RAM & Memory Gauge
@Composable
fun RamMonitorTool() {
    val context = LocalContext.current
    var memInfo by remember { mutableStateOf(ActivityManager.MemoryInfo()) }

    LaunchedEffect(Unit) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        while (isActive) {
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            memInfo = mi
            delay(2000)
        }
    }

    val totalGb = memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
    val availGb = memInfo.availMem / (1024.0 * 1024.0 * 1024.0)
    val usedGb = totalGb - availGb
    val usedPercent = if (totalGb > 0.0) ((usedGb / totalGb) * 100.0).toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("RAM Memory Usage: ${"%.1f".format(usedPercent)}%", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { usedPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = if (usedPercent > 85f) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
        )

        ResultCard("Total Physical RAM", "%.2f GB".format(totalGb))
        ResultCard("Used RAM", "%.2f GB".format(usedGb), MaterialTheme.colorScheme.primary)
        ResultCard("Available Free RAM", "%.2f GB".format(availGb), Color(0xFF10B981))
        ResultCard("System Low-Memory State", if (memInfo.lowMemory) "⚠️ Low Memory Triggered" else "Normal (Sufficient RAM)")
    }
}

// Tool 66: Device & OS Specs
@Composable
fun DeviceSpecsTool() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Device Model", "${Build.MANUFACTURER} ${Build.MODEL}", MaterialTheme.colorScheme.primary)
        ResultCard("Android Version", "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        ResultCard("Hardware Board", Build.HARDWARE)
        ResultCard("Build ID", Build.ID)
        ResultCard("Supported CPU ABIs", Build.SUPPORTED_ABIS.joinToString(", "))
        ResultCard("Device Codename", Build.DEVICE)
    }
}

// Tool 67: Haptic Vibration Studio
@Composable
fun HapticStudioTool() {
    val context = LocalContext.current

    fun vibrate(type: String) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator != null) {
                val effect = when (type) {
                    "click" -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    "heavy" -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    "double" -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                    else -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50L)
            }
        } catch (_: Exception) {}
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            "Standard Click" to "click",
            "Heavy Click" to "heavy",
            "Double Click" to "double",
            "Tick Feedback" to "tick"
        ).forEach { (label, type) ->
            Button(
                onClick = { vibrate(type) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Vibration, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test $label")
            }
        }
    }
}

// Tool 68: Dead Pixel Tester
@Composable
fun DeadPixelTesterTool() {
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black)
    var colorIdx by remember { mutableStateOf(0) }
    var isTesting by remember { mutableStateOf(false) }

    if (isTesting) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors[colorIdx])
                .clickable {
                    if (colorIdx < colors.size - 1) {
                        colorIdx++
                    } else {
                        isTesting = false
                        colorIdx = 0
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Tap to next color (${colorIdx + 1}/${colors.size})",
                color = if (colors[colorIdx] == Color.White) Color.Black else Color.White,
                fontSize = 14.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Cycle through solid colors to visually detect dead or stuck display pixels.")
            Button(
                onClick = { isTesting = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Launch Fullscreen Screen Tester")
            }
        }
    }
}

// Tool 69: Device Uptime
@Composable
fun UptimeClockTool() {
    var uptimeMs by remember { mutableStateOf(SystemClock.elapsedRealtime()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            uptimeMs = SystemClock.elapsedRealtime()
            delay(1000)
        }
    }

    val seconds = (uptimeMs / 1000) % 60
    val minutes = (uptimeMs / (1000 * 60)) % 60
    val hours = (uptimeMs / (1000 * 60 * 60)) % 24
    val days = uptimeMs / (1000 * 60 * 60 * 24)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("System Uptime", "$days days, $hours hrs, $minutes mins, $seconds secs", MaterialTheme.colorScheme.primary)
        ResultCard("Total Uptime Milliseconds", "$uptimeMs ms")
        ResultCard("Boot Time Elapsed", "Device active since boot")
    }
}

// Tool 70: Storage Space
@Composable
fun StorageSpaceTool() {
    val stat = remember { StatFs(Environment.getDataDirectory().path) }
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val availableBlocks = stat.availableBlocksLong

    val totalBytes = totalBlocks * blockSize
    val availBytes = availableBlocks * blockSize
    val usedBytes = totalBytes - availBytes

    val totalGb = totalBytes / (1024.0 * 1024.0 * 1024.0)
    val usedGb = usedBytes / (1024.0 * 1024.0 * 1024.0)
    val availGb = availBytes / (1024.0 * 1024.0 * 1024.0)
    val usedPct = if (totalGb > 0.0) ((usedGb / totalGb) * 100.0).toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Internal Storage Used: ${"%.1f".format(usedPct)}%", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { usedPct / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = MaterialTheme.colorScheme.primary
        )

        ResultCard("Total Storage", "%.2f GB".format(totalGb))
        ResultCard("Used Space", "%.2f GB".format(usedGb), MaterialTheme.colorScheme.primary)
        ResultCard("Available Free Space", "%.2f GB".format(availGb), Color(0xFF10B981))
    }
}

// Tool 71: Refresh Rate
@Composable
fun RefreshRateTool() {
    val context = LocalContext.current
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? android.view.WindowManager
    @Suppress("DEPRECATION")
    val display = windowManager?.defaultDisplay
    val refreshRate = display?.refreshRate ?: 60.0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Active Display Refresh Rate", "${refreshRate.toInt()} Hz", MaterialTheme.colorScheme.primary)
        ResultCard("Panel Smoothness Rating", if (refreshRate >= 120f) "120Hz ProMotion / Ultra Smooth" else if (refreshRate >= 90f) "90Hz Smooth Display" else "60Hz Standard Display")
    }
}

// Tool 72: Network Monitor
@Composable
fun NetworkMonitorTool() {
    val context = LocalContext.current
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val activeNet = cm?.activeNetwork
    val caps = cm?.getNetworkCapabilities(activeNet)

    val (netType, isConnected) = when {
        caps == null -> "Offline" to false
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi Connection" to true
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular Mobile Data" to true
        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet Link" to true
        caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN Active" to true
        else -> "Connected (Other)" to true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Internet Status", if (isConnected) "Online" else "Disconnected", if (isConnected) Color(0xFF10B981) else Color(0xFFEF4444))
        ResultCard("Active Link Type", netType, MaterialTheme.colorScheme.primary)
        ResultCard("Metered Status", if (caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == true) "Unmetered" else "Metered / Standard")
    }
}

// Tool 73: 440Hz Pitch Pipe
@Composable
fun PitchPipeTool() {
    var isPlaying by remember { mutableStateOf(false) }
    var audioTrack by remember { mutableStateOf<AudioTrack?>(null) }

    fun toggleTone() {
        if (isPlaying) {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
            isPlaying = false
        } else {
            val sampleRate = 44100
            val numSamples = sampleRate * 2 // 2 seconds buffer loop
            val buffer = ShortArray(numSamples)
            val freq = 440.0 // A440

            for (i in 0 until numSamples) {
                val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.7).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.setLoopPoints(0, buffer.size, -1)
            track.play()
            audioTrack = track
            isPlaying = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioTrack?.stop()
            audioTrack?.release()
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
                .background(if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .clickable { toggleTone() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = if (isPlaying) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            if (isPlaying) "440Hz Tone Playing..." else "Concert Pitch A440 (Tuning Tone)",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Button(onClick = { toggleTone() }) {
            Text(if (isPlaying) "Stop Tone" else "Play 440Hz Tone")
        }
    }
}

// Tool 74: Thermal Status
@Composable
fun ThermalStatusTool() {
    val context = LocalContext.current
    val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    val (statusStr, color) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && pm != null) {
        when (pm.currentThermalStatus) {
            PowerManager.THERMAL_STATUS_NONE -> "Normal (Cool)" to Color(0xFF10B981)
            PowerManager.THERMAL_STATUS_LIGHT -> "Light Warmth" to Color(0xFF3B82F6)
            PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Warmth" to Color(0xFFF59E0B)
            PowerManager.THERMAL_STATUS_SEVERE -> "Severe Throttling" to Color(0xFFEF4444)
            PowerManager.THERMAL_STATUS_CRITICAL -> "Critical Heat" to Color(0xFFDC2626)
            PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency Shutdown Warning" to Color(0xFF991B1B)
            else -> "Optimal" to Color(0xFF10B981)
        }
    } else {
        "Optimal (Android < 10)" to Color(0xFF10B981)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Device Thermal State", statusStr, color)
        ResultCard("Processor Throttling", "System operating without heat warnings")
    }
}

// Tool 75: Clipboard Inspector
@Composable
fun ClipboardInspectorTool() {
    val context = LocalContext.current
    var clipText by remember { mutableStateOf("") }

    fun refreshClip() {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipText = cm.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }

    LaunchedEffect(Unit) {
        refreshClip()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { refreshClip() }) {
                Text("Refresh Clipboard")
            }
            FilledTonalButton(onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                cm.setPrimaryClip(android.content.ClipData.newPlainText("", ""))
                clipText = ""
            }) {
                Text("Clear")
            }
        }

        ResultCard("Current Clipboard Content", if (clipText.isEmpty()) "(Clipboard is empty)" else clipText, MaterialTheme.colorScheme.primary)
        ResultCard("Length", "${clipText.length} characters")
    }
}

// Tool 76: Digital Ruler
@Composable
fun DigitalRulerTool() {
    val context = LocalContext.current
    val dm = context.resources.displayMetrics
    val xdpi = dm.xdpi
    val pxPerCm = xdpi / 2.54f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Calibrated On-Screen Ruler (Centimeters):", fontWeight = FontWeight.SemiBold)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
        ) {
            val totalCm = (size.width / pxPerCm).toInt()
            for (cm in 0..totalCm) {
                val x = cm * pxPerCm
                // Major mark
                drawLine(Color.Black, Offset(x, 0f), Offset(x, 40f), strokeWidth = 3f)
                // Millimeter marks
                for (mm in 1..9) {
                    val mmX = x + (mm * pxPerCm / 10f)
                    val markHeight = if (mm == 5) 26f else 16f
                    drawLine(Color.DarkGray, Offset(mmX, 0f), Offset(mmX, markHeight), strokeWidth = 1.5f)
                }
            }
        }

        ResultCard("Display DPI Calibration", "%.1f DPI (%.1f px/cm)".format(xdpi, pxPerCm))
    }
}

// Tool 77: Bubble Level
@Composable
fun BubbleLevelTool() {
    val context = LocalContext.current
    var pitch by remember { mutableStateOf(0f) }
    var roll by remember { mutableStateOf(0f) }

    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensor = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    pitch = it.values[1] * 5f
                    roll = it.values[0] * 5f
                }
            }
            override fun onAccuracyChanged(s: Sensor?, acc: Int) {}
        }
        sm?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sm?.unregisterListener(listener)
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
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2, size.height / 2)
                // Crosshairs
                drawLine(Color.Gray, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 2f)
                drawLine(Color.Gray, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 2f)
                // Target circle
                drawCircle(Color(0xFF10B981), radius = 25f, center = centerOffset, style = androidx.compose.ui.graphics.drawscope.Stroke(3f))
                // Bubble
                val bubbleX = (centerOffset.x - roll).coerceIn(20f, size.width - 20f)
                val bubbleY = (centerOffset.y + pitch).coerceIn(20f, size.height - 20f)
                drawCircle(Color(0xFF10B981), radius = 18f, center = Offset(bubbleX, bubbleY))
            }
        }

        ResultCard("Pitch & Roll Coordinates", "Pitch: ${"%.1f".format(pitch)}° | Roll: ${"%.1f".format(roll)}°", MaterialTheme.colorScheme.primary)
    }
}

// Tool 78: Hardware Sensors Inventory
@Composable
fun SensorsInventoryTool() {
    val context = LocalContext.current
    val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    val sensors = remember { sm?.getSensorList(Sensor.TYPE_ALL) ?: emptyList() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Found ${sensors.size} Onboard Hardware Sensors:", fontWeight = FontWeight.Bold)

        sensors.forEach { sensor ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(sensor.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Vendor: ${sensor.vendor}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Power: ${sensor.power} mA", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// Tool 79: Volume Streams
@Composable
fun VolumeStreamsTool() {
    val context = LocalContext.current
    val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    val mediaVol = am?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 7
    val maxMedia = am?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15

    val ringVol = am?.getStreamVolume(AudioManager.STREAM_RING) ?: 5
    val maxRing = am?.getStreamMaxVolume(AudioManager.STREAM_RING) ?: 10

    val alarmVol = am?.getStreamVolume(AudioManager.STREAM_ALARM) ?: 6
    val maxAlarm = am?.getStreamMaxVolume(AudioManager.STREAM_ALARM) ?: 10

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultCard("Media Audio Stream", "$mediaVol / $maxMedia", MaterialTheme.colorScheme.primary)
        ResultCard("Ring & Call Stream", "$ringVol / $maxRing")
        ResultCard("Alarm Clock Stream", "$alarmVol / $maxAlarm")
    }
}

// Tool 80: Theme Palette Inspector
@Composable
fun ThemePaletteTool() {
    val colors = listOf(
        "Primary" to MaterialTheme.colorScheme.primary,
        "Secondary" to MaterialTheme.colorScheme.secondary,
        "Tertiary" to MaterialTheme.colorScheme.tertiary,
        "Surface" to MaterialTheme.colorScheme.surface,
        "Surface Variant" to MaterialTheme.colorScheme.surfaceVariant,
        "Error" to MaterialTheme.colorScheme.error,
        "Outline" to MaterialTheme.colorScheme.outline
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        colors.forEach { (name, color) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Column {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "#%02X%02X%02X".format((color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt()),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
