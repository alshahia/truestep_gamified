package com.example.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

/**
 * Universal sealed representation of UI text.
 * Allows ViewModels, domain logic, and repositories to emit localized strings,
 * formatted arguments, or plurals cleanly without holding Android Context references.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as StringResource
            return resId == other.resId && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            return 31 * resId + args.contentHashCode()
        }
    }

    class PluralResource(
        @PluralsRes val resId: Int,
        val count: Int,
        vararg val args: Any
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as PluralResource
            return resId == other.resId && count == other.count && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + count
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
            is PluralResource -> pluralStringResource(resId, count, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
            is PluralResource -> context.resources.getQuantityString(resId, count, *args)
        }
    }
}
