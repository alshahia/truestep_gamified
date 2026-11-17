# ProGuard and R8 Rules for TrueStep Production Release

# Preserve line numbers and source attributes for production stack traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Room Database, DAOs, and Entities
-keep class * extends androidx.room.RoomDatabase
-keep class com.example.data.local.** { *; }
-dontwarn androidx.room.paging.**

# Domain Models and Registries
-keep class com.example.domain.model.** { *; }
-keep class com.example.domain.content.** { *; }
-keep class com.example.domain.reconciliation.** { *; }

# UI State and Navigation Models
-keep class com.example.ui.model.** { *; }
-keep class com.example.ui.screens.settings.model.** { *; }

# Foreground Services, Receivers, and App Application
-keep class com.example.StepTrackApplication { *; }
-keep class com.example.service.** { *; }
-keep class com.example.receiver.** { *; }

# Kotlin Coroutines, Flows, and Dispatchers
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Health Connect Client SDK
-keep class androidx.health.connect.client.** { *; }
-dontwarn androidx.health.connect.client.**

# AndroidX WorkManager
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-dontwarn androidx.work.**

# AndroidX DataStore Preferences
-keep class androidx.datastore.preferences.protobuf.** { *; }
-dontwarn androidx.datastore.preferences.protobuf.**

# Moshi and JSON serialization
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter
-keep class * {
    @com.squareup.moshi.Json <fields>;
}

# Android Compose runtime
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.runtime.**
