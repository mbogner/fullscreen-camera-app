package dev.mbo.androidcamera.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mbo.androidcamera.ui.screens.CameraScreen
import dev.mbo.androidcamera.ui.screens.StartScreen
import dev.mbo.androidcamera.ui.viewmodels.StartViewModelProvider

@Composable
fun NavigationHost() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = NavigationTargets.START) {
        composable(NavigationTargets.START) {
            StartScreen(viewModel = viewModel(factory = StartViewModelProvider(navController)))
        }
        composable(NavigationTargets.CAMERA) {
            CameraScreen(viewModel = viewModel())
        }
    }
}
