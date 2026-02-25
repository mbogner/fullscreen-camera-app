package dev.mbo.androidcamera.utils

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraSizeUtilTest {

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun clearCache() {
        val field = CameraSizeUtil::class.java.getDeclaredField("sizeCache")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val cache = field.get(CameraSizeUtil) as MutableMap<Int, Size?>
        cache.clear()
    }

    @Test
    fun backCameraReturnsNonNullSize() {
        val size = CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_BACK)
        assertNotNull(size)
    }

    @Test
    fun backCameraReturnsSensibleDimensions() {
        val size = CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_BACK)!!
        assertTrue("Width should be > 0", size.width > 0)
        assertTrue("Height should be > 0", size.height > 0)
        assertTrue("Width should be at least VGA (640)", size.width >= 640)
        assertTrue("Height should be at least VGA (480)", size.height >= 480)
    }

    @Test
    fun cachingReturnsSameInstance() {
        val first = CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_BACK)
        val second = CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_BACK)
        assertSame(first, second)
    }

    @Test
    fun perLensCaching() {
        val back = CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_BACK)
        val front = CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_FRONT)
        // Both should be cached independently — calling again should return same instances
        assertSame(back, CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_BACK))
        assertSame(front, CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_FRONT))
    }

    @Test
    fun defaultParameterUsesBackCamera() {
        val withDefault = CameraSizeUtil.getMaxSize(context)
        val withExplicit = CameraSizeUtil.getMaxSize(context, CameraSelector.LENS_FACING_BACK)
        assertEquals(withDefault, withExplicit)
    }
}
