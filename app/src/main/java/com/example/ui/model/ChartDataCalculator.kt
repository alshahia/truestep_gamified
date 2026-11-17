package com.example.ui.model

import com.example.R
import com.example.data.local.DailySteps
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

object ChartDataCalculator {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun buildChartRangeData(
        range: HistoryChartRange,
        history: List<DailySteps>,
        todayRecord: DailySteps?,
        targetDailyGoal: Int
    ): ChartRangeData {
        val today = LocalDate.now()
        val historyMap = history.associateBy { it.date }.toMutableMap()
        if (todayRecord != null) {
            historyMap[todayRecord.date] = todayRecord
        }

        return when (range) {
            HistoryChartRange.DAILY -> buildDailyChart(today, historyMap, targetDailyGoal)
            HistoryChartRange.WEEKLY -> buildWeeklyChart(today, historyMap, targetDailyGoal)
            HistoryChartRange.MONTHLY -> buildMonthlyChart(today, historyMap, targetDailyGoal)
        }
    }

    private fun buildDailyChart(
        today: LocalDate,
        historyMap: Map<String, DailySteps>,
        targetDailyGoal: Int
    ): ChartRangeData {
        val bars = (13 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong())
            val dateStr = date.format(dateFormatter)
            val record = historyMap[dateStr]
            val steps = record?.totalSteps ?: 0
            val goal = record?.goalSteps ?: targetDailyGoal
            ChartBarData(
                label = date.dayOfMonth.toString(),
                value = steps,
                goal = goal,
                isCurrent = offset == 0,
                isGoalMet = steps >= goal
            )
        }

        val maxVal = maxOf(targetDailyGoal, (bars.maxOfOrNull { it.value } ?: targetDailyGoal))
        val chartMax = (maxVal * 1.15).toInt().coerceAtLeast(1000)

        return ChartRangeData(
            range = HistoryChartRange.DAILY,
            titleRes = R.string.last_14_days,
            goalValue = targetDailyGoal,
            maxValue = chartMax,
            bars = bars
        )
    }

    private fun buildWeeklyChart(
        today: LocalDate,
        historyMap: Map<String, DailySteps>,
        targetDailyGoal: Int
    ): ChartRangeData {
        val weeklyGoal = targetDailyGoal * 7
        val bars = (11 downTo 0).map { weekOffset ->
            val weekStart = today.minusWeeks(weekOffset.toLong()).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
            var weekTotal = 0
            for (i in 0..6) {
                val d = weekStart.plusDays(i.toLong())
                if (!d.isAfter(today)) {
                    val dateStr = d.format(dateFormatter)
                    weekTotal += historyMap[dateStr]?.totalSteps ?: 0
                }
            }
            ChartBarData(
                label = weekStart.dayOfMonth.toString(),
                value = weekTotal,
                goal = weeklyGoal,
                isCurrent = weekOffset == 0,
                isGoalMet = weekTotal >= weeklyGoal
            )
        }

        val maxVal = maxOf(weeklyGoal, (bars.maxOfOrNull { it.value } ?: weeklyGoal))
        val chartMax = (maxVal * 1.15).toInt().coerceAtLeast(1000)

        return ChartRangeData(
            range = HistoryChartRange.WEEKLY,
            titleRes = R.string.last_12_weeks,
            goalValue = weeklyGoal,
            maxValue = chartMax,
            bars = bars
        )
    }

    private fun buildMonthlyChart(
        today: LocalDate,
        historyMap: Map<String, DailySteps>,
        targetDailyGoal: Int
    ): ChartRangeData {
        val monthlyGoal = targetDailyGoal * 30
        val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
        val bars = (11 downTo 0).map { monthOffset ->
            val monthDate = today.minusMonths(monthOffset.toLong())
            val year = monthDate.year
            val month = monthDate.monthValue
            val daysInMonth = monthDate.lengthOfMonth()

            var monthTotal = 0
            for (d in 1..daysInMonth) {
                val day = LocalDate.of(year, month, d)
                if (!day.isAfter(today)) {
                    val dateStr = day.format(dateFormatter)
                    monthTotal += historyMap[dateStr]?.totalSteps ?: 0
                }
            }

            ChartBarData(
                label = monthDate.format(monthFormatter),
                value = monthTotal,
                goal = monthlyGoal,
                isCurrent = monthOffset == 0,
                isGoalMet = monthTotal >= monthlyGoal
            )
        }

        val maxVal = maxOf(monthlyGoal, (bars.maxOfOrNull { it.value } ?: monthlyGoal))
        val chartMax = (maxVal * 1.15).toInt().coerceAtLeast(1000)

        return ChartRangeData(
            range = HistoryChartRange.MONTHLY,
            titleRes = R.string.last_12_months,
            goalValue = monthlyGoal,
            maxValue = chartMax,
            bars = bars
        )
    }
}
