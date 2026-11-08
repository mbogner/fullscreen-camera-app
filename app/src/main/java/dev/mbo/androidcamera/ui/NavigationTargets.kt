package dev.mbo.androidcamera.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// data object: NavEntry contentKey defaults to toString(), which must be stable across process death.
@Serializable
data object Start : NavKey

@Serializable
data class Camera(
    val lensFacing: Int,
    val logicalCameraId: String? = null,
    val physicalCameraId: String? = null
) : NavKey
