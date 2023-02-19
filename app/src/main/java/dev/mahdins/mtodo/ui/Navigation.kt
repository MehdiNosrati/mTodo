package dev.mahdins.mtodo.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import dev.mahdins.mtodo.R

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    object TodoPage : Screen("todo", R.string.todo, Icons.Filled.CheckBox)
    object DonePage : Screen("done", R.string.done, Icons.Filled.Check)
    object SettingsPage :
        Screen("settings", R.string.settings, Icons.Filled.Settings)
}