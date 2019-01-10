package com.forreview.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import timber.log.Timber
import java.lang.Exception

object AudioUtils {

    fun getDuration(context: Context, uri: Uri): Long {
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, uri)
            val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            return durationStr.toLong()
        } catch (exc: Exception) {
            Timber.e(exc)
        }

        return 0L
    }
}