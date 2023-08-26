package dev.mbo.androidcamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.mbo.androidcamera.ui.NavigationHost
import dev.mbo.androidcamera.ui.screens.StartScreen
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
            NavigationHost()
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
    }
}