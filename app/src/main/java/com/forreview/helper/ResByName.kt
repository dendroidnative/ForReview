package com.forreview.helper

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.google.android.vending.expansion.downloader.Helpers
import java.io.File

object ResByName {

    fun drawable(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "drawable",  context.packageName)
    }

    fun raw(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "raw",  context.packageName)
    }

    fun expansionFileUri(context: Context, name: String): Uri {
        return File(getExpansionFilesDir(context), name).toUri()
//        return Uri.parse("android.resource://${context.packageName}/${raw(context, name)}")
    }

    fun isExpansionFilesDelivered(context: Context): Boolean {
        for (xf in xAPKS) {
            val fileName = Helpers.getExpansionAPKFileName(context, xf.mIsMain, xf.mFileVersion)
            val doesFileExist = Helpers.doesFileExist(context, fileName, xf.mFileSize, false)
            return doesFileExist
        }
        return true
    }

    fun isExpansionFilesUnpacked(context: Context): Boolean {
        getExpansionFilesDir(context)?.listFiles { dir, name ->
            if (name.contains(".m4a") or name.contains(".mp4")) {
                return@listFiles true
            }
            return@listFiles false
        }?.also { list ->
            if (list.size == 70) {
                return true
            }
        }

        return false
    }

    //todo change size of obb

    private fun getExpansionFilesDir(context: Context): File? {
        val file = File(context.getExternalFilesDir(null), "media")
        return if (file.exists()) {
            file
        } else {
            null
        }
    }

    /**
     * Here is where you place the data that the validator will use to determine
     * if the file was delivered correctly. This is encoded in the source code
     * so the application can easily determine whether the file has been
     * properly delivered without having to talk to the server. If the
     * application is using LVL for licensing, it may make sense to eliminate
     * these checks and to just rely on the server.
     */
    val xAPKS = arrayOf(
        XAPKFile(
            true, // true signifies a main file
            25, // the version of the APK that the file was uploaded
            // against
            659_386_510L // the length of the file in bytes
        )/*, XAPKFile(
                false, // false signifies a patch file
                1, // the version of the APK that the patch file was uploaded
                // against
                512860L // the length of the patch file in bytes
            )*/
    )


    /**
     * This is a little helper class that demonstrates simple testing of an
     * Expansion APK file delivered by Market. You may not wish to hard-code
     * things such as file lengths into your executable... and you may wish to
     * turn this code off during application development.
     */
    class XAPKFile internal constructor(val mIsMain: Boolean, val mFileVersion: Int, val mFileSize: Long)
}