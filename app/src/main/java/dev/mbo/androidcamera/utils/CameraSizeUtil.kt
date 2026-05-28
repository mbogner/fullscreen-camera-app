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
import kotlin.math.roundToInt
import kotlin.math.sqrt

object CameraSizeUtil {

    private const val TAG = "CameraSizeUtil"
    private val sizeCache = ConcurrentHashMap<String, Size>()

    data class BackCameraInfo(
        val logicalCameraId: String,
        val physicalCameraId: String?,
        val focalLengthMm: Int?
    ) {
        val key: String get() = if (physicalCameraId != null) "$logicalCameraId|$physicalCameraId" else logicalCameraId
    }

    fun getMaxSize(
        context: Context,
        lensFacing: Int = CameraSelector.LENS_FACING_BACK,
        logicalCameraId: String? = null,
        physicalCameraId: String? = null
    ): Size? {
        val cacheKey = "${logicalCameraId ?: "lens:$lensFacing"}|${physicalCameraId ?: ""}"
        sizeCache[cacheKey]?.let { return it }

        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val resolvedLogical = logicalCameraId ?: findFirstCameraId(cameraManager, lensFacing) ?: return null
        val charsId = physicalCameraId ?: resolvedLogical

        val size = try {
            val characteristics = cameraManager.getCameraCharacteristics(charsId)
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(ImageFormat.JPEG)
                ?.maxByOrNull { it.width * it.height }
        } catch (e: CameraAccessException) {
            Log.w(TAG, "characteristics fetch failed for $charsId", e)
            null
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "characteristics rejected $charsId", e)
            null
        }

        if (size != null) {
            sizeCache[cacheKey] = size
        }
        return size
    }

    fun listBackCameras(context: Context): List<BackCameraInfo> {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraIds = try {
            cameraManager.cameraIdList
        } catch (e: CameraAccessException) {
            Log.w(TAG, "cameraIdList failed", e)
            return emptyList()
        }

        val result = mutableListOf<BackCameraInfo>()

        for (logicalId in cameraIds) {
            val logicalCharacteristics = try {
                cameraManager.getCameraCharacteristics(logicalId)
            } catch (e: CameraAccessException) {
                Log.w(TAG, "getCameraCharacteristics failed for $logicalId", e)
                continue
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "getCameraCharacteristics rejected $logicalId", e)
                continue
            }

            if (logicalCharacteristics.get(CameraCharacteristics.LENS_FACING)
                != CameraCharacteristics.LENS_FACING_BACK
            ) {
                continue
            }

            val physicalIds = try {
                logicalCharacteristics.physicalCameraIds
            } catch (_: Throwable) {
                emptySet<String>()
            }

            if (physicalIds.isNullOrEmpty()) {
                result += BackCameraInfo(
                    logicalCameraId = logicalId,
                    physicalCameraId = null,
                    focalLengthMm = computeEquivalentFocalLength(logicalCharacteristics)
                )
            } else {
                for (physicalId in physicalIds) {
                    val physicalCharacteristics = try {
                        cameraManager.getCameraCharacteristics(physicalId)
                    } catch (e: CameraAccessException) {
                        Log.w(TAG, "getCameraCharacteristics failed for physical $physicalId", e)
                        continue
                    } catch (e: IllegalArgumentException) {
                        Log.w(TAG, "getCameraCharacteristics rejected physical $physicalId", e)
                        continue
                    }
                    result += BackCameraInfo(
                        logicalCameraId = logicalId,
                        physicalCameraId = physicalId,
                        focalLengthMm = computeEquivalentFocalLength(physicalCharacteristics)
                    )
                }
            }
        }

        return result.sortedBy { it.focalLengthMm ?: Int.MAX_VALUE }
    }

    private fun findFirstCameraId(cameraManager: CameraManager, lensFacing: Int): String? {
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

        return cameraIds.firstOrNull { id ->
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
        }
    }

    private fun computeEquivalentFocalLength(characteristics: CameraCharacteristics): Int? {
        val focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
        val focalLength = focalLengths?.firstOrNull() ?: return null
        val sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE) ?: return null
        val sensorDiagonal = sqrt(sensorSize.width * sensorSize.width + sensorSize.height * sensorSize.height)
        if (sensorDiagonal <= 0f) return null
        val diagonal35mm = 43.27f
        val cropFactor = diagonal35mm / sensorDiagonal
        return (focalLength * cropFactor).roundToInt()
    }
}
