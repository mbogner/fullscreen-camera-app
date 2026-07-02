package dev.mbo.androidcamera.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mbo.androidcamera.utils.CameraSizeUtil
import dev.mbo.androidcamera.utils.CameraSizeUtil.BackCameraInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartViewModel(application: Application) : AndroidViewModel(application) {

    var availableBackCameras by mutableStateOf<List<BackCameraInfo>>(emptyList())
        private set

    var selectedBackCamera by mutableStateOf<BackCameraInfo?>(null)
        private set

    var selectedLensFacing by mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
        private set

    init {
        // Camera enumeration (binder IPC per logical/physical camera) and the first
        // SharedPreferences read (disk I/O) must not run on the main thread, or they stall the
        // first frame and risk an ANR. Load off-main and publish results as Compose state.
        viewModelScope.launch {
            val (cameras, initial) = withContext(Dispatchers.IO) {
                val app = getApplication<Application>()
                val cams = CameraSizeUtil.listBackCameras(app)
                val stored = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getString(KEY_BACK_CAMERA, null)
                cams to (cams.firstOrNull { it.key == stored } ?: cams.firstOrNull())
            }
            availableBackCameras = cameras
            selectedBackCamera = initial
        }
    }

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
        viewModelScope.launch(Dispatchers.IO) {
            getApplication<Application>()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit { putString(KEY_BACK_CAMERA, key) }
        }
    }

    companion object {
        private const val PREFS_NAME = "camera_prefs"
        private const val KEY_BACK_CAMERA = "back_camera_key"
    }
}
