package dev.mbo.androidcamera.ui.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for [computeZoomRatio], the pure zoom math behind the pinch-to-zoom gesture.
 *
 * A pinch step multiplies the current zoom ratio by the gesture's relative scale factor; the result
 * is clamped to the sensor's supported [min, max] range so a fast/large pinch can never drive the
 * camera outside its zoom limits.
 */
class CameraZoomTest {

    @Test
    fun `scale of one leaves the ratio unchanged`() {
        assertEquals(2.0f, computeZoomRatio(current = 2.0f, scaleFactor = 1.0f, min = 1.0f, max = 10.0f), 0.0001f)
    }

    @Test
    fun `zoom in multiplies within range`() {
        assertEquals(4.0f, computeZoomRatio(current = 2.0f, scaleFactor = 2.0f, min = 1.0f, max = 10.0f), 0.0001f)
    }

    @Test
    fun `zoom out reduces the ratio`() {
        assertEquals(1.5f, computeZoomRatio(current = 3.0f, scaleFactor = 0.5f, min = 1.0f, max = 10.0f), 0.0001f)
    }

    @Test
    fun `result is clamped to max when the pinch would exceed it`() {
        assertEquals(10.0f, computeZoomRatio(current = 8.0f, scaleFactor = 5.0f, min = 1.0f, max = 10.0f), 0.0001f)
    }

    @Test
    fun `result is clamped to min when the pinch would fall below it`() {
        assertEquals(1.0f, computeZoomRatio(current = 1.5f, scaleFactor = 0.1f, min = 1.0f, max = 10.0f), 0.0001f)
    }

    @Test
    fun `already at max, further zoom in stays at max`() {
        assertEquals(10.0f, computeZoomRatio(current = 10.0f, scaleFactor = 2.0f, min = 1.0f, max = 10.0f), 0.0001f)
    }
}
