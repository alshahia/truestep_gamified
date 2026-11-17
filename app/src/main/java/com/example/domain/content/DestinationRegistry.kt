package com.example.domain.content

import com.example.R
import com.example.domain.model.Destination

object DestinationRegistry {
    val destinations: List<Destination> = listOf(
        Destination(
            id = "walk_park",
            name = "E-Rank Neon Gate",
            kmRequired = 5.0,
            emoji = "🌀",
            description = "Shibuya perimeter breached by low-tier goblins",
            nameRes = R.string.dest_park_name,
            descriptionRes = R.string.dest_park_desc,
            rankGrade = "E-RANK",
            chamberTitle = "Chamber 1/4: Neon Slums",
            primaryLootEmoji = "💠",
            primaryLootName = "Cybernetic Fragment",
            secondaryLootName = "Mystic Dust (x50)",
            arcadeScoreBonus = 50000
        ),
        Destination(
            id = "walk_bridge",
            name = "D-Rank Kabuki Monolith Gate",
            kmRequired = 12.0,
            emoji = "⚡",
            description = "High-voltage transmission bridge gateway",
            nameRes = R.string.dest_bridge_name,
            descriptionRes = R.string.dest_bridge_desc,
            rankGrade = "D-RANK",
            chamberTitle = "Chamber 2/4: Circuit Spine",
            primaryLootEmoji = "⚙️",
            primaryLootName = "Kinetic Core",
            secondaryLootName = "Arcane Data Chip",
            arcadeScoreBonus = 120000
        ),
        Destination(
            id = "walk_lake",
            name = "C-Rank Substation Matrix Gate",
            kmRequired = 25.0,
            emoji = "🔮",
            description = "Submerged cooling reactor dimensional rift",
            nameRes = R.string.dest_lake_name,
            descriptionRes = R.string.dest_lake_desc,
            rankGrade = "C-RANK",
            chamberTitle = "Chamber 3/4: Glitch Reactor",
            primaryLootEmoji = "💎",
            primaryLootName = "Energy Spark",
            secondaryLootName = "Arcane Data Chip",
            arcadeScoreBonus = 250000
        ),
        Destination(
            id = "walk_marathon",
            name = "B-Rank Arasaka Vault Gate",
            kmRequired = 42.2,
            emoji = "🏆",
            description = "High-security corporate black-site vault",
            nameRes = R.string.dest_marathon_name,
            descriptionRes = R.string.dest_marathon_desc,
            rankGrade = "B-RANK",
            chamberTitle = "Chamber 4/4: Security Core",
            primaryLootEmoji = "⚙️",
            primaryLootName = "Zephyr Engine Mod",
            secondaryLootName = "Arcane Data Chip",
            arcadeScoreBonus = 420000
        ),
        Destination(
            id = "walk_canyon",
            name = "A-Rank Dragon's Core Gate",
            kmRequired = 75.0,
            emoji = "🔥",
            description = "Molten server farm dimensional inferno",
            nameRes = R.string.dest_canyon_name,
            descriptionRes = R.string.dest_canyon_desc,
            rankGrade = "A-RANK",
            chamberTitle = "Chamber 2/4: Magma Spire",
            primaryLootEmoji = "🔮",
            primaryLootName = "Draconic Core (A)",
            secondaryLootName = "Mystic Dust (x300)",
            arcadeScoreBonus = 750000
        ),
        Destination(
            id = "walk_fuji",
            name = "S-Rank Cyber-Gate 04 (Jeju Breach)",
            kmRequired = 120.0,
            emoji = "🌀",
            description = "Catastrophic ant-queen dimensional hyper-gate",
            nameRes = R.string.dest_fuji_name,
            descriptionRes = R.string.dest_fuji_desc,
            rankGrade = "S-RANK",
            chamberTitle = "Chamber 1/4: The Depths",
            primaryLootEmoji = "⚙️",
            primaryLootName = "Legendary Core (S)",
            secondaryLootName = "Arcane Data Chip",
            arcadeScoreBonus = 1200000
        ),
        Destination(
            id = "walk_pyramids",
            name = "S-Rank Demon Castle Gate",
            kmRequired = 180.0,
            emoji = "👑",
            description = "100-floor floating citadel of the Demon King",
            nameRes = R.string.dest_pyramids_name,
            descriptionRes = R.string.dest_pyramids_desc,
            rankGrade = "S-RANK",
            chamberTitle = "Floor 50: Hellfire Palace",
            primaryLootEmoji = "👑",
            primaryLootName = "Demon Monarch Crown",
            secondaryLootName = "Soul Shards (x500)",
            arcadeScoreBonus = 1800000
        ),
        Destination(
            id = "walk_camino",
            name = "S-Rank Monarch Void Gate",
            kmRequired = 260.0,
            emoji = "🌌",
            description = "Shadow dimension rift separating realities",
            nameRes = R.string.dest_camino_name,
            descriptionRes = R.string.dest_camino_desc,
            rankGrade = "S-RANK",
            chamberTitle = "Chamber 4/4: Void Nexus",
            primaryLootEmoji = "🗡️",
            primaryLootName = "Shadow Dagger Blueprint",
            secondaryLootName = "Eternal Core",
            arcadeScoreBonus = 2600000
        ),
        Destination(
            id = "walk_iceland",
            name = "S-Rank Glitch Overlord Gate",
            kmRequired = 380.0,
            emoji = "🧊",
            description = "Sub-zero cryogenic mainframe breach",
            nameRes = R.string.dest_iceland_name,
            descriptionRes = R.string.dest_iceland_desc,
            rankGrade = "S-RANK",
            chamberTitle = "Cryo Chamber: Absolute Zero",
            primaryLootEmoji = "❄️",
            primaryLootName = "Glacial Core (S)",
            secondaryLootName = "Mystic Dust (x800)",
            arcadeScoreBonus = 3800000
        ),
        Destination(
            id = "walk_wall",
            name = "S-Rank Imperial Red Gate",
            kmRequired = 500.0,
            emoji = "🏮",
            description = "Ancient sovereign defensive array portal",
            nameRes = R.string.dest_wall_name,
            descriptionRes = R.string.dest_wall_desc,
            rankGrade = "S-RANK",
            chamberTitle = "The Great Fortress Chamber",
            primaryLootEmoji = "🛡️",
            primaryLootName = "Imperial Nanoweave Exosuit",
            secondaryLootName = "Arcane Data Chip (x5)",
            arcadeScoreBonus = 5000000
        ),
        Destination(
            id = "walk_sahara",
            name = "S-Rank Solar Core Nexus Gate",
            kmRequired = 600.0,
            emoji = "☀️",
            description = "Hyper-charged solar particle collector gate",
            nameRes = R.string.dest_sahara_name,
            descriptionRes = R.string.dest_sahara_desc,
            rankGrade = "S-RANK",
            chamberTitle = "Solar Flare Sanctuary",
            primaryLootEmoji = "⭐",
            primaryLootName = "Apex Solar Chrono",
            secondaryLootName = "Pure Mana Crystals",
            arcadeScoreBonus = 6000000
        ),
        Destination(
            id = "walk_everest",
            name = "Apex Monarch Singularity Gate",
            kmRequired = 680.0,
            emoji = "⚡",
            description = "The ultimate pinnacle dimensional gate of the Shadow Monarch",
            nameRes = R.string.dest_everest_name,
            descriptionRes = R.string.dest_everest_desc,
            rankGrade = "SHADOW MONARCH",
            chamberTitle = "The Apex Throne",
            primaryLootEmoji = "👑",
            primaryLootName = "Shadow Monarch Heart",
            secondaryLootName = "Infinite Mana Core",
            arcadeScoreBonus = 10000000
        )
    )

    fun getNextDestination(totalKm: Double): Destination? {
        return destinations.firstOrNull { it.kmRequired > totalKm }
    }

    fun getCompletedDestinations(totalKm: Double): List<Destination> {
        return destinations.filter { it.kmRequired <= totalKm }
    }
}

