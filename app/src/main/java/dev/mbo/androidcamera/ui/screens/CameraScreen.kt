package dev.mbo.androidcamera.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val window = activity?.window ?: return@LaunchedEffect
        WindowCompat.getInsetsController(window, rootView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    LaunchedEffect(lensFacing, logicalCameraId, physicalCameraId) {
        viewModel.initializeCamera(
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            lensFacing = lensFacing,
            logicalCameraId = logicalCameraId,
            physicalCameraId = physicalCameraId
        )
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.release() }
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .pinchToZoom { viewModel.onPinchZoom(it) },
        factory = { previewView }
    )
}
