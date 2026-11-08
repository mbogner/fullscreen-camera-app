package dev.mbo.androidcamera.ui

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.mbo.androidcamera.ui.screens.CameraScreen
import dev.mbo.androidcamera.ui.screens.StartScreen
import dev.mbo.androidcamera.ui.viewmodels.StartViewModel

@Composable
fun NavigationHost() {
    val backStack = rememberNavBackStack(Start)
    val activity = LocalActivity.current
    val showsCamera = backStack.lastOrNull() is Camera

    // The menu is laid out for portrait only; the camera preview follows the device rotation.
    // Keyed on the committed back stack (not on composition) so a predictive-back peek at the
    // menu doesn't rotate the screen mid-gesture.
    LaunchedEffect(activity, showsCamera) {
        activity?.requestedOrientation = if (showsCamera) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Start> {
                val viewModel: StartViewModel = viewModel()
                StartScreen(
                    viewModel = viewModel,
                    onStartCamera = {
                        // Ignore repeated taps while the camera screen is already on top.
                        if (backStack.lastOrNull() == Start) {
                            val isBack = viewModel.selectedLensFacing == CameraSelector.LENS_FACING_BACK
                            val selected = viewModel.selectedBackCamera.takeIf { isBack }
                            backStack.add(
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
            entry<Camera> { camera ->
                CameraScreen(
                    viewModel = viewModel(),
                    lensFacing = camera.lensFacing,
                    logicalCameraId = camera.logicalCameraId,
                    physicalCameraId = camera.physicalCameraId
                )
            }
        }
    )
}
