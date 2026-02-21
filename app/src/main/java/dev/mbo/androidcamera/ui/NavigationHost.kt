package dev.mbo.androidcamera.ui

import androidx.compose.runtime.Composable
import androidx.camera.core.CameraSelector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        composable(
            route = NavigationTargets.CAMERA,
            arguments = listOf(
                navArgument(NavigationTargets.CAMERA_LENS_FACING_ARG) {
                    type = NavType.IntType
                    defaultValue = CameraSelector.LENS_FACING_BACK
                }
            )
        ) { backStackEntry ->
            val lensFacing = backStackEntry.arguments?.getInt(NavigationTargets.CAMERA_LENS_FACING_ARG)
                ?: CameraSelector.LENS_FACING_BACK
            CameraScreen(viewModel = viewModel(), lensFacing = lensFacing)
        }
    }
}
