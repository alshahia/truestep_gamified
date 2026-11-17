package com.example.ui.model

enum class HistoryChartRange {
    DAILY,
    WEEKLY,
    MONTHLY
}

data class ChartBarData(
    val label: String,
    val value: Int,
    val goal: Int,
    val isCurrent: Boolean,
    val isGoalMet: Boolean
)

data class ChartRangeData(
    val range: HistoryChartRange,
    val titleRes: Int,
    val goalValue: Int,
    val maxValue: Int,
    val bars: List<ChartBarData>
)
