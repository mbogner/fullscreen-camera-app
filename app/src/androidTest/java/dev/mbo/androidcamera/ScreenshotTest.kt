package dev.mbo.androidcamera

import android.Manifest
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val localeTestRule = LocaleTestRule()
    }

    // Granted before the activity launches; otherwise the system permission dialog covers the
    // activity and the compose hierarchy is never found.
    @get:Rule(order = 0)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun startScreenBackCamera() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())

        composeTestRule.waitForIdle()
        Screengrab.screenshot("1_start_back")
    }

    @Test
    fun startScreenFrontCamera() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())

        // Select front camera
        composeTestRule.onNodeWithText("Front").performClick()
        composeTestRule.waitForIdle()
        Screengrab.screenshot("2_start_front")
    }
}
