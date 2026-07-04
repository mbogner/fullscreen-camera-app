package dev.mbo.androidcamera.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies the [Modifier.pinchToZoom] gesture wiring: a two-finger pinch reports relative scale
 * factors whose product is >1 when fingers spread apart (zoom in) and <1 when they close together
 * (zoom out). Uses a plain tagged Box so no camera hardware/permission is required.
 */
@RunWith(AndroidJUnit4::class)
class PinchToZoomTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val tag = "pinchTarget"

    @Test
    fun pinchingOutZoomsIn() {
        var accumulated = 1f
        composeTestRule.setContent {
            androidx.compose.foundation.layout.Box(
                Modifier.fillMaxSize().testTag(tag).pinchToZoom { accumulated *= it }
            )
        }

        // Both fingers start near the center and move outward -> zoom in.
        composeTestRule.onNodeWithTag(tag).performTouchInput {
            pinch(
                start0 = Offset(centerX - 50f, centerY),
                end0 = Offset(centerX - 300f, centerY),
                start1 = Offset(centerX + 50f, centerY),
                end1 = Offset(centerX + 300f, centerY),
            )
        }

        assertTrue("spreading fingers should accumulate zoom > 1 (got $accumulated)", accumulated > 1f)
    }

    @Test
    fun pinchingInZoomsOut() {
        var accumulated = 1f
        composeTestRule.setContent {
            androidx.compose.foundation.layout.Box(
                Modifier.fillMaxSize().testTag(tag).pinchToZoom { accumulated *= it }
            )
        }

        // Both fingers start far apart and move inward -> zoom out.
        composeTestRule.onNodeWithTag(tag).performTouchInput {
            pinch(
                start0 = Offset(centerX - 300f, centerY),
                end0 = Offset(centerX - 50f, centerY),
                start1 = Offset(centerX + 300f, centerY),
                end1 = Offset(centerX + 50f, centerY),
            )
        }

        assertTrue("closing fingers should accumulate zoom < 1 (got $accumulated)", accumulated < 1f)
    }
}
