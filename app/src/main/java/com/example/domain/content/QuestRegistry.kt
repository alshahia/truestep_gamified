package com.example.domain.content

import com.example.R
import com.example.domain.model.DailyQuest

object QuestRegistry {

    fun generateDailyQuests(
        currentSteps: Int,
        goalSteps: Int,
        activeMinutes: Int,
        hourlySteps: List<Int>,
        completedIds: Set<String>,
        currentHour: Int
    ): List<DailyQuest> {
        val halfGoal = maxOf(1000, goalSteps / 2)

        // 1. Morning Momentum: Steps taken before 10 AM
        val morningSteps = hourlySteps.take(10).sum()
        val morningCompleted = completedIds.contains("quest_morning") || (morningSteps >= 500 && currentHour < 12)

        // 2. Brisk Aerobic: 15 active minutes
        val briskCompleted = completedIds.contains("quest_brisk") || activeMinutes >= 15

        // 3. Midday Stride: 50% of daily goal
        val middayCompleted = completedIds.contains("quest_midday") || currentSteps >= halfGoal

        // 4. Target Master: 100% of daily goal
        val targetCompleted = completedIds.contains("quest_goal_master") || currentSteps >= goalSteps

        // 5. Twilight Trek: Steps taken after 6:00 PM (hour 18)
        val eveningSteps = hourlySteps.drop(18).sum()
        val eveningCompleted = completedIds.contains("quest_twilight") || eveningSteps >= 1000

        return listOf(
            DailyQuest(
                id = "quest_morning",
                titleRes = R.string.quest_morning_title,
                descRes = R.string.quest_morning_desc,
                iconEmoji = "🌅",
                targetValue = 500,
                currentValue = if (morningCompleted) 500 else morningSteps.coerceAtMost(500),
                rewardXp = 50,
                isCompleted = morningCompleted
            ),
            DailyQuest(
                id = "quest_brisk",
                titleRes = R.string.quest_brisk_title,
                descRes = R.string.quest_brisk_desc,
                iconEmoji = "⚡",
                targetValue = 15,
                currentValue = if (briskCompleted) 15 else activeMinutes.coerceAtMost(15),
                rewardXp = 75,
                isCompleted = briskCompleted
            ),
            DailyQuest(
                id = "quest_midday",
                titleRes = R.string.quest_midday_title,
                descRes = R.string.quest_midday_desc,
                iconEmoji = "☀️",
                targetValue = halfGoal,
                currentValue = if (middayCompleted) halfGoal else currentSteps.coerceAtMost(halfGoal),
                rewardXp = 100,
                isCompleted = middayCompleted
            ),
            DailyQuest(
                id = "quest_goal_master",
                titleRes = R.string.quest_goal_master_title,
                descRes = R.string.quest_goal_master_desc,
                iconEmoji = "🎯",
                targetValue = goalSteps,
                currentValue = if (targetCompleted) goalSteps else currentSteps.coerceAtMost(goalSteps),
                rewardXp = 150,
                isCompleted = targetCompleted
            ),
            DailyQuest(
                id = "quest_twilight",
                titleRes = R.string.quest_twilight_title,
                descRes = R.string.quest_twilight_desc,
                iconEmoji = "🌙",
                targetValue = 1000,
                currentValue = if (eveningCompleted) 1000 else eveningSteps.coerceAtMost(1000),
                rewardXp = 75,
                isCompleted = eveningCompleted
            )
        )
    }
}
