package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Top-level background functions (K2 FIR compiler workaround)
private val classicTealBg: @Composable BoxScope.() -> Unit = {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE6F4F1), Color(0xFFF7FBFB))
                )
            )
    )
}

private val classicTealDarkBg: @Composable BoxScope.() -> Unit = {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF101414), Color(0xFF141E1E))
                )
            )
    )
}

private val neonFuturisticBg: @Composable BoxScope.() -> Unit = {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF1E1035), Color(0xFF090614))
                )
            )
    )
}

private val glassmorphismBg: @Composable BoxScope.() -> Unit = {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFEEF2FF), Color(0xFFF8FAFC))
                )
            )
    )
}

private val glassmorphismDarkBg: @Composable BoxScope.() -> Unit = {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF030712))
                )
            )
    )
}

private val cyberneticStrideBg: @Composable BoxScope.() -> Unit = {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF171B26), Color(0xFF0F131D))
                )
            )
    )
}

object ThemeRegistry {

    private val standardShapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp)
    )

    private val roundedPlayfulShapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(20.dp),
        large = RoundedCornerShape(26.dp),
        extraLarge = RoundedCornerShape(32.dp)
    )

    private val technicalSharpShapes = Shapes(
        extraSmall = RoundedCornerShape(2.dp),
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(12.dp),
        extraLarge = RoundedCornerShape(16.dp)
    )

    fun getThemeSpec(choice: ThemeChoice): AppThemeSpec {
        return when (choice) {
            ThemeChoice.CLASSIC_TEAL -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF006A6A),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFBCEBEB),
                    onPrimaryContainer = Color(0xFF002020),
                    secondary = Color(0xFF4A6363),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFCCE8E7),
                    tertiary = Color(0xFF028090),
                    onTertiary = Color.White,
                    surface = Color(0xFFF8FAFA),
                    onSurface = Color(0xFF191C1C),
                    surfaceVariant = Color(0xFFDAE5E4),
                    onSurfaceVariant = Color(0xFF3F4948),
                    outline = Color(0xFF6F7978),
                    outlineVariant = Color(0xFFBFC9C8),
                    background = Color(0xFFF4F7F7),
                    onBackground = Color(0xFF191C1C)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFF4DDADA),
                    onPrimary = Color(0xFF003737),
                    primaryContainer = Color(0xFF004F4F),
                    onPrimaryContainer = Color(0xFFBCEBEB),
                    secondary = Color(0xFFB1CCCC),
                    onSecondary = Color(0xFF1C3534),
                    secondaryContainer = Color(0xFF334B4B),
                    tertiary = Color(0xFF26A69A),
                    onTertiary = Color(0xFF003731),
                    surface = Color(0xFF191C1C),
                    onSurface = Color(0xFFE0E3E2),
                    surfaceVariant = Color(0xFF3F4948),
                    onSurfaceVariant = Color(0xFFBFC9C8),
                    outline = Color(0xFF899392),
                    outlineVariant = Color(0xFF3F4948),
                    background = Color(0xFF101414),
                    onBackground = Color(0xFFE0E3E2)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF006A6A), Color(0xFF028090))),
                background = classicTealBg,
                darkBackground = classicTealDarkBg
            )

            ThemeChoice.APPLE_HEALTH -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFFE63946),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFFDAD6),
                    onPrimaryContainer = Color(0xFF410002),
                    secondary = Color(0xFF457B9D),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFD1E4FF),
                    tertiary = Color(0xFFFF6B6B),
                    onTertiary = Color.White,
                    surface = Color(0xFFFFFFFF),
                    onSurface = Color(0xFF1A1C1E),
                    surfaceVariant = Color(0xFFF1F3F5),
                    onSurfaceVariant = Color(0xFF43474E),
                    outline = Color(0xFF74777F),
                    outlineVariant = Color(0xFFC4C7CF),
                    background = Color(0xFFF8F9FA),
                    onBackground = Color(0xFF1A1C1E)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFFFB4AB),
                    onPrimary = Color(0xFF690005),
                    primaryContainer = Color(0xFF93000A),
                    onPrimaryContainer = Color(0xFFFFDAD6),
                    secondary = Color(0xFFA8DADC),
                    onSecondary = Color(0xFF0E3537),
                    secondaryContainer = Color(0xFF2E4D50),
                    tertiary = Color(0xFFFF8B8B),
                    onTertiary = Color(0xFF561E1E),
                    surface = Color(0xFF18181A),
                    onSurface = Color(0xFFE3E2E6),
                    surfaceVariant = Color(0xFF2A2B2E),
                    onSurfaceVariant = Color(0xFFC4C7CF),
                    outline = Color(0xFF8E9099),
                    outlineVariant = Color(0xFF43474E),
                    background = Color(0xFF0F0F11),
                    onBackground = Color(0xFFE3E2E6)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFFFF3366), Color(0xFFFF6B6B)))
            )

            ThemeChoice.BOLD_VIBRANT -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF0052CC),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFDCE2FF),
                    onPrimaryContainer = Color(0xFF001550),
                    secondary = Color(0xFFFF5722),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFFFDBCE),
                    tertiary = Color(0xFF6366F1),
                    onTertiary = Color.White,
                    surface = Color(0xFFFFFFFF),
                    onSurface = Color(0xFF1A1C1E),
                    surfaceVariant = Color(0xFFE7ECF5),
                    onSurfaceVariant = Color(0xFF44474F),
                    outline = Color(0xFF757780),
                    outlineVariant = Color(0xFFC5C6D0),
                    background = Color(0xFFF4F6F9),
                    onBackground = Color(0xFF1A1C1E)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFB6C4FF),
                    onPrimary = Color(0xFF00277F),
                    primaryContainer = Color(0xFF003B99),
                    onPrimaryContainer = Color(0xFFDCE2FF),
                    secondary = Color(0xFFFF8A65),
                    onSecondary = Color(0xFF5B1A04),
                    secondaryContainer = Color(0xFF7E2A0E),
                    tertiary = Color(0xFFA5B4FC),
                    onTertiary = Color(0xFF1E1B4B),
                    surface = Color(0xFF181C24),
                    onSurface = Color(0xFFE2E2E9),
                    surfaceVariant = Color(0xFF282F3D),
                    onSurfaceVariant = Color(0xFFC5C6D0),
                    outline = Color(0xFF8F909A),
                    outlineVariant = Color(0xFF44474F),
                    background = Color(0xFF0E121A),
                    onBackground = Color(0xFFE2E2E9)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF0052CC), Color(0xFF6366F1)))
            )

            ThemeChoice.DATA_DASHBOARD -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF0F766E),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFCCFBF1),
                    onPrimaryContainer = Color(0xFF00201D),
                    secondary = Color(0xFF0284C7),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFE0F2FE),
                    tertiary = Color(0xFF0D9488),
                    onTertiary = Color.White,
                    surface = Color(0xFFF8FAFC),
                    onSurface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFFE2E8F0),
                    onSurfaceVariant = Color(0xFF475569),
                    outline = Color(0xFF64748B),
                    outlineVariant = Color(0xFFCBD5E1),
                    background = Color(0xFFF1F5F9),
                    onBackground = Color(0xFF0F172A)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFF2DD4BF),
                    onPrimary = Color(0xFF134E4A),
                    primaryContainer = Color(0xFF042F2E),
                    onPrimaryContainer = Color(0xFFCCFBF1),
                    secondary = Color(0xFF38BDF8),
                    onSecondary = Color(0xFF082F49),
                    secondaryContainer = Color(0xFF075985),
                    tertiary = Color(0xFF5EEAD4),
                    onTertiary = Color(0xFF003732),
                    surface = Color(0xFF0F172A),
                    onSurface = Color(0xFFF1F5F9),
                    surfaceVariant = Color(0xFF1E293B),
                    onSurfaceVariant = Color(0xFF94A3B8),
                    outline = Color(0xFF64748B),
                    outlineVariant = Color(0xFF334155),
                    background = Color(0xFF020617),
                    onBackground = Color(0xFFF1F5F9)
                ),
                shapes = technicalSharpShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF0F766E), Color(0xFF0EA5E9)))
            )

            ThemeChoice.NEON_FUTURISTIC -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF7C3AED),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFEDE9FE),
                    onPrimaryContainer = Color(0xFF2E1065),
                    secondary = Color(0xFF06B6D4),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFCFFAFE),
                    onSecondaryContainer = Color(0xFF164E63),
                    tertiary = Color(0xFFD946EF),
                    onTertiary = Color.White,
                    surface = Color(0xFFFAF5FF),
                    onSurface = Color(0xFF1E1035),
                    surfaceVariant = Color(0xFFE9D5FF),
                    onSurfaceVariant = Color(0xFF581C87),
                    outline = Color(0xFF8B5CF6),
                    outlineVariant = Color(0xFFDDD6FE),
                    background = Color(0xFFF5F3FF),
                    onBackground = Color(0xFF1E1035)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFA78BFA),
                    onPrimary = Color(0xFF3B0764),
                    primaryContainer = Color(0xFF581C87),
                    onPrimaryContainer = Color(0xFFEDE9FE),
                    secondary = Color(0xFF22D3EE),
                    onSecondary = Color(0xFF083344),
                    secondaryContainer = Color(0xFF155E75),
                    onSecondaryContainer = Color(0xFFCFFAFE),
                    tertiary = Color(0xFFF472B6),
                    onTertiary = Color(0xFF500724),
                    surface = Color(0xFF140D26),
                    onSurface = Color(0xFFF3E8FF),
                    surfaceVariant = Color(0xFF251640),
                    onSurfaceVariant = Color(0xFFC4B5FD),
                    outline = Color(0xFF7C3AED),
                    outlineVariant = Color(0xFF4C1D95),
                    background = Color(0xFF0A0517),
                    onBackground = Color(0xFFF3E8FF)
                ),
                shapes = roundedPlayfulShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFFA855F7), Color(0xFFEC4899))),
                background = neonFuturisticBg
            )

            ThemeChoice.GLASSMORPHISM -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF4F46E5),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFE0E7FF),
                    onPrimaryContainer = Color(0xFF1E1B4B),
                    secondary = Color(0xFF0284C7),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFE0F2FE),
                    onSecondaryContainer = Color(0xFF001D33),
                    tertiary = Color(0xFF06B6D4),
                    onTertiary = Color.White,
                    surface = Color(0xFFF8FAFC),
                    onSurface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFFE2E8F0),
                    onSurfaceVariant = Color(0xFF475569),
                    outline = Color(0xFF94A3B8),
                    outlineVariant = Color(0xFFCBD5E1),
                    background = Color(0xFFF1F5F9),
                    onBackground = Color(0xFF0F172A)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFF818CF8),
                    onPrimary = Color(0xFF1E1B4B),
                    primaryContainer = Color(0xFF312E81),
                    onPrimaryContainer = Color(0xFFE0E7FF),
                    secondary = Color(0xFF38BDF8),
                    onSecondary = Color(0xFF082F49),
                    secondaryContainer = Color(0xFF075985),
                    onSecondaryContainer = Color(0xFFE0F2FE),
                    tertiary = Color(0xFF22D3EE),
                    onTertiary = Color(0xFF083344),
                    surface = Color(0xFF0F172A),
                    onSurface = Color(0xFFF8FAFC),
                    surfaceVariant = Color(0xFF1E293B),
                    onSurfaceVariant = Color(0xFF94A3B8),
                    outline = Color(0xFF64748B),
                    outlineVariant = Color(0xFF334155),
                    background = Color(0xFF030712),
                    onBackground = Color(0xFFF8FAFC)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF38BDF8))),
                background = glassmorphismBg,
                darkBackground = glassmorphismDarkBg
            )

            ThemeChoice.FROSTED_GLASS -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF0284C7),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFE0F2FE),
                    onPrimaryContainer = Color(0xFF001D33),
                    secondary = Color(0xFF0D9488),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFCCFBF1),
                    onSecondaryContainer = Color(0xFF00201D),
                    tertiary = Color(0xFF06B6D4),
                    onTertiary = Color.White,
                    surface = Color(0xFFF0F9FF),
                    onSurface = Color(0xFF0C1929),
                    surfaceVariant = Color(0xFFE0F2FE),
                    onSurfaceVariant = Color(0xFF0369A1),
                    outline = Color(0xFF7DD3FC),
                    outlineVariant = Color(0xFFBAE6FD),
                    background = Color(0xFFE0F2FE),
                    onBackground = Color(0xFF0C1929)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFF38BDF8),
                    onPrimary = Color(0xFF082F49),
                    primaryContainer = Color(0xFF075985),
                    onPrimaryContainer = Color(0xFFE0F2FE),
                    secondary = Color(0xFF2DD4BF),
                    onSecondary = Color(0xFF134E4A),
                    secondaryContainer = Color(0xFF042F2E),
                    onSecondaryContainer = Color(0xFFCCFBF1),
                    tertiary = Color(0xFF22D3EE),
                    onTertiary = Color(0xFF083344),
                    surface = Color(0xFF0B192C),
                    onSurface = Color(0xFFF0F9FF),
                    surfaceVariant = Color(0xFF132845),
                    onSurfaceVariant = Color(0xFF7DD3FC),
                    outline = Color(0xFF0284C7),
                    outlineVariant = Color(0xFF075985),
                    background = Color(0xFF040D1A),
                    onBackground = Color(0xFFF0F9FF)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF06B6D4)))
            )

            ThemeChoice.SOFT_PASTEL -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF8B5CF6),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFF3E8FF),
                    onPrimaryContainer = Color(0xFF3B0764),
                    secondary = Color(0xFFF43F5E),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFFFE4E6),
                    onSecondaryContainer = Color(0xFF4C0519),
                    tertiary = Color(0xFFA855F7),
                    onTertiary = Color.White,
                    surface = Color(0xFFFDFBFE),
                    onSurface = Color(0xFF1F1A24),
                    surfaceVariant = Color(0xFFF3E8FF),
                    onSurfaceVariant = Color(0xFF6B21A8),
                    outline = Color(0xFFC4B5FD),
                    outlineVariant = Color(0xFFE9D5FF),
                    background = Color(0xFFFAF5FF),
                    onBackground = Color(0xFF1F1A24)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFC4B5FD),
                    onPrimary = Color(0xFF4C1D95),
                    primaryContainer = Color(0xFF6D28D9),
                    onPrimaryContainer = Color(0xFFF3E8FF),
                    secondary = Color(0xFFFB7185),
                    onSecondary = Color(0xFF4C0519),
                    secondaryContainer = Color(0xFF881337),
                    onSecondaryContainer = Color(0xFFFFE4E6),
                    tertiary = Color(0xFFD8B4FE),
                    onTertiary = Color(0xFF3B0764),
                    surface = Color(0xFF1A1429),
                    onSurface = Color(0xFFFDFBFE),
                    surfaceVariant = Color(0xFF2B1F42),
                    onSurfaceVariant = Color(0xFFC4B5FD),
                    outline = Color(0xFF8B5CF6),
                    outlineVariant = Color(0xFF4C1D95),
                    background = Color(0xFF100B1C),
                    onBackground = Color(0xFFFDFBFE)
                ),
                shapes = roundedPlayfulShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFFC084FC), Color(0xFFF472B6)))
            )

            ThemeChoice.BOLD_GRADIENT -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFFEA580C),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFFEDD5),
                    onPrimaryContainer = Color(0xFF431407),
                    secondary = Color(0xFF7C3AED),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFEDE9FE),
                    onSecondaryContainer = Color(0xFF2E1065),
                    tertiary = Color(0xFFD946EF),
                    onTertiary = Color.White,
                    surface = Color(0xFFFFF7ED),
                    onSurface = Color(0xFF261812),
                    surfaceVariant = Color(0xFFFFEDD5),
                    onSurfaceVariant = Color(0xFF9A3412),
                    outline = Color(0xFFFDBA74),
                    outlineVariant = Color(0xFFFED7AA),
                    background = Color(0xFFFFF1EB),
                    onBackground = Color(0xFF261812)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFFB923C),
                    onPrimary = Color(0xFF7C2D12),
                    primaryContainer = Color(0xFF9A3412),
                    onPrimaryContainer = Color(0xFFFFEDD5),
                    secondary = Color(0xFFA78BFA),
                    onSecondary = Color(0xFF3B0764),
                    secondaryContainer = Color(0xFF581C87),
                    onSecondaryContainer = Color(0xFFEDE9FE),
                    tertiary = Color(0xFFE879F9),
                    onTertiary = Color(0xFF4A044E),
                    surface = Color(0xFF261812),
                    onSurface = Color(0xFFFFF7ED),
                    surfaceVariant = Color(0xFF3A241C),
                    onSurfaceVariant = Color(0xFFFB923C),
                    outline = Color(0xFFEA580C),
                    outlineVariant = Color(0xFF9A3412),
                    background = Color(0xFF180D08),
                    onBackground = Color(0xFFFFF7ED)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFFEA580C), Color(0xFFD946EF)))
            )

            ThemeChoice.GAMIFIED_GOLD -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFFD97706),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFEF3C7),
                    onPrimaryContainer = Color(0xFF451A03),
                    secondary = Color(0xFF1E293B),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFF1F5F9),
                    onSecondaryContainer = Color(0xFF0F172A),
                    tertiary = Color(0xFFCA8A04),
                    onTertiary = Color.White,
                    surface = Color(0xFFFFFDF5),
                    onSurface = Color(0xFF201808),
                    surfaceVariant = Color(0xFFFEF9C3),
                    onSurfaceVariant = Color(0xFF78350F),
                    outline = Color(0xFFFBBF24),
                    outlineVariant = Color(0xFFFDE68A),
                    background = Color(0xFFFEF9C3),
                    onBackground = Color(0xFF201808)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFFBBF24),
                    onPrimary = Color(0xFF78350F),
                    primaryContainer = Color(0xFF92400E),
                    onPrimaryContainer = Color(0xFFFEF3C7),
                    secondary = Color(0xFFCBD5E1),
                    onSecondary = Color(0xFF0F172A),
                    secondaryContainer = Color(0xFF334155),
                    onSecondaryContainer = Color(0xFFF1F5F9),
                    tertiary = Color(0xFFFACC15),
                    onTertiary = Color(0xFF713F12),
                    surface = Color(0xFF201808),
                    onSurface = Color(0xFFFFFDF5),
                    surfaceVariant = Color(0xFF32260C),
                    onSurfaceVariant = Color(0xFFFBBF24),
                    outline = Color(0xFFD97706),
                    outlineVariant = Color(0xFF78350F),
                    background = Color(0xFF120D04),
                    onBackground = Color(0xFFFFFDF5)
                ),
                shapes = roundedPlayfulShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEAB308)))
            )

            ThemeChoice.NATURE_ORGANIC -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFFC2410C),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFFEDD5),
                    onPrimaryContainer = Color(0xFF431407),
                    secondary = Color(0xFF57534E),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFE7E5E4),
                    onSecondaryContainer = Color(0xFF1C1917),
                    tertiary = Color(0xFF65A30D),
                    onTertiary = Color.White,
                    surface = Color(0xFFFAF8F5),
                    onSurface = Color(0xFF1C1917),
                    surfaceVariant = Color(0xFFF5F2EB),
                    onSurfaceVariant = Color(0xFF57534E),
                    outline = Color(0xFFA8A29E),
                    outlineVariant = Color(0xFFD6D3D1),
                    background = Color(0xFFF5F2EB),
                    onBackground = Color(0xFF1C1917)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFFB923C),
                    onPrimary = Color(0xFF431407),
                    primaryContainer = Color(0xFF7C2D12),
                    onPrimaryContainer = Color(0xFFFFEDD5),
                    secondary = Color(0xFFA8A29E),
                    onSecondary = Color(0xFF1C1917),
                    secondaryContainer = Color(0xFF44403C),
                    onSecondaryContainer = Color(0xFFE7E5E4),
                    tertiary = Color(0xFFA3E635),
                    onTertiary = Color(0xFF1A2E05),
                    surface = Color(0xFF1C1917),
                    onSurface = Color(0xFFFAF8F5),
                    surfaceVariant = Color(0xFF292524),
                    onSurfaceVariant = Color(0xFFA8A29E),
                    outline = Color(0xFF78716C),
                    outlineVariant = Color(0xFF44403C),
                    background = Color(0xFF0C0A09),
                    onBackground = Color(0xFFFAF8F5)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFFC2410C), Color(0xFFEA580C)))
            )

            ThemeChoice.PLAYFUL_GAMIFIED -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFFBE185D),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFFCE7F3),
                    onPrimaryContainer = Color(0xFF500724),
                    secondary = Color(0xFF0284C7),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFE0F2FE),
                    onSecondaryContainer = Color(0xFF001D33),
                    tertiary = Color(0xFF8B5CF6),
                    onTertiary = Color.White,
                    surface = Color(0xFFFFF1F2),
                    onSurface = Color(0xFF240D18),
                    surfaceVariant = Color(0xFFFCE7F3),
                    onSurfaceVariant = Color(0xFF831843),
                    outline = Color(0xFFF472B6),
                    outlineVariant = Color(0xFFFBCFE8),
                    background = Color(0xFFFFF0F5),
                    onBackground = Color(0xFF240D18)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFF472B6),
                    onPrimary = Color(0xFF500724),
                    primaryContainer = Color(0xFF831843),
                    onPrimaryContainer = Color(0xFFFCE7F3),
                    secondary = Color(0xFF38BDF8),
                    onSecondary = Color(0xFF082F49),
                    secondaryContainer = Color(0xFF075985),
                    onSecondaryContainer = Color(0xFFE0F2FE),
                    tertiary = Color(0xFFA78BFA),
                    onTertiary = Color(0xFF3B0764),
                    surface = Color(0xFF240D18),
                    onSurface = Color(0xFFFFF1F2),
                    surfaceVariant = Color(0xFF361424),
                    onSurfaceVariant = Color(0xFFF472B6),
                    outline = Color(0xFFBE185D),
                    outlineVariant = Color(0xFF831843),
                    background = Color(0xFF14050D),
                    onBackground = Color(0xFFFFF1F2)
                ),
                shapes = roundedPlayfulShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFFBE185D), Color(0xFF8B5CF6)))
            )

            ThemeChoice.GRADIENT_MESH -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF6366F1),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFE0E7FF),
                    onPrimaryContainer = Color(0xFF1E1B4B),
                    secondary = Color(0xFFEC4899),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFFCE7F3),
                    onSecondaryContainer = Color(0xFF500724),
                    tertiary = Color(0xFF8B5CF6),
                    onTertiary = Color.White,
                    surface = Color(0xFFFAF5FF),
                    onSurface = Color(0xFF13112E),
                    surfaceVariant = Color(0xFFE0E7FF),
                    onSurfaceVariant = Color(0xFF3730A3),
                    outline = Color(0xFFA5B4FC),
                    outlineVariant = Color(0xFFC7D2FE),
                    background = Color(0xFFF5F3FF),
                    onBackground = Color(0xFF13112E)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFF818CF8),
                    onPrimary = Color(0xFF1E1B4B),
                    primaryContainer = Color(0xFF3730A3),
                    onPrimaryContainer = Color(0xFFE0E7FF),
                    secondary = Color(0xFFF472B6),
                    onSecondary = Color(0xFF500724),
                    secondaryContainer = Color(0xFF831843),
                    onSecondaryContainer = Color(0xFFFCE7F3),
                    tertiary = Color(0xFFA78BFA),
                    onTertiary = Color(0xFF3B0764),
                    surface = Color(0xFF13112E),
                    onSurface = Color(0xFFFAF5FF),
                    surfaceVariant = Color(0xFF1E1B45),
                    onSurfaceVariant = Color(0xFF818CF8),
                    outline = Color(0xFF6366F1),
                    outlineVariant = Color(0xFF3730A3),
                    background = Color(0xFF09071E),
                    onBackground = Color(0xFFFAF5FF)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899)))
            )

            ThemeChoice.DUAL_TONE -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF1E293B),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFF1F5F9),
                    onPrimaryContainer = Color(0xFF0F172A),
                    secondary = Color(0xFFFB923C),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFFFEDD5),
                    onSecondaryContainer = Color(0xFF431407),
                    tertiary = Color(0xFF0284C7),
                    onTertiary = Color.White,
                    surface = Color(0xFFFFFFFF),
                    onSurface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFFF1F5F9),
                    onSurfaceVariant = Color(0xFF475569),
                    outline = Color(0xFF94A3B8),
                    outlineVariant = Color(0xFFCBD5E1),
                    background = Color(0xFFF8FAFC),
                    onBackground = Color(0xFF0F172A)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFE2E8F0),
                    onPrimary = Color(0xFF0F172A),
                    primaryContainer = Color(0xFF334155),
                    onPrimaryContainer = Color(0xFFF1F5F9),
                    secondary = Color(0xFFF97316),
                    onSecondary = Color(0xFF431407),
                    secondaryContainer = Color(0xFF7C2D12),
                    onSecondaryContainer = Color(0xFFFFEDD5),
                    tertiary = Color(0xFF38BDF8),
                    onTertiary = Color(0xFF082F49),
                    surface = Color(0xFF1E293B),
                    onSurface = Color(0xFFF8FAFC),
                    surfaceVariant = Color(0xFF2A374A),
                    onSurfaceVariant = Color(0xFF94A3B8),
                    outline = Color(0xFF64748B),
                    outlineVariant = Color(0xFF334155),
                    background = Color(0xFF0F172A),
                    onBackground = Color(0xFFF8FAFC)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFFFB923C)))
            )

            ThemeChoice.CARD_DASHBOARD -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF2563EB),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFDBEAFE),
                    onPrimaryContainer = Color(0xFF1E3A8A),
                    secondary = Color(0xFF475569),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFF1F5F9),
                    onSecondaryContainer = Color(0xFF0F172A),
                    tertiary = Color(0xFF0284C7),
                    onTertiary = Color.White,
                    surface = Color(0xFFFFFFFF),
                    onSurface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFFF1F5F9),
                    onSurfaceVariant = Color(0xFF475569),
                    outline = Color(0xFFCBD5E1),
                    outlineVariant = Color(0xFFE2E8F0),
                    background = Color(0xFFF1F5F9),
                    onBackground = Color(0xFF0F172A)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFF60A5FA),
                    onPrimary = Color(0xFF1E3A8A),
                    primaryContainer = Color(0xFF1D4ED8),
                    onPrimaryContainer = Color(0xFFDBEAFE),
                    secondary = Color(0xFF94A3B8),
                    onSecondary = Color(0xFF0F172A),
                    secondaryContainer = Color(0xFF334155),
                    onSecondaryContainer = Color(0xFFF1F5F9),
                    tertiary = Color(0xFF38BDF8),
                    onTertiary = Color(0xFF082F49),
                    surface = Color(0xFF1E293B),
                    onSurface = Color(0xFFF8FAFC),
                    surfaceVariant = Color(0xFF2A374A),
                    onSurfaceVariant = Color(0xFF94A3B8),
                    outline = Color(0xFF475569),
                    outlineVariant = Color(0xFF334155),
                    background = Color(0xFF0F172A),
                    onBackground = Color(0xFFF8FAFC)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF38BDF8)))
            )

            ThemeChoice.M3_DARK -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = lightColorScheme(
                    primary = Color(0xFF6366F1),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFE0E7FF),
                    onPrimaryContainer = Color(0xFF1E1B4B),
                    secondary = Color(0xFF06B6D4),
                    onSecondary = Color.White,
                    secondaryContainer = Color(0xFFCFFAFE),
                    onSecondaryContainer = Color(0xFF164E63),
                    tertiary = Color(0xFF8B5CF6),
                    onTertiary = Color.White,
                    surface = Color(0xFFF8FAFC),
                    onSurface = Color(0xFF0F172A),
                    surfaceVariant = Color(0xFFE2E8F0),
                    onSurfaceVariant = Color(0xFF475569),
                    outline = Color(0xFF94A3B8),
                    outlineVariant = Color(0xFFCBD5E1),
                    background = Color(0xFFF1F5F9),
                    onBackground = Color(0xFF0F172A)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFFA5B4FC),
                    onPrimary = Color(0xFF1E1B4B),
                    primaryContainer = Color(0xFF312E81),
                    onPrimaryContainer = Color(0xFFE0E7FF),
                    secondary = Color(0xFFC7D2FE),
                    onSecondary = Color(0xFF1E1B4B),
                    secondaryContainer = Color(0xFF1E293B),
                    onSecondaryContainer = Color(0xFFC7D2FE),
                    tertiary = Color(0xFFC4B5FD),
                    onTertiary = Color(0xFF3B0764),
                    surface = Color(0xFF121212),
                    onSurface = Color(0xFFE2E2E2),
                    surfaceVariant = Color(0xFF1E1E1E),
                    onSurfaceVariant = Color(0xFFA0A0A0),
                    outline = Color(0xFF444444),
                    outlineVariant = Color(0xFF2A2A2A),
                    background = Color(0xFF000000),
                    onBackground = Color(0xFFE2E2E2)
                ),
                shapes = standardShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF818CF8), Color(0xFFA5B4FC)))
            )

            ThemeChoice.CYBERNETIC_STRIDE -> AppThemeSpec(
                id = choice,
                displayName = choice.displayName,
                lightColors = darkColorScheme(
                    primary = Color(0xFF00F0FF),
                    onPrimary = Color(0xFF00363A),
                    primaryContainer = Color(0xFF004D54),
                    onPrimaryContainer = Color(0xFFDBFCFF),
                    secondary = Color(0xFF4DFFB2),
                    onSecondary = Color(0xFF003822),
                    secondaryContainer = Color(0xFF005F3D),
                    onSecondaryContainer = Color(0xFFB4FFD8),
                    tertiary = Color(0xFF7B61FF),
                    onTertiary = Color(0xFF1E0066),
                    surface = Color(0xFF171B26),
                    onSurface = Color(0xFFDFE2F1),
                    surfaceVariant = Color(0xFF262A35),
                    onSurfaceVariant = Color(0xFFB9CACB),
                    outline = Color(0xFF4D5E7A),
                    outlineVariant = Color(0xFF2E384B),
                    background = Color(0xFF0F131D),
                    onBackground = Color(0xFFDFE2F1)
                ),
                darkColors = darkColorScheme(
                    primary = Color(0xFF00F0FF),
                    onPrimary = Color(0xFF00363A),
                    primaryContainer = Color(0xFF004D54),
                    onPrimaryContainer = Color(0xFFDBFCFF),
                    secondary = Color(0xFF4DFFB2),
                    onSecondary = Color(0xFF003822),
                    secondaryContainer = Color(0xFF005F3D),
                    onSecondaryContainer = Color(0xFFB4FFD8),
                    tertiary = Color(0xFF7B61FF),
                    onTertiary = Color(0xFF1E0066),
                    surface = Color(0xFF171B26),
                    onSurface = Color(0xFFDFE2F1),
                    surfaceVariant = Color(0xFF262A35),
                    onSurfaceVariant = Color(0xFFB9CACB),
                    outline = Color(0xFF4D5E7A),
                    outlineVariant = Color(0xFF2E384B),
                    background = Color(0xFF0F131D),
                    onBackground = Color(0xFFDFE2F1)
                ),
                shapes = technicalSharpShapes,
                homeProgressGradient = Brush.horizontalGradient(listOf(Color(0xFF00F0FF), Color(0xFF4DFFB2))),
                background = cyberneticStrideBg,
                darkBackground = cyberneticStrideBg
            )
        }
    }
}
