package com.example.ui.theme

enum class DarkModeConfig(val storageKey: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromStorageKey(key: String?): DarkModeConfig {
            return entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: SYSTEM
        }
    }
}
