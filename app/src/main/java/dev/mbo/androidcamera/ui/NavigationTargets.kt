package dev.mbo.androidcamera.ui

import kotlinx.serialization.Serializable

@Serializable
object Start

@Serializable
data class Camera(val lensFacing: Int)
