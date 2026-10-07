package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.VoiceCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app_name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KARAN VOICE AI", appName)
  }

  @Test
  fun `verify voice catalog contains core voices`() {
    val voices = VoiceCatalog.allVoices
    assertTrue(voices.isNotEmpty())
    assertNotNull(VoiceCatalog.findById("hindi_male"))
    assertNotNull(VoiceCatalog.findById("hindi_female"))
    assertNotNull(VoiceCatalog.findById("english_male"))
    assertNotNull(VoiceCatalog.findById("english_female"))
    assertNotNull(VoiceCatalog.findById("deep_male"))
    assertNotNull(VoiceCatalog.findById("energetic_male"))
    assertNotNull(VoiceCatalog.findById("calm_male"))
    assertNotNull(VoiceCatalog.findById("storytelling"))
    assertNotNull(VoiceCatalog.findById("youtube_gaming"))
  }
}
