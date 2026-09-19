package io.mns.base.app.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.data.SortOrder
import io.mns.base.app.ui.viewmodels.SettingViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val SettingsBrand1 = Color(0xFF8B5CF6)
private val SettingsBrand2 = Color(0xFFEC4899)
private val SettingsBrush = Brush.linearGradient(
    colors = listOf(SettingsBrand1, SettingsBrand2),
    start = Offset.Zero,
    end = Offset.Infinite
)

private val DataBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF3B82F6), Color(0xFF06B6D4)),
    start = Offset.Zero,
    end = Offset.Infinite
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    isDark: Boolean,
    onBack: () -> Unit,
    onToggleTheme: () -> Unit,
    viewModel: SettingViewModel = koinViewModel()
) {
    val sortOrder by viewModel.sortOrder.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportBackup(uri) { success, msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.restoreBackup(uri) { success, msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    SettingScreenContent(
        isDark = isDark,
        sortOrder = sortOrder,
        onBack = onBack,
        onToggleTheme = {
            viewModel.toggleTheme()
            onToggleTheme()
        },
        onSortOrderChange = { viewModel.setSortOrder(it) },
        onExportClick = {
            val time = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            exportLauncher.launch("mtodo_backup_$time.json")
        },
        onRestoreClick = {
            restoreLauncher.launch(arrayOf("application/json", "*/*"))
        },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenContent(
    isDark: Boolean,
    sortOrder: SortOrder = SortOrder.CREATION_DATE_DESC,
    onBack: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    onSortOrderChange: (SortOrder) -> Unit = {},
    onExportClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    animate: Boolean = true
) {
    var showSortDialog by remember { mutableStateOf(false) }

    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Default Sort Order", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortOrder.entries.forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSortOrderChange(order)
                                    showSortDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (order == sortOrder),
                                onClick = {
                                    onSortOrderChange(order)
                                    showSortDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = order.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = TextStyle(
                            brush = SettingsBrush,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            // Section: Appearance
            AnimatedSettingsSection(delayMs = 60L, animate = animate) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Settings,
                        iconBrush = SettingsBrush,
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark theme"
                    ) {
                        Switch(
                            checked = isDark,
                            onCheckedChange = { onToggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SettingsBrand1,
                                uncheckedThumbColor = Color.White,
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }

            // Section: Tasks & Organization
            AnimatedSettingsSection(delayMs = 90L, animate = animate) {
                Text(
                    text = "Tasks & Organization",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Sort,
                        iconBrush = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669))),
                        title = "Default Sort Order",
                        subtitle = sortOrder.label
                    ) {
                        IconButton(onClick = { showSortDialog = true }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Change sort order")
                        }
                    }
                }
            }

            // Section: Backup & Restore
            AnimatedSettingsSection(delayMs = 120L, animate = animate) {
                Text(
                    text = "Data Management",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.CloudUpload,
                        iconBrush = DataBrush,
                        title = "Export Backup (JSON)",
                        subtitle = "Save all active and completed tasks"
                    ) {
                        FilledTonalButton(
                            onClick = onExportClick,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Export", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    SettingsRow(
                        icon = Icons.Default.CloudDownload,
                        iconBrush = Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFF43F5E))),
                        title = "Restore from Backup",
                        subtitle = "Import tasks from a JSON backup file"
                    ) {
                        FilledTonalButton(
                            onClick = onRestoreClick,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Restore", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Section: About
            AnimatedSettingsSection(delayMs = 150L, animate = animate) {
                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Info,
                        iconBrush = Brush.linearGradient(
                            colors = listOf(Color(0xFF3B82F6), Color(0xFF06B6D4)),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        ),
                        title = "mTodo v2.4.0",
                        subtitle = "Made with ♥ and Jetpack Compose"
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun AnimatedSettingsSection(
    delayMs: Long,
    animate: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(!animate) }
    LaunchedEffect(Unit) {
        if (animate) {
            delay(delayMs)
            visible = true
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = SettingsBrand1.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 4.dp),
        content = content
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconBrush: Brush,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = SettingsBrand1.copy(alpha = 0.2f)
                )
                .background(iconBrush, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing()
    }
}
