package dev.mbo.androidcamera.ui

object NavigationTargets {

    const val START = "startScreen"
    const val CAMERA_LENS_FACING_ARG = "lensFacing"
    const val CAMERA = "cameraScreen/{$CAMERA_LENS_FACING_ARG}"

    fun cameraRoute(lensFacing: Int): String {
        return "cameraScreen/$lensFacing"
    }
}
