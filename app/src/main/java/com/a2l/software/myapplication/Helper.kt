package com.a2l.software.myapplication

import android.content.Context
import android.content.pm.PackageManager

object Helper {

    /**
     * Use to check device with camera or connect to an external camera device
     *
     * @return true when camera is active
     */
    fun isCheckCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
}