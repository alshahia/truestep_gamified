package com.example

import com.example.domain.content.GearRegistry
import com.example.domain.model.GearRarity
import com.example.domain.model.GearSlot
import com.example.domain.model.HunterProfile
import com.example.domain.model.VaultCategory
import com.example.ui.theme.ThemeChoice
import com.example.ui.theme.ThemeRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CyberneticStrideUnitTest {

    @Test
    fun testGearRegistryContainsRequiredItems() {
        val allItems = GearRegistry.allGearItems
        assertTrue("Gear registry should contain at least 6 iconic items", allItems.size >= 6)

        val neonPhantom = allItems.find { it.id == "gear_neon_phantom" }
        assertNotNull(neonPhantom)
        assertEquals(GearSlot.FOOTWEAR, neonPhantom?.slot)
        assertEquals(GearRarity.EPIC, neonPhantom?.rarity)
        assertEquals(720, neonPhantom?.cpBonus)

        val zephyrEngine = allItems.find { it.id == "gear_zephyr_engine" }
        assertNotNull(zephyrEngine)
        assertEquals(GearSlot.CORE, zephyrEngine?.slot)
        assertEquals(GearRarity.RARE, zephyrEngine?.rarity)
        assertEquals(450, zephyrEngine?.cpBonus)

        val nanoweave = allItems.find { it.id == "gear_nanoweave_vest" }
        assertNotNull(nanoweave)
        assertEquals(GearSlot.EXOSUIT, nanoweave?.slot)
        assertEquals(610, nanoweave?.cpBonus)

        val chronoWatch = allItems.find { it.id == "gear_chrono_watch" }
        assertNotNull(chronoWatch)
        assertEquals(GearSlot.CHRONO, chronoWatch?.slot)
        assertEquals(410, chronoWatch?.cpBonus)
    }

    @Test
    fun testCombatPowerCalculation() {
        val equippedItems = GearRegistry.getEquippedItems(
            profileFootwearId = "gear_neon_phantom",
            profileCoreId = "gear_zephyr_engine",
            profileExosuitId = "gear_nanoweave_vest",
            profileChronoId = "gear_chrono_watch"
        )
        assertEquals(4, equippedItems.size)

        val cp = GearRegistry.calculateCombatPower(
            level = 14,
            streakDays = 7,
            equippedItems = equippedItems
        )

        // Formula: 14 * 250 + 7 * 120 + sum(720 + 450 + 610 + 410) + (45 + 62 + 38 + 50) * 15
        // = 3500 + 840 + 2190 + 2925 = 9455
        assertEquals(9455, cp)
    }

    @Test
    fun testGearEquipLookupFallback() {
        val equipped = GearRegistry.getEquippedItems(
            profileFootwearId = "non_existent_id",
            profileCoreId = "gear_zephyr_engine",
            profileExosuitId = "gear_nanoweave_vest",
            profileChronoId = "gear_chrono_watch"
        )
        // Non-existent footwear is omitted, returning 3 items
        assertEquals(3, equipped.size)
    }

    @Test
    fun testFilterByCategory() {
        val footwear = GearRegistry.allGearItems.filter { it.category == VaultCategory.FOOTWEAR }
        assertTrue(footwear.isNotEmpty())
        assertTrue(footwear.all { it.category == VaultCategory.FOOTWEAR })

        val cores = GearRegistry.allGearItems.filter { it.category == VaultCategory.CORES }
        assertTrue(cores.isNotEmpty())
        assertTrue(cores.all { it.category == VaultCategory.CORES })

        val materials = GearRegistry.allGearItems.filter { it.category == VaultCategory.MATERIALS }
        assertTrue(materials.isNotEmpty())
        assertTrue(materials.all { it.category == VaultCategory.MATERIALS })
    }

    @Test
    fun testHunterProfileProgression() {
        val profile = HunterProfile(
            name = "Ren Amamiya",
            level = 14,
            currentExp = 1850,
            expToNextLevel = 2500,
            combatPower = 6510,
            sparks = 48,
            cores = 6,
            shards = 1420
        )

        assertEquals("Ren Amamiya", profile.name)
        assertEquals(14, profile.level)
        assertEquals(48, profile.sparks)
        assertEquals(6, profile.cores)
        assertEquals(1420, profile.shards)
        assertEquals("gear_neon_phantom", profile.equippedFootwearId)
    }

    @Test
    fun testCyberneticStrideThemeSpec() {
        val cybernetic = ThemeRegistry.getThemeSpec(ThemeChoice.CYBERNETIC_STRIDE)
        assertNotNull("Cybernetic Stride theme spec must exist", cybernetic)
        assertEquals(ThemeChoice.CYBERNETIC_STRIDE, cybernetic.id)
        assertNotNull(cybernetic.darkBackground)
        assertNotNull(cybernetic.homeProgressGradient)
    }

    @Test
    fun testStatPointAllocationIncreasesCombatPower() {
        val equippedItems = GearRegistry.getEquippedItems(
            profileFootwearId = "gear_neon_phantom",
            profileCoreId = "gear_zephyr_engine",
            profileExosuitId = "gear_nanoweave_vest",
            profileChronoId = "gear_chrono_watch"
        )

        val cpInitial = GearRegistry.calculateCombatPower(
            level = 14,
            streakDays = 7,
            equippedItems = equippedItems,
            strength = 45,
            agility = 62,
            vitality = 38,
            sense = 50
        )

        // Allocate +1 STR
        val cpAfterStr = GearRegistry.calculateCombatPower(
            level = 14,
            streakDays = 7,
            equippedItems = equippedItems,
            strength = 46,
            agility = 62,
            vitality = 38,
            sense = 50
        )

        assertEquals(15, cpAfterStr - cpInitial)

        // Allocate +1 AGI
        val cpAfterAgi = GearRegistry.calculateCombatPower(
            level = 14,
            streakDays = 7,
            equippedItems = equippedItems,
            strength = 46,
            agility = 63,
            vitality = 38,
            sense = 50
        )

        assertEquals(30, cpAfterAgi - cpInitial)
    }

    @Test
    fun testOptionCDestinationRegistryExpeditions() {
        val destinations = com.example.domain.content.DestinationRegistry.destinations
        assertTrue(destinations.size >= 10)

        // Verify E-Rank Gate
        val eRank = destinations.first()
        assertEquals("E-RANK", eRank.rankGrade)
        assertTrue(eRank.chamberTitle.isNotEmpty())
        assertTrue(eRank.primaryLootName.isNotEmpty())

        // Verify S-Rank Gate 04
        val sRank = destinations.first { it.id == "walk_fuji" }
        assertEquals("S-RANK", sRank.rankGrade)
        assertEquals("Chamber 1/4: The Depths", sRank.chamberTitle)
        assertEquals("Legendary Core (S)", sRank.primaryLootName)
        assertEquals("Arcane Data Chip", sRank.secondaryLootName)
        assertEquals(1200000, sRank.arcadeScoreBonus)
    }
}
