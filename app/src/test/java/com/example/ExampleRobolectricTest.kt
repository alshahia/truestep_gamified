package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.ThemeChoice
import com.example.ui.theme.ThemeRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TrueStep", appName)
  }

  @Test
  fun `verify theme registry has 17 themes`() {
    assertEquals(17, ThemeChoice.values().size)
    ThemeChoice.values().forEach { choice ->
      val spec = ThemeRegistry.getThemeSpec(choice)
      assertNotNull(spec)
      assertEquals(choice, spec.id)
    }
  }
}
