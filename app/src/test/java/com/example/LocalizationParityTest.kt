package com.example

import android.content.Context
import android.view.View
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.domain.content.AchievementRegistry
import com.example.domain.content.DestinationRegistry
import com.example.domain.content.MultiplierRegistry
import com.example.sensor.SensorMode
import com.example.ui.theme.ThemeChoice
import com.example.ui.theme.tokens.AppDimensions
import com.example.ui.theme.tokens.AppShapes
import com.example.ui.theme.tokens.AppSpacing
import com.example.util.AppLanguage
import com.example.util.LocaleHelper
import com.example.util.LocalizationUtil
import com.example.util.MetricFormatter
import com.example.util.UiText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalizationParityTest {

    private val baseContext: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun testAllAchievementsHaveValidStringResources() {
        val achievements = AchievementRegistry.allAchievements
        assertEquals(16, achievements.size)

        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        achievements.forEach { achievement ->
            assertNotEquals("Achievement ${achievement.id} missing titleRes", 0, achievement.titleRes)
            assertNotEquals("Achievement ${achievement.id} missing requirementRes", 0, achievement.requirementRes)

            val enTitle = enContext.getString(achievement.titleRes)
            val arTitle = arContext.getString(achievement.titleRes)
            assertTrue("EN title should not be blank for ${achievement.id}", enTitle.isNotBlank())
            assertTrue("AR title should not be blank for ${achievement.id}", arTitle.isNotBlank())
            assertNotEquals("AR and EN titles should differ for ${achievement.id}", enTitle, arTitle)

            val enReq = enContext.getString(achievement.requirementRes)
            val arReq = arContext.getString(achievement.requirementRes)
            assertTrue("EN requirement should not be blank for ${achievement.id}", enReq.isNotBlank())
            assertTrue("AR requirement should not be blank for ${achievement.id}", arReq.isNotBlank())
        }
    }

    @Test
    fun testAllDestinationsHaveValidStringResources() {
        val destinations = DestinationRegistry.destinations
        assertEquals(12, destinations.size)

        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        destinations.forEach { dest ->
            assertNotEquals("Destination ${dest.id} missing nameRes", 0, dest.nameRes)
            assertNotEquals("Destination ${dest.id} missing descriptionRes", 0, dest.descriptionRes)

            val enName = enContext.getString(dest.nameRes)
            val arName = arContext.getString(dest.nameRes)
            assertTrue("EN name should not be blank for ${dest.id}", enName.isNotBlank())
            assertTrue("AR name should not be blank for ${dest.id}", arName.isNotBlank())
            assertNotEquals("AR and EN names should differ for ${dest.id}", enName, arName)

            val enDesc = enContext.getString(dest.descriptionRes)
            val arDesc = arContext.getString(dest.descriptionRes)
            assertTrue("EN description should not be blank for ${dest.id}", enDesc.isNotBlank())
            assertTrue("AR description should not be blank for ${dest.id}", arDesc.isNotBlank())
        }
    }

    @Test
    fun testAllMultiplierTiersHaveValidStringResources() {
        val tiers = MultiplierRegistry.tiers
        assertEquals(12, tiers.size)

        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        tiers.forEach { tier ->
            assertNotEquals("Streak tier ${tier.multiplierLabel} missing titleRes", 0, tier.titleRes)

            val enTitle = enContext.getString(tier.titleRes)
            val arTitle = arContext.getString(tier.titleRes)
            assertTrue("EN title should not be blank for ${tier.multiplierLabel}", enTitle.isNotBlank())
            assertTrue("AR title should not be blank for ${tier.multiplierLabel}", arTitle.isNotBlank())
        }
    }

    @Test
    fun testAllThemeChoicesHaveValidStringResources() {
        val choices = ThemeChoice.entries
        assertEquals(17, choices.size)

        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        choices.forEach { choice ->
            assertNotEquals("Theme ${choice.name} missing displayNameRes", 0, choice.displayNameRes)
            assertNotEquals("Theme ${choice.name} missing subtitleRes", 0, choice.subtitleRes)

            val enName = enContext.getString(choice.displayNameRes)
            val arName = arContext.getString(choice.displayNameRes)
            assertTrue("EN theme name should not be blank for ${choice.name}", enName.isNotBlank())
            assertTrue("AR theme name should not be blank for ${choice.name}", arName.isNotBlank())
        }
    }

    @Test
    fun testAllSensorModesHaveValidStringResources() {
        val modes = SensorMode.entries
        assertEquals(4, modes.size)

        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        modes.forEach { mode ->
            assertNotEquals("Sensor mode ${mode.name} missing displayNameRes", 0, mode.displayNameRes)

            val enName = enContext.getString(mode.displayNameRes)
            val arName = arContext.getString(mode.displayNameRes)
            assertTrue("EN sensor name should not be blank for ${mode.name}", enName.isNotBlank())
            assertTrue("AR sensor name should not be blank for ${mode.name}", arName.isNotBlank())
        }
    }

    @Test
    fun testLocaleHelperWrappingAndRtlDetection() {
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)
        val arConfig = arContext.resources.configuration

        assertEquals("ar", arConfig.locales[0].language)
        assertEquals(View.LAYOUT_DIRECTION_RTL, arConfig.layoutDirection)

        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val enConfig = enContext.resources.configuration

        assertEquals("en", enConfig.locales[0].language)
        assertEquals(View.LAYOUT_DIRECTION_LTR, enConfig.layoutDirection)
    }

    @Test
    fun testCarryingModeAndConfidenceResolvers() {
        val modes = listOf("pocket", "hand", "bag", "backpack", "wearable", "treadmill")
        modes.forEach { mode ->
            val resId = LocalizationUtil.getCarryingModeRes(mode)
            assertNotEquals("Carrying mode $mode should map to valid resource", 0, resId)
            val text = baseContext.getString(resId)
            assertTrue(text.isNotBlank())
        }

        val confidences = listOf("HIGH", "MEDIUM", "LOW")
        confidences.forEach { conf ->
            val resId = LocalizationUtil.getConfidenceBadgeRes(conf)
            assertNotEquals("Confidence $conf should map to valid resource", 0, resId)
            val text = baseContext.getString(resId)
            assertTrue(text.isNotBlank())
        }
    }

    @Test
    fun testDesignTokensConsistency() {
        val spacing = AppSpacing()
        assertEquals(0.dp, spacing.none)
        assertEquals(8.dp, spacing.sm)
        assertEquals(16.dp, spacing.lg)
        assertEquals(32.dp, spacing.xxxl)

        val dimensions = AppDimensions()
        assertEquals(48.dp, dimensions.minTouchTarget)
        assertEquals(48.dp, dimensions.buttonHeight)
        assertEquals(16.dp, dimensions.iconSmall)
        assertEquals(72.dp, dimensions.scrubberTrackHeight)

        val shapes = AppShapes()
        assertNotNull(shapes.small)
        assertNotNull(shapes.medium)
        assertNotNull(shapes.large)
        assertNotNull(shapes.bottomSheet)
    }

    @Test
    fun testUiTextResolution() {
        val dynamic = UiText.DynamicString("StepTrack Test")
        assertEquals("StepTrack Test", dynamic.asString(baseContext))

        val resText = UiText.StringResource(R.string.app_name)
        val resolved = resText.asString(baseContext)
        assertEquals("TrueStep", resolved)
    }

    @Test
    fun testMetricFormatterConsistency() {
        val enLocale = Locale.US
        assertEquals("10,000", MetricFormatter.formatSteps(10000, enLocale))
        assertEquals("5.20", MetricFormatter.formatDistance(5.2f, enLocale))
        assertEquals("350", MetricFormatter.formatCalories(350, enLocale))
        assertEquals("85%", MetricFormatter.formatPercentage(85, enLocale))
        assertEquals("01:15:30", MetricFormatter.formatDuration(4530L, enLocale))
        assertEquals("15:30", MetricFormatter.formatDuration(930L, enLocale))
    }

    @Test
    fun testMotivationalQuotesParity() {
        val quotes = com.example.domain.content.MotivationalQuotesRegistry.quotes
        assertEquals(6, quotes.size)

        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        quotes.forEach { quote ->
            val enText = enContext.getString(quote.textRes)
            val arText = arContext.getString(quote.textRes)

            assertTrue("EN quote text should not be blank for quote ${quote.id}", enText.isNotBlank())
            assertTrue("AR quote text should not be blank for quote ${quote.id}", arText.isNotBlank())
            assertNotEquals("AR and EN quote text should differ for quote ${quote.id}", enText, arText)
        }
    }

    @Test
    fun testSplashScreenStringsParity() {
        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        val splashKeys = listOf(
            R.string.splash_app_title,
            R.string.splash_tagline,
            R.string.splash_quote_label,
            R.string.splash_loading_status,
            R.string.splash_logo_content_desc,
            R.string.settings_replay_onboarding_title,
            R.string.settings_replay_onboarding_sub
        )

        splashKeys.forEach { resId ->
            val enText = enContext.getString(resId)
            val arText = arContext.getString(resId)

            assertTrue("EN string should not be blank for resource $resId", enText.isNotBlank())
            assertTrue("AR string should not be blank for resource $resId", arText.isNotBlank())
            assertNotEquals("AR and EN string should differ for resource $resId", enText, arText)
        }
    }

    @Test
    fun testPrivacyPolicyStringsParity() {
        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        val privacyKeys = listOf(
            R.string.privacy_policy_title,
            R.string.privacy_policy_sub,
            R.string.privacy_policy_body,
            R.string.onboarding_sensor_perm_title,
            R.string.onboarding_sensor_perm_desc,
            R.string.onboarding_allow_btn,
            R.string.read_privacy_policy
        )

        privacyKeys.forEach { resId ->
            val enText = enContext.getString(resId)
            val arText = arContext.getString(resId)

            assertTrue("EN string should not be blank for resource $resId", enText.isNotBlank())
            assertTrue("AR string should not be blank for resource $resId", arText.isNotBlank())
            assertNotEquals("AR and EN string should differ for resource $resId", enText, arText)
        }
    }

    @Test
    fun testCyberneticStrideStringsParity() {
        val enContext = LocaleHelper.wrap(baseContext, AppLanguage.ENGLISH)
        val arContext = LocaleHelper.wrap(baseContext, AppLanguage.ARABIC)

        val cyberneticKeys = listOf(
            R.string.theme_cybernetic_stride_title,
            R.string.theme_cybernetic_stride_desc,
            R.string.hero_title,
            R.string.hero_subtitle,
            R.string.hero_mana_overdrive,
            R.string.active_loadout_title,
            R.string.storage_vault_title,
            R.string.vault_filter_all,
            R.string.vault_filter_footwear,
            R.string.vault_filter_cores,
            R.string.vault_filter_boosts,
            R.string.vault_filter_materials,
            R.string.gear_neon_phantom_title,
            R.string.gear_zephyr_engine_title,
            R.string.gear_nanoweave_vest_title,
            R.string.gear_chrono_watch_title,
            R.string.hud_cadence_supercharged,
            R.string.hud_hold_to_complete,
            R.string.hud_encounter_title,
            R.string.raid_grade_s,
            R.string.raid_grade_a,
            R.string.raid_grade_b,
            R.string.raid_overdrive_master,
            R.string.raid_claim_rewards,
            R.string.home_aura_synced,
            R.string.home_aura_overdrive_active,
            R.string.home_cadence_insight
        )

        cyberneticKeys.forEach { resId ->
            val enText = enContext.getString(resId)
            val arText = arContext.getString(resId)

            assertTrue("EN string should not be blank for resource $resId", enText.isNotBlank())
            assertTrue("AR string should not be blank for resource $resId", arText.isNotBlank())
            assertNotEquals("AR and EN string should differ for resource $resId", enText, arText)
        }
    }
}
