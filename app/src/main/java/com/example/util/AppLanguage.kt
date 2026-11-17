package com.example.util

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val isRtl: Boolean
) {
    SYSTEM("", "System Default", "حسب النظام", false),
    ENGLISH("en", "English", "English", false),
    ARABIC("ar", "Arabic", "العربية", true);

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
        }
    }
}
