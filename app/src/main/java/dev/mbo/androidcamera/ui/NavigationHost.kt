package dev.mbo.androidcamera.ui

import androidx.compose.runtime.Composable
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
                    navController.navigate(Camera(viewModel.selectedLensFacing))
                }
            )
        }
        composable<Camera> { backStackEntry ->
            val camera: Camera = backStackEntry.toRoute()
            CameraScreen(viewModel = viewModel(), lensFacing = camera.lensFacing)
        }
    }
}
