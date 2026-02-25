package dev.mbo.androidcamera.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dev.mbo.androidcamera.utils.CameraSizeUtil

class CameraViewModel : ViewModel() {

    fun initializeCamera(
        previewView: PreviewView,
        context: Context,
        lensFacing: Int
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                val cameraSize = CameraSizeUtil.getMaxSize(context, lensFacing)

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

                val preview = previewBuilder.build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "binding failed", exc)
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    companion object {
        private const val TAG = "CameraViewModel"
    }
}
