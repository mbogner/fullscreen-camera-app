package dev.mbo.androidcamera.ui.viewmodels

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dev.mbo.androidcamera.utils.CameraSizeUtil

class HomeViewModel : ViewModel() {
    private var cameraSize: Size? = null

    fun initializeCamera(
        previewView: PreviewView,
        context: Context
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraSize = CameraSizeUtil.getMaxSize(context)!!

                val preview = Preview.Builder()
                    .setTargetResolution(cameraSize!!)
                    .build()
                    .also {
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
                    Log.e(TAG, "Use case binding failed", exc)
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }

}