package dev.mbo.androidcamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mbo.androidcamera.ui.screens.HomeScreen
import dev.mbo.androidcamera.ui.theme.AndroidCameraTheme
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
            AndroidCameraTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(viewModel = viewModel())
                    }
                }
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
    }
}