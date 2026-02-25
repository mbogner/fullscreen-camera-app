package dev.mbo.androidcamera.utils

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Size
import androidx.camera.core.CameraSelector

object CameraSizeUtil {

    private val sizeCache = mutableMapOf<Int, Size?>()

    fun getMaxSize(context: Context, lensFacing: Int = CameraSelector.LENS_FACING_BACK): Size? {
        sizeCache[lensFacing]?.let { return it }

        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val camera2LensFacing = when (lensFacing) {
            CameraSelector.LENS_FACING_FRONT -> CameraCharacteristics.LENS_FACING_FRONT
            else -> CameraCharacteristics.LENS_FACING_BACK
        }

        val cameraId = cameraManager.cameraIdList.firstOrNull {
            cameraManager.getCameraCharacteristics(it)
                .get(CameraCharacteristics.LENS_FACING) == camera2LensFacing
        } ?: return null

        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val streamConfigurationMap =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val size = streamConfigurationMap?.getOutputSizes(ImageFormat.JPEG)
            ?.maxByOrNull { it.width * it.height }

        sizeCache[lensFacing] = size
        return size
    }
}
