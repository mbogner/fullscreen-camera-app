package dev.mbo.androidcamera.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {

    fun requestIfNeeded(activity: Activity, permission: String, requestCode: Int) {
        if (needsPermission(activity, permission)) {
            requestPermission(
                activity,
                permission,
                requestCode
            )
        }
    }

    private fun needsPermission(activity: Activity, permission: String): Boolean {
        return !hasPermission(activity, permission)
    }

    private fun hasPermission(activity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            requestCode
        )
    }

}