package com.example.domain.content

import androidx.annotation.StringRes
import com.example.R
import java.time.LocalDate

data class MotivationalQuote(
    val id: Int,
    @StringRes val textRes: Int
)

object MotivationalQuotesRegistry {
    val quotes = listOf(
        MotivationalQuote(1, R.string.splash_quote_1),
        MotivationalQuote(2, R.string.splash_quote_2),
        MotivationalQuote(3, R.string.splash_quote_3),
        MotivationalQuote(4, R.string.splash_quote_4),
        MotivationalQuote(5, R.string.splash_quote_5),
        MotivationalQuote(6, R.string.splash_quote_6)
    )

    fun getDailyQuote(dayOfYear: Int = LocalDate.now().dayOfYear): MotivationalQuote {
        val index = Math.floorMod(dayOfYear, quotes.size)
        return quotes[index]
    }
}
