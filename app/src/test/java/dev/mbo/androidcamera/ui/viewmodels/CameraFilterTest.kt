package dev.mbo.androidcamera.ui.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Collections

/**
 * Regression tests for [filterByLogicalId].
 *
 * CameraX hands the [androidx.camera.core.CameraFilter] an unmodifiable list and then calls
 * `retainAll()` on whatever the filter returns (CameraSelector.filter -> retainAll). Returning
 * the unmodifiable input therefore crashes with UnsupportedOperationException, which is exactly
 * the Android Vitals crash this reproduces. The filter must return a list that supports mutation.
 */
class CameraFilterTest {

    @Test
    fun `no match returns all cameras and stays mutable`() {
        val cameras = Collections.unmodifiableList(listOf("0", "1", "2"))
        val result = filterByLogicalId(cameras, logicalCameraId = "99") { it }
        // Must NOT throw UnsupportedOperationException — this is the crash under test.
        (result as MutableList<String>).retainAll(cameras)
        assertEquals(listOf("0", "1", "2"), result)
    }

    @Test
    fun `match returns only the matching camera and stays mutable`() {
        val cameras = Collections.unmodifiableList(listOf("0", "1", "2"))
        val result = filterByLogicalId(cameras, logicalCameraId = "1") { it }
        (result as MutableList<String>).retainAll(cameras) // must NOT throw
        assertEquals(listOf("1"), result)
    }

    @Test
    fun `always returns a fresh list, never the input instance`() {
        val cameras = listOf("0", "1", "2")
        // Both branches must copy — returning the input (unmodifiable at runtime) is the bug.
        assertNotSame(cameras, filterByLogicalId(cameras, logicalCameraId = "1") { it })
        assertNotSame(cameras, filterByLogicalId(cameras, logicalCameraId = "99") { it })
    }

    @Test
    fun `empty input yields an empty mutable list`() {
        val result = filterByLogicalId(emptyList<String>(), logicalCameraId = "1") { it }
        (result as MutableList<String>).retainAll(emptyList()) // must NOT throw
        assertTrue(result.isEmpty())
    }
}
