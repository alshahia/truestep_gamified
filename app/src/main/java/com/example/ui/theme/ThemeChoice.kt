package com.example.ui.theme

import androidx.annotation.StringRes
import com.example.R

enum class ThemeChoice(
    val displayName: String,
    val subtitle: String,
    @StringRes val displayNameRes: Int = 0,
    @StringRes val subtitleRes: Int = 0
) {
    CLASSIC_TEAL("Classic Teal", "Calm and balanced oceanic tones", R.string.theme_classic_teal_title, R.string.theme_classic_teal_desc),
    APPLE_HEALTH("Apple Health", "Clean minimalist white and vivid accent", R.string.theme_apple_health_title, R.string.theme_apple_health_desc),
    BOLD_VIBRANT("Bold Vibrant", "High energy cobalt and radiant coral", R.string.theme_bold_vibrant_title, R.string.theme_bold_vibrant_desc),
    DATA_DASHBOARD("Data Dashboard", "Precision slate with crisp technical accents", R.string.theme_data_dashboard_title, R.string.theme_data_dashboard_desc),
    NEON_FUTURISTIC("Neon Futuristic", "Cyberpunk midnight with neon violet glow", R.string.theme_neon_futuristic_title, R.string.theme_neon_futuristic_desc),
    GLASSMORPHISM("Glassmorphism", "Soft frosted layers with deep translucent sheen", R.string.theme_glassmorphism_title, R.string.theme_glassmorphism_desc),
    FROSTED_GLASS("Frosted Glass", "Cool crystalline ice and arctic blue", R.string.theme_frosted_glass_title, R.string.theme_frosted_glass_desc),
    SOFT_PASTEL("Soft Pastel", "Gentle lavender and warm blush tones", R.string.theme_soft_pastel_title, R.string.theme_soft_pastel_desc),
    BOLD_GRADIENT("Bold Gradient", "Sunset radiance and deep twilight", R.string.theme_bold_gradient_title, R.string.theme_bold_gradient_desc),
    GAMIFIED_GOLD("Gamified Gold", "Championship amber, trophy gold, and obsidian", R.string.theme_gamified_gold_title, R.string.theme_gamified_gold_desc),
    NATURE_ORGANIC("Nature Organic", "Earthy terracotta, warm sand, and sage", R.string.theme_nature_organic_title, R.string.theme_nature_organic_desc),
    PLAYFUL_GAMIFIED("Playful Gamified", "Lively arcade vibes with joyful highlights", R.string.theme_playful_gamified_title, R.string.theme_playful_gamified_desc),
    GRADIENT_MESH("Gradient Mesh", "Fluid modern color blends and smooth curves", R.string.theme_gradient_mesh_title, R.string.theme_gradient_mesh_desc),
    DUAL_TONE("Dual Tone", "Striking contrast of navy blue and warm apricot", R.string.theme_dual_tone_title, R.string.theme_dual_tone_desc),
    CARD_DASHBOARD("Card Dashboard", "Structured modular cards with crisp borders", R.string.theme_card_dashboard_title, R.string.theme_card_dashboard_desc),
    M3_DARK("Material 3 Dark", "Pure deep dark theme with expressive tonal surfaces", R.string.theme_m3_dark_title, R.string.theme_m3_dark_desc),
    CYBERNETIC_STRIDE("Cybernetic Stride", "High-octane anime cyberpunk athletic HUD with obsidian surfaces and electric glow", R.string.theme_cybernetic_stride_title, R.string.theme_cybernetic_stride_desc)
}

