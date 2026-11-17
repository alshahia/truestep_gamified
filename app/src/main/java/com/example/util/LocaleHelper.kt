package com.example.util

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import java.util.Locale

class LocalizedActivityContext(
    base: Context,
    private val activity: ComponentActivity
) : ContextWrapper(base),
    ActivityResultRegistryOwner by activity,
    OnBackPressedDispatcherOwner by activity

object LocaleHelper {

    private const val PREFS_NAME = "locale_settings"
    private const val KEY_LANG = "persisted_language"

    fun getPersistedLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_LANG, "") ?: ""
        return AppLanguage.fromCode(code)
    }

    fun persistLanguage(context: Context, language: AppLanguage) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, language.code).apply()
    }

    fun wrap(context: Context, language: AppLanguage): Context {
        val targetLocale = if (language == AppLanguage.SYSTEM || language.code.isEmpty()) {
            val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                android.content.res.Resources.getSystem().configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                android.content.res.Resources.getSystem().configuration.locale
            }
            systemLocale ?: Locale.getDefault()
        } else {
            Locale.forLanguageTag(language.code)
        }
        Locale.setDefault(targetLocale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(targetLocale)
        config.setLayoutDirection(targetLocale)

        val configContext = context.createConfigurationContext(config)
        return if (context is ComponentActivity) {
            LocalizedActivityContext(configContext, context)
        } else {
            configContext
        }
    }

    fun applyLanguage(context: Context, language: AppLanguage): Context {
        persistLanguage(context, language)

        val targetLocale = if (language.code.isEmpty()) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(language.code)
        }
        Locale.setDefault(targetLocale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(targetLocale)
        config.setLayoutDirection(targetLocale)

        // API 33+ per-app language preference
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                if (language.code.isEmpty()) {
                    localeManager?.applicationLocales = LocaleList.getEmptyLocaleList()
                } else {
                    localeManager?.applicationLocales = LocaleList.forLanguageTags(language.code)
                }
            } catch (_: Exception) {
            }
        }

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        return context.createConfigurationContext(config)
    }
}
