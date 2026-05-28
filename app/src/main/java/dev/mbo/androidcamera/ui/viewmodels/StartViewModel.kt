package dev.mbo.androidcamera.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dev.mbo.androidcamera.utils.CameraSizeUtil
import dev.mbo.androidcamera.utils.CameraSizeUtil.BackCameraInfo

class StartViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val availableBackCameras: List<BackCameraInfo> = CameraSizeUtil.listBackCameras(application)

    var selectedLensFacing by mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
        private set

    var selectedBackCamera by mutableStateOf(resolveInitialBackCamera())
        private set

    fun onCameraLensFacingChanged(useFrontCamera: Boolean) {
        selectedLensFacing = if (useFrontCamera) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    fun onBackCameraSelected(key: String) {
        val match = availableBackCameras.firstOrNull { it.key == key } ?: return
        selectedBackCamera = match
        prefs.edit().putString(KEY_BACK_CAMERA, key).apply()
    }

    private fun resolveInitialBackCamera(): BackCameraInfo? {
        val stored = prefs.getString(KEY_BACK_CAMERA, null)
        return availableBackCameras.firstOrNull { it.key == stored }
            ?: availableBackCameras.firstOrNull()
    }

    companion object {
        private const val PREFS_NAME = "camera_prefs"
        private const val KEY_BACK_CAMERA = "back_camera_key"
    }
}
