package dev.mbo.androidcamera.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.camera.core.CameraSelector
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mbo.androidcamera.ui.viewmodels.StartViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun allElementsDisplayed() {
        val viewModel = StartViewModel(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            StartScreen(viewModel = viewModel, onStartCamera = {})
        }

        composeTestRule.onNodeWithText("Fullscreen USB StreamCam").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Front").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Useful Links").assertIsDisplayed()
        composeTestRule.onNodeWithText("Privacy Policy").assertIsDisplayed()
        composeTestRule.onNodeWithText("buy me a coffee").assertIsDisplayed()
    }

    @Test
    fun backIsSelectedByDefault() {
        val viewModel = StartViewModel(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            StartScreen(viewModel = viewModel, onStartCamera = {})
        }

        composeTestRule.onNodeWithText("Back").assertIsSelected()
        composeTestRule.onNodeWithText("Front").assertIsNotSelected()
    }

    @Test
    fun clickFrontSelectsFrontCamera() {
        val viewModel = StartViewModel(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            StartScreen(viewModel = viewModel, onStartCamera = {})
        }

        composeTestRule.onNodeWithText("Front").performClick()

        composeTestRule.onNodeWithText("Front").assertIsSelected()
        composeTestRule.onNodeWithText("Back").assertIsNotSelected()
        assertEquals(CameraSelector.LENS_FACING_FRONT, viewModel.selectedLensFacing)
    }

    @Test
    fun clickBackAfterFrontSwitchesBack() {
        val viewModel = StartViewModel(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            StartScreen(viewModel = viewModel, onStartCamera = {})
        }

        composeTestRule.onNodeWithText("Front").performClick()
        composeTestRule.onNodeWithText("Back").performClick()

        composeTestRule.onNodeWithText("Back").assertIsSelected()
        assertEquals(CameraSelector.LENS_FACING_BACK, viewModel.selectedLensFacing)
    }

    @Test
    fun startButtonInvokesCallback() {
        var callbackInvoked = false
        val viewModel = StartViewModel(ApplicationProvider.getApplicationContext())
        composeTestRule.setContent {
            StartScreen(viewModel = viewModel, onStartCamera = { callbackInvoked = true })
        }

        composeTestRule.onNodeWithText("Start Camera").performClick()

        assertTrue("onStartCamera callback should have been invoked", callbackInvoked)
    }
}
