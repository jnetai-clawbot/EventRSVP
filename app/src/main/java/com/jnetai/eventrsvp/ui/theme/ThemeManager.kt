package com.jnetai.eventrsvp.ui.theme

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    fun applyDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}