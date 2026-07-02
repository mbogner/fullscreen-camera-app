package dev.mbo.androidcamera.utils

import dev.mbo.androidcamera.utils.CameraSizeUtil.BackCameraInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pure-logic unit tests for [CameraSizeUtil]. The camera-enumeration paths need a device
 * (see the instrumented CameraSizeUtilTest); these cover the device-independent math and
 * key formatting that back the picker labels.
 */
class CameraSizeUtilLogicTest {

    // --- equivalentFocalLength35mm ---

    @Test
    fun `full-frame sensor gives crop factor 1`() {
        // 36mm x 24mm ≈ the 35mm reference diagonal → equivalent == physical focal length.
        assertEquals(50, CameraSizeUtil.equivalentFocalLength35mm(50f, 36f, 24f))
    }

    @Test
    fun `applies crop factor for a small sensor`() {
        // diagonal = sqrt(6^2 + 8^2) = 10 → crop = 43.27/10 = 4.327 → 10 * 4.327 = 43.27
        assertEquals(43, CameraSizeUtil.equivalentFocalLength35mm(10f, 6f, 8f))
    }

    @Test
    fun `typical phone main sensor rounds to a sane wide-angle`() {
        // diagonal = 8.0 (6.4 x 4.8) → crop 5.40875 → 5.44 * 5.40875 ≈ 29.4
        assertEquals(29, CameraSizeUtil.equivalentFocalLength35mm(5.44f, 6.4f, 4.8f))
    }

    @Test
    fun `zero sensor size returns null instead of dividing by zero`() {
        assertNull(CameraSizeUtil.equivalentFocalLength35mm(5f, 0f, 0f))
    }

    // --- BackCameraInfo.key ---

    @Test
    fun `key of a logical-only camera is the logical id`() {
        assertEquals("0", BackCameraInfo("0", physicalCameraId = null, focalLengthMm = 24).key)
    }

    @Test
    fun `key of a physical camera combines logical and physical ids`() {
        assertEquals("0|2", BackCameraInfo("0", physicalCameraId = "2", focalLengthMm = 13).key)
    }
}
