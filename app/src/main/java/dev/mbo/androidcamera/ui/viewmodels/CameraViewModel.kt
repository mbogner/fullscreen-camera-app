package dev.mbo.androidcamera.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dev.mbo.androidcamera.utils.CameraSizeUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraViewModel : ViewModel() {

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    @Volatile
    private var cameraProvider: ProcessCameraProvider? = null

    fun initializeCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        lensFacing: Int
    ) {
        val appContext: Context = previewView.context.applicationContext
        val cameraProviderFuture = ProcessCameraProvider.getInstance(appContext)
        cameraProviderFuture.addListener(
            {
                val provider = try {
                    cameraProviderFuture.get()
                } catch (e: Exception) {
                    Log.e(TAG, "ProcessCameraProvider.getInstance failed", e)
                    return@addListener
                }
                cameraProvider = provider

                val cameraSelector = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                val cameraSize = CameraSizeUtil.getMaxSize(appContext, lensFacing)

                val previewBuilder = Preview.Builder()
                if (cameraSize != null) {
                    val resolutionSelector = ResolutionSelector.Builder()
                        .setResolutionStrategy(
                            ResolutionStrategy(
                                cameraSize,
                                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                            )
                        )
                        .build()
                    previewBuilder.setResolutionSelector(resolutionSelector)
                }

                val preview = previewBuilder.build()

                previewView.post {
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    try {
                        provider.unbindAll()
                        provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG, "binding failed: invalid selector/use case", e)
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "binding failed: lifecycle state", e)
                    }
                }
            },
            cameraExecutor
        )
    }

    fun release() {
        cameraProvider?.unbindAll()
        cameraProvider = null
    }

    override fun onCleared() {
        release()
        cameraExecutor.shutdown()
        super.onCleared()
    }

    companion object {
        private const val TAG = "CameraViewModel"
    }
}
