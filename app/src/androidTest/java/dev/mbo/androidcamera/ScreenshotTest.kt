package dev.mbo.androidcamera

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun startScreenBackCamera() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())

        // Grant permission button may appear first — if so, click it
        try {
            composeTestRule.onNodeWithText("Grant Permission").performClick()
            composeTestRule.waitForIdle()
        } catch (_: AssertionError) {
            // Permission already granted
        }

        // Wait for the start screen to render
        composeTestRule.waitForIdle()
        Screengrab.screenshot("1_start_back")
    }

    @Test
    fun startScreenFrontCamera() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())

        // Grant permission button may appear first — if so, click it
        try {
            composeTestRule.onNodeWithText("Grant Permission").performClick()
            composeTestRule.waitForIdle()
        } catch (_: AssertionError) {
            // Permission already granted
        }

        // Select front camera
        composeTestRule.onNodeWithText("Front").performClick()
        composeTestRule.waitForIdle()
        Screengrab.screenshot("2_start_front")
    }
}
