package dev.mbo.androidcamera.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dev.mbo.androidcamera.ui.NavigationTargets

class StartViewModel(
    private val navController: NavController
) : ViewModel() {

    fun startCameraButtonClicked() {
        navController.navigate(NavigationTargets.CAMERA)
    }

}