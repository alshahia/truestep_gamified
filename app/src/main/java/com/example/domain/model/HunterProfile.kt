package com.example.domain.model

data class HunterProfile(
    val name: String = "Ren Amamiya",
    val classTitle: String = "Wind Courier • Speedster",
    val level: Int = 14,
    val currentExp: Int = 1850,
    val expToNextLevel: Int = 2500,
    val rankTitle: String = "B-RANK",
    val combatPower: Int = 4820,
    val sparks: Int = 48,
    val cores: Int = 6,
    val shards: Int = 1420,
    val manaOverdrivePercent: Int = 78,
    val isAuraMultiplierActive: Boolean = true,
    val auraMultiplier: Float = 1.25f,
    val equippedFootwearId: String = "gear_neon_phantom",
    val equippedCoreId: String = "gear_zephyr_engine",
    val equippedExosuitId: String = "gear_nanoweave_vest",
    val equippedChronoId: String = "gear_chrono_watch",
    val agilityBonusPercent: Int = 42,
    val staminaBonusPercent: Int = 15,
    val expBonusPercent: Int = 20,
    val fortuneBonusPercent: Int = 18,
    // Solo Leveling Hunter Stat Point System
    val strength: Int = 45,
    val agility: Int = 62,
    val vitality: Int = 38,
    val sense: Int = 50,
    val availableStatPoints: Int = 3
) {
    companion object {
        fun computeRankTitle(level: Int): String = when {
            level >= 50 -> "SHADOW MONARCH"
            level >= 40 -> "S-RANK"
            level >= 30 -> "A-RANK"
            level >= 20 -> "B-RANK"
            level >= 10 -> "C-RANK"
            level >= 5 -> "D-RANK"
            else -> "E-RANK"
        }
    }
}
