package dev.mbo.androidcamera.ui.viewmodels

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mbo.androidcamera.utils.CameraSizeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel : ViewModel() {

    private var cameraProvider: ProcessCameraProvider? = null
    private var initJob: Job? = null

    fun initializeCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        lensFacing: Int
    ) {
        initJob?.cancel()
        val appContext = previewView.context.applicationContext

        initJob = viewModelScope.launch {
            val provider = try {
                withContext(Dispatchers.IO) {
                    ProcessCameraProvider.getInstance(appContext).get()
                }
            } catch (e: Exception) {
                Log.e(TAG, "ProcessCameraProvider.getInstance failed", e)
                return@launch
            }

            val cameraSize = withContext(Dispatchers.IO) {
                CameraSizeUtil.getMaxSize(appContext, lensFacing)
            }

            val selector = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            val preview = Preview.Builder()
                .apply {
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
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            cameraProvider = provider
            try {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "binding failed: invalid selector/use case", e)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "binding failed: lifecycle state", e)
            }
        }
    }

    fun release() {
        initJob?.cancel()
        initJob = null
        cameraProvider?.unbindAll()
        cameraProvider = null
    }

    override fun onCleared() {
        release()
        super.onCleared()
    }

    companion object {
        private const val TAG = "CameraViewModel"
    }
}
