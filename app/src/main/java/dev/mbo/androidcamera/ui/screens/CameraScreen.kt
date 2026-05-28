package dev.mbo.androidcamera.ui.screens

import android.view.WindowInsetsController
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
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.mbo.androidcamera.ui.viewmodels.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    lensFacing: Int,
    logicalCameraId: String? = null,
    physicalCameraId: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val rootView = LocalView.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        rootView.windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
        modifier = modifier.fillMaxSize(),
        factory = { previewView }
    )
}
