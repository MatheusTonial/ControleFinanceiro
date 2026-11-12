package com.tonial.controlefinanceiro.ui.theme

import android.content.Context

enum class ThemeMode {
    LIGHT, DARK, SYSTEM;

    companion object {
        fun fromKey(key: String?): ThemeMode = entries.find { it.name == key } ?: SYSTEM
    }
}

object ThemePreference {
    private const val PREFS_NAME = "app_prefs" // mesmo arquivo já usado pelo backup em AppScaffold.kt
    private const val KEY_THEME_MODE = "theme_mode"

    fun get(context: Context): ThemeMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return ThemeMode.fromKey(prefs.getString(KEY_THEME_MODE, null))
    }

    fun set(context: Context, mode: ThemeMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME_MODE, mode.name)
            .apply()
    }
}
