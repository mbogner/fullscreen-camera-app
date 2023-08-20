package dev.mbo.androidcamera.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastHelper {

    private val mainHandler = Handler(Looper.getMainLooper())

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        mainHandler.post {
            Toast.makeText(context, message, duration).show()
        }
    }
}