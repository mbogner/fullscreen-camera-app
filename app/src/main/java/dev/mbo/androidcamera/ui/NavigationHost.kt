package dev.mbo.androidcamera.ui

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.mbo.androidcamera.ui.screens.CameraScreen
import dev.mbo.androidcamera.ui.screens.StartScreen
import dev.mbo.androidcamera.ui.viewmodels.StartViewModel

@Composable
fun NavigationHost() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Start) {
        composable<Start> {
            val viewModel: StartViewModel = viewModel()
            StartScreen(
                viewModel = viewModel,
                onStartCamera = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        val isBack = viewModel.selectedLensFacing == CameraSelector.LENS_FACING_BACK
                        val selected = viewModel.selectedBackCamera.takeIf { isBack }
                        navController.navigate(
                            Camera(
                                lensFacing = viewModel.selectedLensFacing,
                                logicalCameraId = selected?.logicalCameraId,
                                physicalCameraId = selected?.physicalCameraId
                            )
                        )
                    }
                }
            )
        }
        composable<Camera> { backStackEntry ->
            val camera: Camera = backStackEntry.toRoute()
            CameraScreen(
                viewModel = viewModel(),
                lensFacing = camera.lensFacing,
                logicalCameraId = camera.logicalCameraId,
                physicalCameraId = camera.physicalCameraId
            )
        }
    }
}
