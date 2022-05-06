package com.a2l.software.myapplication

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.a2l.software.myapplication.FileUtils.getPath
import com.a2l.software.myapplication.FileUtils.isLocal
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.channels.FileChannel

object Helper {

    /**
     * Use to check device with camera or connect to an external camera device
     *
     * @return true when camera is active
     */
    fun isCheckCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun getFile(context: Context, uri: Uri?): File? {
        if (uri == null) return null
        val path = getPath(context, uri)
        if (path?.isNotEmpty() == true && isLocal(path)) {
            return File(path)
        }
        return null
    }

    fun copyFile(src: File, dst: File): Boolean {
        if (!src.exists()) return false
        val ins = FileInputStream(src)
        var out: FileOutputStream? = null
        var isSuccess = true

        try {
            out = FileOutputStream(dst)
            val inChannel: FileChannel = ins.channel
            val outChannel: FileChannel = out.channel

            inChannel.transferTo(0, inChannel.size(), outChannel)
            Log.d("TAGs", "dst: ${dst.path}")
        } catch (ex: Exception) {
            Log.d("TAGs", "Exception")
            ex.printStackTrace()
            isSuccess = false
        } finally {
            ins.close()
            out?.close()
        }
        return isSuccess
    }

//    fun
}