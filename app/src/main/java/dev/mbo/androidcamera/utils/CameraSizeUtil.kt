package dev.mbo.androidcamera.utils

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Size
import androidx.camera.core.CameraSelector

object CameraSizeUtil {

    fun getMaxSize(context: Context): Size? {
        val cameraManager =
            context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull {
            cameraManager.getCameraCharacteristics(it)
                .get(CameraCharacteristics.LENS_FACING) == CameraSelector.LENS_FACING_BACK
        }
        val characteristics = cameraManager.getCameraCharacteristics(cameraId!!)
        val streamConfigurationMap =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        return streamConfigurationMap?.getOutputSizes(ImageFormat.JPEG)
            ?.maxByOrNull { it.width * it.height }
    }

}