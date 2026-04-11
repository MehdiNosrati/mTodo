package io.mns.androidlib

import android.content.res.Configuration
import android.content.res.Resources

fun Resources.isDark() =
    (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_NO