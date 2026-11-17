package com.example.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

object OemBatteryHelper {

    val manufacturer: String = Build.MANUFACTURER.lowercase()

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
            powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
        } else {
            true
        }
    }

    fun getOemName(): String {
        return when {
            manufacturer.contains("samsung") -> "Samsung (One UI)"
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> "Xiaomi / POCO (MIUI / HyperOS)"
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> "Huawei / Honor (EMUI / MagicOS)"
            manufacturer.contains("oppo") || manufacturer.contains("realme") -> "OPPO / Realme (ColorOS)"
            manufacturer.contains("oneplus") -> "OnePlus (OxygenOS)"
            manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> "Vivo / iQOO (FuntouchOS / OriginOS)"
            manufacturer.contains("google") -> "Google Pixel"
            else -> Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        }
    }

    fun getOemInstructions(): List<String> {
        return when {
            manufacturer.contains("samsung") -> listOf(
                "1. Open Settings → Device Care → Battery → Background usage limits.",
                "2. Tap 'Never sleeping apps' and add TrueStep.",
                "3. Disable 'Put unused apps to sleep' for TrueStep."
            )
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> listOf(
                "1. Open Security app → Manage apps → TrueStep.",
                "2. Turn ON 'Autostart'.",
                "3. Set 'Battery saver' to 'No restrictions'."
            )
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> listOf(
                "1. Open Settings → Battery → App launch.",
                "2. Find TrueStep and turn off 'Manage automatically'.",
                "3. Turn ON 'Auto-launch', 'Secondary launch', and 'Run in background'."
            )
            manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> listOf(
                "1. Open Settings → Battery → App battery management.",
                "2. Select TrueStep and enable 'Allow background activity'.",
                "3. Enable 'Allow auto-launch' in Startup settings."
            )
            manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> listOf(
                "1. Open Settings → Battery → Background power consumption.",
                "2. Select TrueStep and choose 'High background power consumption'."
            )
            else -> listOf(
                "1. Open Settings → Apps → TrueStep → Battery.",
                "2. Set App battery usage to 'Unrestricted'.",
                "3. Ensure 'Pause app activity if unused' is turned OFF."
            )
        }
    }

    fun openOemSettings(context: Context) {
        val intents = mutableListOf<Intent>()

        when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> {
                intents.add(Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")))
                intents.add(Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.powercenter.PowerSettings")))
            }
            manufacturer.contains("samsung") -> {
                intents.add(Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")))
                intents.add(Intent().setComponent(ComponentName("com.samsung.android.sm", "com.samsung.android.sm.battery.ui.BatteryActivity")))
            }
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> {
                intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")))
                intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")))
            }
            manufacturer.contains("oppo") || manufacturer.contains("realme") -> {
                intents.add(Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")))
                intents.add(Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")))
            }
            manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> {
                intents.add(Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")))
                intents.add(Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity")))
            }
        }

        // Standard AOSP fallbacks
        intents.add(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        intents.add(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        })

        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (_: Exception) {
                // Try next intent fallback
            }
        }
    }
}
