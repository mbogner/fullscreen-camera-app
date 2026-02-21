package dev.mbo.androidcamera.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import dev.mbo.androidcamera.ui.NavigationTargets

class StartViewModel(
    private val navController: NavController
) : ViewModel() {

    var selectedLensFacing by mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
        private set

    fun onCameraLensFacingChanged(useFrontCamera: Boolean) {
        selectedLensFacing = if (useFrontCamera) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    fun startCameraButtonClicked() {
        navController.navigate(NavigationTargets.cameraRoute(selectedLensFacing))
    }

}
