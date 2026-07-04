package dev.mbo.androidcamera.ui.screens

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Detects a two-finger pinch and reports each gesture step's relative scale to [onZoom].
 *
 * The value passed to [onZoom] is the per-event zoom multiplier from [detectTransformGestures]
 * (>1 = fingers moving apart / zoom in, <1 = fingers moving together / zoom out), which maps
 * directly onto CameraX's multiplicative zoom ratio. Extracted as a standalone modifier so the
 * gesture wiring can be instrumented without binding a real camera (see PinchToZoomTest).
 */
fun Modifier.pinchToZoom(onZoom: (Float) -> Unit): Modifier = this.pointerInput(Unit) {
    detectTransformGestures { _, _, zoom, _ -> onZoom(zoom) }
}
