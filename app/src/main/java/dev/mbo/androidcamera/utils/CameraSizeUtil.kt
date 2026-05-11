package dev.mbo.androidcamera.utils

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import java.util.concurrent.ConcurrentHashMap

object CameraSizeUtil {

    private const val TAG = "CameraSizeUtil"
    private val sizeCache = ConcurrentHashMap<Int, Size>()

    fun getMaxSize(context: Context, lensFacing: Int = CameraSelector.LENS_FACING_BACK): Size? {
        sizeCache[lensFacing]?.let { return it }

        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val camera2LensFacing = when (lensFacing) {
            CameraSelector.LENS_FACING_FRONT -> CameraCharacteristics.LENS_FACING_FRONT
            else -> CameraCharacteristics.LENS_FACING_BACK
        }

        val cameraIds = try {
            cameraManager.cameraIdList
        } catch (e: CameraAccessException) {
            Log.w(TAG, "cameraIdList failed", e)
            return null
        }

        val cameraId = cameraIds.firstOrNull { id ->
            try {
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.LENS_FACING) == camera2LensFacing
            } catch (e: CameraAccessException) {
                Log.w(TAG, "getCameraCharacteristics failed for $id", e)
                false
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "getCameraCharacteristics rejected $id", e)
                false
            }
        } ?: return null

        val size = try {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(ImageFormat.JPEG)
                ?.maxByOrNull { it.width * it.height }
        } catch (e: CameraAccessException) {
            Log.w(TAG, "characteristics fetch failed for $cameraId", e)
            null
        }

        if (size != null) {
            sizeCache[lensFacing] = size
        }
        return size
    }
}
