package com.example

import com.example.domain.content.QuestRegistry
import com.example.domain.model.PaceZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JourneyRetentionUnitTest {

    @Test
    fun testQuestRegistryGeneratesFiveStandardQuests() {
        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 2000,
            goalSteps = 10000,
            activeMinutes = 10,
            hourlySteps = List(24) { 0 },
            completedIds = emptySet(),
            currentHour = 9
        )

        assertEquals(5, quests.size)
        val ids = quests.map { it.id }.toSet()
        assertTrue(ids.contains("quest_morning"))
        assertTrue(ids.contains("quest_brisk"))
        assertTrue(ids.contains("quest_midday"))
        assertTrue(ids.contains("quest_goal_master"))
        assertTrue(ids.contains("quest_twilight"))
    }

    @Test
    fun testMorningQuestCompletion() {
        val hourlySteps = MutableList(24) { 0 }
        hourlySteps[7] = 300
        hourlySteps[8] = 250 // Total before 10 AM: 550 >= 500

        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 550,
            goalSteps = 10000,
            activeMinutes = 5,
            hourlySteps = hourlySteps,
            completedIds = emptySet(),
            currentHour = 9
        )

        val morningQuest = quests.first { it.id == "quest_morning" }
        assertTrue(morningQuest.isCompleted)
        assertEquals(1f, morningQuest.progressPercent, 0.01f)
    }

    @Test
    fun testBriskQuestCompletion() {
        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 1500,
            goalSteps = 10000,
            activeMinutes = 20, // >= 15
            hourlySteps = List(24) { 0 },
            completedIds = emptySet(),
            currentHour = 14
        )

        val briskQuest = quests.first { it.id == "quest_brisk" }
        assertTrue(briskQuest.isCompleted)
        assertEquals(15, briskQuest.currentValue)
    }

    @Test
    fun testMiddayQuestCompletionAtHalfGoal() {
        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 5000, // 5000 / 10000 = 50%
            goalSteps = 10000,
            activeMinutes = 30,
            hourlySteps = List(24) { 0 },
            completedIds = emptySet(),
            currentHour = 13
        )

        val middayQuest = quests.first { it.id == "quest_midday" }
        assertTrue(middayQuest.isCompleted)
        assertEquals(1f, middayQuest.progressPercent, 0.01f)
    }

    @Test
    fun testTargetMasterQuestCompletion() {
        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 10500,
            goalSteps = 10000,
            activeMinutes = 60,
            hourlySteps = List(24) { 0 },
            completedIds = emptySet(),
            currentHour = 17
        )

        val targetQuest = quests.first { it.id == "quest_goal_master" }
        assertTrue(targetQuest.isCompleted)
    }

    @Test
    fun testTwilightQuestCompletion() {
        val hourlySteps = MutableList(24) { 0 }
        hourlySteps[19] = 1200 // After 6 PM (hour 18)

        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 1200,
            goalSteps = 10000,
            activeMinutes = 10,
            hourlySteps = hourlySteps,
            completedIds = emptySet(),
            currentHour = 20
        )

        val twilightQuest = quests.first { it.id == "quest_twilight" }
        assertTrue(twilightQuest.isCompleted)
    }

    @Test
    fun testPreviouslyCompletedIdsRemainCompleted() {
        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 100,
            goalSteps = 10000,
            activeMinutes = 0,
            hourlySteps = List(24) { 0 },
            completedIds = setOf("quest_morning", "quest_midday"),
            currentHour = 15
        )

        val morning = quests.first { it.id == "quest_morning" }
        val midday = quests.first { it.id == "quest_midday" }
        val target = quests.first { it.id == "quest_goal_master" }

        assertTrue(morning.isCompleted)
        assertTrue(midday.isCompleted)
        assertFalse(target.isCompleted)
    }

    @Test
    fun testPaceZoneFromSpm() {
        assertEquals(PaceZone.STROLL, PaceZone.fromSpm(0))
        assertEquals(PaceZone.STROLL, PaceZone.fromSpm(79))
        assertEquals(PaceZone.MODERATE, PaceZone.fromSpm(80))
        assertEquals(PaceZone.MODERATE, PaceZone.fromSpm(99))
        assertEquals(PaceZone.BRISK, PaceZone.fromSpm(100))
        assertEquals(PaceZone.BRISK, PaceZone.fromSpm(119))
        assertEquals(PaceZone.POWER, PaceZone.fromSpm(120))
        assertEquals(PaceZone.POWER, PaceZone.fromSpm(150))
    }
}
