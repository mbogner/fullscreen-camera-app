package dev.mbo.androidcamera.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun startScreenIsInitialDestination() {
        composeTestRule.setContent {
            NavigationHost()
        }

        composeTestRule.onNodeWithText("Fullscreen USB StreamCam").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Camera").assertIsDisplayed()
    }

    @Test
    fun clickStartCameraNavigatesAwayFromStartScreen() {
        composeTestRule.setContent {
            NavigationHost()
        }

        composeTestRule.onNodeWithText("Start Camera").performClick()

        composeTestRule.onNodeWithText("Start Camera").assertDoesNotExist()
    }
}
