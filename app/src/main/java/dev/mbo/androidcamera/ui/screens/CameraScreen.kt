package dev.mbo.androidcamera.ui.screens

import android.view.WindowInsetsController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import dev.mbo.androidcamera.ui.viewmodels.CameraViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rootView = LocalView.current
    val previewView = remember { PreviewView(context) }
    val windowInsetsController = rootView.windowInsetsController!!

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { previewView }
    ) {
        viewModel.initializeCamera(previewView, context)

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

