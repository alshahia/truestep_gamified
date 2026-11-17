package com.example.domain.content

import com.example.R
import com.example.domain.model.GearItem
import com.example.domain.model.GearRarity
import com.example.domain.model.GearSlot
import com.example.domain.model.VaultCategory

object GearRegistry {

    val allGearItems: List<GearItem> = listOf(
        // Slot 1: Footwear [EPIC]
        GearItem(
            id = "gear_neon_phantom",
            nameRes = R.string.gear_neon_phantom_title,
            descRes = R.string.gear_neon_phantom_desc,
            slot = GearSlot.FOOTWEAR,
            rarity = GearRarity.EPIC,
            level = 8,
            requiredHunterLevel = 1,
            statBonusText = "+35 AGI • +12% Step-to-EXP",
            perkText = "Overdrive Sprint",
            cpBonus = 720,
            isEquipped = true,
            category = VaultCategory.FOOTWEAR
        ),

        // Slot 2: Core [RARE]
        GearItem(
            id = "gear_zephyr_engine",
            nameRes = R.string.gear_zephyr_engine_title,
            descRes = R.string.gear_zephyr_engine_desc,
            slot = GearSlot.CORE,
            rarity = GearRarity.RARE,
            level = 5,
            requiredHunterLevel = 1,
            statBonusText = "+18 AURA • +10% Wind Pacing",
            perkText = "Aero Defiance",
            cpBonus = 450,
            isEquipped = true,
            category = VaultCategory.CORES
        ),

        // Slot 3: Exosuit [EPIC]
        GearItem(
            id = "gear_nanoweave_vest",
            nameRes = R.string.gear_nanoweave_vest_title,
            descRes = R.string.gear_nanoweave_vest_desc,
            slot = GearSlot.EXOSUIT,
            rarity = GearRarity.EPIC,
            level = 7,
            requiredHunterLevel = 1,
            statBonusText = "+24 END • -5% Pace Fatigue",
            perkText = "Kinetic Loop",
            cpBonus = 610,
            isEquipped = true,
            category = VaultCategory.ALL
        ),

        // Slot 4: Chrono [RARE]
        GearItem(
            id = "gear_chrono_watch",
            nameRes = R.string.gear_chrono_watch_title,
            descRes = R.string.gear_chrono_watch_desc,
            slot = GearSlot.CHRONO,
            rarity = GearRarity.RARE,
            level = 6,
            requiredHunterLevel = 1,
            statBonusText = "+15 AGI • 5Hz GPS Sensor Lock",
            perkText = "Precision Route",
            cpBonus = 410,
            isEquipped = true,
            category = VaultCategory.ALL
        ),

        // Alternative Footwear [RARE]
        GearItem(
            id = "gear_vapor_glide",
            nameRes = R.string.gear_vapor_glide_title,
            descRes = R.string.gear_vapor_glide_desc,
            slot = GearSlot.FOOTWEAR,
            rarity = GearRarity.RARE,
            level = 4,
            requiredHunterLevel = 1,
            statBonusText = "+18 Agility Boost",
            perkText = "Smooth Asphalt Bias",
            cpBonus = 340,
            isEquipped = false,
            category = VaultCategory.FOOTWEAR
        ),

        // Locked Legendary Footwear
        GearItem(
            id = "gear_apex_pulse",
            nameRes = R.string.gear_apex_pulse_title,
            descRes = R.string.gear_apex_pulse_desc,
            slot = GearSlot.FOOTWEAR,
            rarity = GearRarity.LEGENDARY,
            level = 15,
            requiredHunterLevel = 15,
            statBonusText = "+55 AGI • Spark Trail",
            perkText = "Celestial Velocity",
            cpBonus = 1200,
            isEquipped = false,
            category = VaultCategory.FOOTWEAR
        ),

        // Consumable: Overclock Battery
        GearItem(
            id = "gear_overclock_battery",
            nameRes = R.string.gear_overclock_battery_title,
            descRes = R.string.gear_overclock_battery_desc,
            slot = GearSlot.CORE,
            rarity = GearRarity.RARE,
            level = 1,
            statBonusText = "2x Spark Generation",
            perkText = "Lasts next 5,000 steps",
            cpBonus = 0,
            isEquipped = false,
            category = VaultCategory.BOOSTS,
            isConsumable = true,
            count = 3
        ),

        // Consumable: Stride Sync Elixir
        GearItem(
            id = "gear_sync_elixir",
            nameRes = R.string.gear_sync_elixir_title,
            descRes = R.string.gear_sync_elixir_desc,
            slot = GearSlot.CORE,
            rarity = GearRarity.EPIC,
            level = 1,
            statBonusText = "+500 Stride EXP",
            perkText = "Instant Avatar Surge",
            cpBonus = 0,
            isEquipped = false,
            category = VaultCategory.BOOSTS,
            isConsumable = true,
            count = 5
        ),

        // Material: Core [Matrix 4]
        GearItem(
            id = "gear_matrix_core",
            nameRes = R.string.gear_matrix_core_title,
            descRes = R.string.gear_matrix_core_desc,
            slot = GearSlot.CORE,
            rarity = GearRarity.RARE,
            level = 1,
            statBonusText = "Synthesis Engine",
            perkText = "Shoe stat re-roll mat",
            cpBonus = 0,
            isEquipped = false,
            category = VaultCategory.MATERIALS,
            isConsumable = false,
            count = 6
        ),

        // Material: Apex Shards
        GearItem(
            id = "gear_apex_shards",
            nameRes = R.string.gear_apex_shards_title,
            descRes = R.string.gear_apex_shards_desc,
            slot = GearSlot.FOOTWEAR,
            rarity = GearRarity.EPIC,
            level = 1,
            statBonusText = "Crafting Shard",
            perkText = "6 more to synthesize",
            cpBonus = 0,
            isEquipped = false,
            category = VaultCategory.MATERIALS,
            isConsumable = false,
            count = 14,
            craftProgress = 0.70f
        )
    )

    fun getEquippedItems(profileFootwearId: String, profileCoreId: String, profileExosuitId: String, profileChronoId: String): List<GearItem> {
        val equippedIds = setOf(profileFootwearId, profileCoreId, profileExosuitId, profileChronoId)
        return allGearItems.filter { it.id in equippedIds }
    }

    fun calculateCombatPower(
        level: Int,
        streakDays: Int,
        equippedItems: List<GearItem>,
        strength: Int = 45,
        agility: Int = 62,
        vitality: Int = 38,
        sense: Int = 50
    ): Int {
        val baseFromLevel = level * 250
        val baseFromStreak = streakDays * 120
        val gearBonus = equippedItems.sumOf { it.cpBonus }
        val statBonus = (strength + agility + vitality + sense) * 15
        return baseFromLevel + baseFromStreak + gearBonus + statBonus
    }
}
