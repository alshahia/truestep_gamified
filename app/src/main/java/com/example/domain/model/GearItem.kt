package com.example.domain.model

import androidx.annotation.StringRes
import com.example.R

enum class GearSlot(@StringRes val titleRes: Int) {
    FOOTWEAR(R.string.vault_filter_footwear),
    CORE(R.string.vault_filter_cores),
    EXOSUIT(R.string.active_loadout_title),
    CHRONO(R.string.active_loadout_title)
}

enum class GearRarity(val label: String, val colorHex: Long) {
    COMMON("COMMON", 0xFF94A3B8),
    RARE("RARE", 0xFF00F0FF),
    EPIC("EPIC", 0xFF7B61FF),
    LEGENDARY("LEGENDARY", 0xFFF59E0B)
}

enum class VaultCategory(@StringRes val titleRes: Int) {
    ALL(R.string.vault_filter_all),
    FOOTWEAR(R.string.vault_filter_footwear),
    CORES(R.string.vault_filter_cores),
    BOOSTS(R.string.vault_filter_boosts),
    MATERIALS(R.string.vault_filter_materials)
}

data class GearItem(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    val slot: GearSlot,
    val rarity: GearRarity,
    val level: Int,
    val requiredHunterLevel: Int = 1,
    val statBonusText: String,
    val perkText: String,
    val cpBonus: Int,
    val isEquipped: Boolean = false,
    val category: VaultCategory = VaultCategory.FOOTWEAR,
    val isConsumable: Boolean = false,
    val count: Int = 1,
    val craftProgress: Float? = null // e.g. 0.7f for 14/20 shards
)
