package dev.mbo.androidcamera

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.mbo.androidcamera.ui.screens.HomeScreen
import dev.mbo.androidcamera.utils.PermissionUtil

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionUtil.requestIfNeeded(
            this,
            android.Manifest.permission.CAMERA,
            CAMERA_PERMISSION_REQUEST_CODE
        )

        setContent {
            HomeScreen(viewModel = viewModel())
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
    }
}