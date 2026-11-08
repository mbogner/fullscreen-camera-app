package dev.mbo.androidcamera.ui.screens

import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mbo.androidcamera.ui.viewmodels.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    lensFacing: Int,
    modifier: Modifier = Modifier,
    logicalCameraId: String? = null,
    physicalCameraId: String? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current
    val rootView = LocalView.current
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    // Immersive only while the preview is shown; restore the system bars when leaving the screen.
    DisposableEffect(activity, rootView) {
        val controller = activity?.window?.let { WindowCompat.getInsetsController(it, rootView) }
        controller?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        onDispose { controller?.show(WindowInsetsCompat.Type.systemBars()) }
    }

    // Covers 90° turns (also a configuration change) and 180° flips (display change only).
    DisposableEffect(rootView) {
        val displayManager = context.getSystemService(DisplayManager::class.java)
        fun syncRotation() {
            rootView.display?.let { viewModel.onDisplayRotationChanged(it.rotation) }
        }
        val listener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) = Unit
            override fun onDisplayRemoved(displayId: Int) = Unit
            override fun onDisplayChanged(displayId: Int) {
                if (displayId == rootView.display?.displayId) syncRotation()
            }
        }
        syncRotation()
        displayManager?.registerDisplayListener(listener, Handler(Looper.getMainLooper()))
        onDispose { displayManager?.unregisterDisplayListener(listener) }
    }

    LaunchedEffect(lensFacing, logicalCameraId, physicalCameraId) {
        viewModel.initializeCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            lensFacing = lensFacing,
            logicalCameraId = logicalCameraId,
            physicalCameraId = physicalCameraId
        )
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.release() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            // Matches PreviewView's black backdrop until the first SurfaceRequest arrives.
            .background(Color.Black)
            .pinchToZoom { viewModel.onPinchZoom(it) }
    ) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
