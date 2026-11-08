package dev.mbo.androidcamera.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.InitializationException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mbo.androidcamera.utils.CameraSizeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel : ViewModel() {

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var targetRotation: Int? = null
    private var initJob: Job? = null

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)

    /** Latest preview surface request, rendered by `CameraXViewfinder` in the UI. */
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    @OptIn(ExperimentalCamera2Interop::class)
    fun initializeCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        lensFacing: Int,
        logicalCameraId: String? = null,
        physicalCameraId: String? = null
    ) {
        initJob?.cancel()
        val appContext = context.applicationContext

        initJob = viewModelScope.launch {
            val provider = try {
                ProcessCameraProvider.awaitInstance(appContext)
            } catch (e: InitializationException) {
                Log.e(TAG, "ProcessCameraProvider init failed", e)
                return@launch
            }

            val cameraSize = withContext(Dispatchers.IO) {
                CameraSizeUtil.getMaxSize(appContext, lensFacing, logicalCameraId, physicalCameraId)
            }

            val selector = buildCameraSelector(lensFacing, logicalCameraId)

            val previewBuilder = Preview.Builder().apply {
                targetRotation?.let { setTargetRotation(it) }
                cameraSize?.let { size ->
                    setResolutionSelector(
                        ResolutionSelector.Builder()
                            .setResolutionStrategy(
                                ResolutionStrategy(
                                    size,
                                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                                )
                            )
                            .build()
                    )
                }
            }

            if (physicalCameraId != null) {
                try {
                    Camera2Interop.Extender(previewBuilder).setPhysicalCameraId(physicalCameraId)
                } catch (e: Throwable) {
                    Log.w(TAG, "setPhysicalCameraId failed for $physicalCameraId", e)
                }
            }

            val preview = previewBuilder.build()
                .also { it.setSurfaceProvider { request -> _surfaceRequest.value = request } }

            this@CameraViewModel.preview = preview
            cameraProvider = provider
            try {
                provider.unbindAll()
                camera = provider.bindToLifecycle(lifecycleOwner, selector, preview)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "binding failed: invalid selector/use case", e)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "binding failed: lifecycle state", e)
            }
        }
    }

    @OptIn(ExperimentalCamera2Interop::class)
    private fun buildCameraSelector(lensFacing: Int, logicalCameraId: String?): CameraSelector {
        if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            return CameraSelector.DEFAULT_FRONT_CAMERA
        }
        if (logicalCameraId == null) {
            return CameraSelector.DEFAULT_BACK_CAMERA
        }
        return CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .addCameraFilter { infos ->
                filterByLogicalId(infos, logicalCameraId) { Camera2CameraInfo.from(it).cameraId }
            }
            .build()
    }

    /**
     * Applies a pinch gesture's relative scale [scaleFactor] to the current camera zoom, clamped to
     * the sensor's supported range. No-op until a camera is bound or if zoom state is unavailable.
     *
     * `detectTransformGestures` also reports single-finger pans with [scaleFactor] == 1f; those are
     * ignored so a plain drag doesn't spam redundant `setZoomRatio` calls.
     */
    fun onPinchZoom(scaleFactor: Float) {
        if (scaleFactor == 1f) return
        val cam = camera ?: return
        val zoom = cam.cameraInfo.zoomState.value ?: return
        cam.cameraControl.setZoomRatio(
            computeZoomRatio(zoom.zoomRatio, scaleFactor, zoom.minZoomRatio, zoom.maxZoomRatio)
        )
    }

    /**
     * Keeps the preview's output transform in sync with the display. The activity handles
     * orientation changes itself (no recreation) and, unlike PreviewView, CameraXViewfinder relies
     * solely on [Preview.setTargetRotation], so this must be called whenever the display rotates.
     */
    fun onDisplayRotationChanged(rotation: Int) {
        targetRotation = rotation
        preview?.targetRotation = rotation
    }

    fun release() {
        initJob?.cancel()
        initJob = null
        cameraProvider?.unbindAll()
        cameraProvider = null
        camera = null
        preview = null
        _surfaceRequest.value = null
    }

    override fun onCleared() {
        release()
    }

    companion object {
        private const val TAG = "CameraViewModel"
    }
}

/**
 * Returns cameras whose id equals [logicalCameraId]; falls back to all [infos] when none match.
 *
 * Always returns a fresh MUTABLE list — CameraX's `CameraSelector.filter()` calls `retainAll()`
 * on the returned list, which throws `UnsupportedOperationException` on an unmodifiable list.
 */
internal fun <T> filterByLogicalId(
    infos: List<T>,
    logicalCameraId: String,
    idOf: (T) -> String,
): List<T> {
    val match = infos.filter { idOf(it) == logicalCameraId }
    return if (match.isNotEmpty()) match.toMutableList() else infos.toMutableList()
}

/**
 * New zoom ratio for a pinch gesture: the [current] ratio scaled by [scaleFactor], clamped to the
 * sensor's supported [[min], [max]] range. Kept as a pure, framework-free function so the zoom math
 * is unit-testable without a bound camera (see CameraZoomTest).
 */
internal fun computeZoomRatio(current: Float, scaleFactor: Float, min: Float, max: Float): Float =
    (current * scaleFactor).coerceIn(min, max)
