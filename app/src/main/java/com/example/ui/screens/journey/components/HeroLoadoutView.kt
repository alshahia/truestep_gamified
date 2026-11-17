package com.example.ui.screens.journey.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.content.GearRegistry
import com.example.domain.model.GearItem
import com.example.domain.model.GearRarity
import com.example.domain.model.GearSlot
import com.example.domain.model.HunterProfile
import com.example.domain.model.VaultCategory
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroLoadoutView(
    hunterProfile: HunterProfile,
    allGearItems: List<GearItem>,
    onEquipGear: (slotType: String, gearId: String) -> Unit,
    onTuneGear: (gearId: String) -> Unit,
    onAllocateStatPoint: (statType: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(VaultCategory.ALL) }
    var inspectedGear by remember { mutableStateOf<GearItem?>(null) }
    var actionToastText by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val equippedItems = remember(hunterProfile, allGearItems) {
        val equippedIds = setOf(
            hunterProfile.equippedFootwearId,
            hunterProfile.equippedCoreId,
            hunterProfile.equippedExosuitId,
            hunterProfile.equippedChronoId
        )
        allGearItems.filter { it.id in equippedIds }
    }

    val combatPower = remember(hunterProfile, equippedItems) {
        GearRegistry.calculateCombatPower(
            level = hunterProfile.level,
            streakDays = 7, // or active streak
            equippedItems = equippedItems
        )
    }

    val filteredVaultItems = remember(selectedCategory, allGearItems) {
        if (selectedCategory == VaultCategory.ALL) allGearItems
        else allGearItems.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_loadout_view"),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        val errorColor = MaterialTheme.colorScheme.error

        // TOP HERO STATUS / TELEMETRY HUD
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(primaryColor.copy(alpha = 0.7f), tertiaryColor.copy(alpha = 0.6f)))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth().testTag("hunter_profile_card")
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                // Header Row: Avatar, Name, Title, Combat Power (CP)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                    ) {
                        // Avatar Badge with High-Tech Hunter Artwork
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.5.dp, primaryColor, RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.proto_cyber_runner_1791613481498),
                                contentDescription = "Hunter Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(13.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(2.dp)
                                    .clip(MaterialTheme.appShapes.pill)
                                    .background(primaryColor)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LV.${hunterProfile.level}",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = hunterProfile.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "WIND COURIER",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryColor,
                                    modifier = Modifier
                                        .clip(MaterialTheme.appShapes.pill)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(0.5.dp, secondaryColor, MaterialTheme.appShapes.pill)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Class: High-Cadence Speedster",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Combat Power (CP) Badge
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                    ) {
                        Text(
                            text = stringResource(R.string.hero_combat_power).uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$combatPower",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "CP",
                                style = MaterialTheme.typography.labelSmall,
                                color = secondaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Currency Vault Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    // Sparks
                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(MaterialTheme.spacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(text = "SPARKS", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${hunterProfile.sparks}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = primaryColor)
                            }
                        }
                    }

                    // Cores
                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(MaterialTheme.spacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = secondaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(text = "CORES", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${hunterProfile.cores}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = secondaryColor)
                            }
                        }
                    }

                    // Shards
                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(MaterialTheme.spacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = tertiaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(text = "SHARDS", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${hunterProfile.shards}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = tertiaryColor)
                            }
                        }
                    }
                }

                // Dynamic Stat Telemetry Summary Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    Text(
                        text = "STR ${hunterProfile.strength} • Power",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = errorColor,
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = MaterialTheme.spacing.xs),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "AGI ${hunterProfile.agility} • Speed",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = MaterialTheme.spacing.xs),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "VIT ${hunterProfile.vitality} • Stamina",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = secondaryColor,
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = MaterialTheme.spacing.xs),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "SEN ${hunterProfile.sense} • Mana",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = tertiaryColor,
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = MaterialTheme.spacing.xs),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // SOLO LEVELING HUNTER ATTRIBUTE TERMINAL (STR, AGI, VIT, SEN)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hunter_attribute_matrix_card")
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                // Terminal Header with Available Stat Points
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(MaterialTheme.appShapes.medium)
                                .background(primaryColor.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.hunter_stat_points_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Solo Leveling System Allocator",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Available Stat Points Badge
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(
                                if (hunterProfile.availableStatPoints > 0) primaryColor.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                            .testTag("stat_points_badge")
                    ) {
                        Text(
                            text = if (hunterProfile.availableStatPoints > 0)
                                stringResource(R.string.hunter_stat_points_available, hunterProfile.availableStatPoints)
                            else
                                stringResource(R.string.hunter_stat_points_none),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hunterProfile.availableStatPoints > 0) primaryColor
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 4 Interactive Attribute Rows
                HunterStatRow(
                    label = stringResource(R.string.hunter_stat_str),
                    desc = stringResource(R.string.hunter_stat_str_desc),
                    value = hunterProfile.strength,
                    icon = Icons.Default.FlashOn,
                    iconTint = errorColor,
                    canAllocate = hunterProfile.availableStatPoints > 0,
                    testTag = "allocate_stat_str",
                    onAllocate = {
                        onAllocateStatPoint("STR")
                        actionToastText = context.getString(R.string.hunter_stat_allocated_toast, "STR")
                    }
                )

                HunterStatRow(
                    label = stringResource(R.string.hunter_stat_agi),
                    desc = stringResource(R.string.hunter_stat_agi_desc),
                    value = hunterProfile.agility,
                    icon = Icons.Default.Speed,
                    iconTint = primaryColor,
                    canAllocate = hunterProfile.availableStatPoints > 0,
                    testTag = "allocate_stat_agi",
                    onAllocate = {
                        onAllocateStatPoint("AGI")
                        actionToastText = context.getString(R.string.hunter_stat_allocated_toast, "AGI")
                    }
                )

                HunterStatRow(
                    label = stringResource(R.string.hunter_stat_vit),
                    desc = stringResource(R.string.hunter_stat_vit_desc),
                    value = hunterProfile.vitality,
                    icon = Icons.Default.Shield,
                    iconTint = secondaryColor,
                    canAllocate = hunterProfile.availableStatPoints > 0,
                    testTag = "allocate_stat_vit",
                    onAllocate = {
                        onAllocateStatPoint("VIT")
                        actionToastText = context.getString(R.string.hunter_stat_allocated_toast, "VIT")
                    }
                )

                HunterStatRow(
                    label = stringResource(R.string.hunter_stat_sen),
                    desc = stringResource(R.string.hunter_stat_sen_desc),
                    value = hunterProfile.sense,
                    icon = Icons.Default.Visibility,
                    iconTint = tertiaryColor,
                    canAllocate = hunterProfile.availableStatPoints > 0,
                    testTag = "allocate_stat_sen",
                    onAllocate = {
                        onAllocateStatPoint("SEN")
                        actionToastText = context.getString(R.string.hunter_stat_allocated_toast, "SEN")
                    }
                )
            }
        }

        // ACTIVE LOADOUT SECTION (4/4 SLOTS TUNED)
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    Icon(imageVector = Icons.Default.SportsMartialArts, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(text = stringResource(R.string.active_loadout_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Text(text = stringResource(R.string.active_loadout_slots), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // 4 Equipped Slots
            equippedItems.forEach { gear ->
                Card(
                    onClick = { inspectedGear = gear },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, Color(gear.rarity.colorHex).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth().testTag("equipped_gear_${gear.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(MaterialTheme.spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(MaterialTheme.appShapes.medium)
                                    .background(Color(gear.rarity.colorHex).copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = "Lv.${gear.level}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(gear.rarity.colorHex)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = gear.rarity.label,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(gear.rarity.colorHex),
                                        modifier = Modifier
                                            .clip(MaterialTheme.appShapes.pill)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                    Text(
                                        text = stringResource(gear.nameRes),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = gear.statBonusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = secondaryColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "⚡ Perk: ${gear.perkText}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = {
                                onTuneGear(gear.id)
                                actionToastText = "Tuned ${context.getString(gear.nameRes)} (+2 CP)!"
                            },
                            shape = MaterialTheme.appShapes.pill,
                            modifier = Modifier.testTag("tune_button_${gear.id}")
                        ) {
                            Text(text = stringResource(R.string.gear_tune_button), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // CYBERNETIC FORGE SHORTCUT BANNER
        Card(
            shape = MaterialTheme.appShapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("cybernetic_forge_banner")
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(MaterialTheme.appShapes.medium)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Icon(imageVector = Icons.Default.PrecisionManufacturing, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Column {
                        Text(text = stringResource(R.string.gear_forge_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = stringResource(R.string.gear_forge_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Button(
                    onClick = {
                        actionToastText = "Forge Synchronized! Shards distilled into gear power."
                    },
                    shape = MaterialTheme.appShapes.pill,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("forge_enhance_button")
                ) {
                    Text(text = stringResource(R.string.gear_forge_enhance), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // STORAGE VAULT (BENTO GRID WITH TABS)
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.storage_vault_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.vault_capacity_format, filteredVaultItems.size, 80), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Category Filter Pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                items(VaultCategory.entries) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(text = stringResource(cat.titleRes), fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Bento Grid: 2 columns
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                filteredVaultItems.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        rowItems.forEach { item ->
                            val isEquipped = item.id in listOf(
                                hunterProfile.equippedFootwearId,
                                hunterProfile.equippedCoreId,
                                hunterProfile.equippedExosuitId,
                                hunterProfile.equippedChronoId
                            )
                            val isLocked = hunterProfile.level < item.requiredHunterLevel

                            Card(
                                onClick = { inspectedGear = item },
                                shape = MaterialTheme.appShapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.weight(1f).testTag("vault_card_${item.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(MaterialTheme.spacing.sm),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.rarity.label,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(item.rarity.colorHex)
                                        )
                                        if (isEquipped) {
                                            Text(
                                                text = stringResource(R.string.gear_equipped_badge).uppercase(),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = secondaryColor
                                            )
                                        } else if (isLocked) {
                                            Text(
                                                text = "REQ. LV.${item.requiredHunterLevel}",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = tertiaryColor
                                            )
                                        }
                                    }

                                    Text(
                                        text = stringResource(item.nameRes),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = item.statBonusText,
                                        fontSize = 10.sp,
                                        color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else secondaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    item.craftProgress?.let { prog ->
                                        LinearProgressIndicator(
                                            progress = { prog },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(MaterialTheme.appShapes.pill),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }

                                    // Action Button
                                    if (isEquipped) {
                                        FilledTonalButton(
                                            onClick = { inspectedGear = item },
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            shape = MaterialTheme.appShapes.pill
                                        ) {
                                            Text(text = stringResource(R.string.gear_tune_button), fontSize = 10.sp)
                                        }
                                    } else if (isLocked) {
                                        Button(
                                            onClick = { /* Locked */ },
                                            enabled = false,
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            shape = MaterialTheme.appShapes.pill
                                        ) {
                                            Text(text = stringResource(R.string.gear_locked_badge), fontSize = 10.sp)
                                        }
                                    } else if (item.isConsumable) {
                                        Button(
                                            onClick = {
                                                actionToastText = "Activated ${context.getString(item.nameRes)}!"
                                            },
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            shape = MaterialTheme.appShapes.pill,
                                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = MaterialTheme.colorScheme.onSecondary)
                                        ) {
                                            Text(text = "ACTIVATE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                onEquipGear(item.slot.name, item.id)
                                                actionToastText = "Equipped ${context.getString(item.nameRes)}!"
                                            },
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            shape = MaterialTheme.appShapes.pill
                                        ) {
                                            Text(text = stringResource(R.string.gear_equip_button), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Action Toast Feedback
        actionToastText?.let { toast ->
            Card(
                shape = MaterialTheme.appShapes.pill,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = toast,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    // GEAR INSPECTION MODAL DRAWER
    inspectedGear?.let { gear ->
        ModalBottomSheet(
            onDismissRequest = { inspectedGear = null },
            sheetState = sheetState,
            shape = MaterialTheme.appShapes.bottomSheet
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.md)
                    .testTag("gear_inspection_drawer"),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                        Text(
                            text = gear.rarity.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(gear.rarity.colorHex),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.pill)
                                .background(Color(gear.rarity.colorHex).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Text(
                            text = stringResource(gear.nameRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { inspectedGear = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }

                Card(
                    shape = MaterialTheme.appShapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        Text(text = stringResource(gear.descRes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Stat Profile: ${gear.statBonusText}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text(text = "Active Perk: ${gear.perkText}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Text(text = "Combat Power: +${gear.cpBonus} CP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    FilledTonalButton(
                        onClick = {
                            onTuneGear(gear.id)
                            inspectedGear = null
                            actionToastText = "Overclock matrix synchronized (+2 CP)!"
                        },
                        shape = MaterialTheme.appShapes.medium,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.tune_stats_title))
                    }

                    Button(
                        onClick = {
                            onEquipGear(gear.slot.name, gear.id)
                            inspectedGear = null
                            actionToastText = "Equipped ${context.getString(gear.nameRes)}!"
                        },
                        shape = MaterialTheme.appShapes.medium,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.confirm_loadout_title))
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            }
        }
    }
}

@Composable
private fun HunterStatRow(
    label: String,
    desc: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    canAllocate: Boolean,
    testTag: String,
    onAllocate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.appShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(MaterialTheme.appShapes.small)
                        .background(iconTint.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$value",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = iconTint
                        )
                    }
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // [+] Allocate Button
            FilledTonalButton(
                onClick = onAllocate,
                enabled = canAllocate,
                shape = MaterialTheme.appShapes.pill,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (canAllocate) iconTint.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (canAllocate) iconTint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag(testTag)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Allocate $label",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "+1",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

