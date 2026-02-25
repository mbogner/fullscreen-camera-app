package dev.mbo.androidcamera.ui

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Test

class NavigationTargetsTest {

    @Test
    fun cameraRouteStoresBackLensFacing() {
        val camera = Camera(lensFacing = 1) // LENS_FACING_BACK
        assertEquals(1, camera.lensFacing)
    }

    @Test
    fun cameraRouteStoresFrontLensFacing() {
        val camera = Camera(lensFacing = 0) // LENS_FACING_FRONT
        assertEquals(0, camera.lensFacing)
    }

    @Test
    fun cameraRouteEquality() {
        val a = Camera(lensFacing = 1)
        val b = Camera(lensFacing = 1)
        assertEquals(a, b)
    }

    @Test
    fun cameraRouteInequality() {
        val back = Camera(lensFacing = 1)
        val front = Camera(lensFacing = 0)
        assertNotEquals(back, front)
    }

    @Test
    fun cameraRouteSerializationRoundTrip() {
        val original = Camera(lensFacing = 0)
        val json = Json.encodeToString(Camera.serializer(), original)
        val restored = Json.decodeFromString(Camera.serializer(), json)
        assertEquals(original, restored)
    }

    @Test
    fun startIsSingleton() {
        assertSame(Start, Start)
    }
}
