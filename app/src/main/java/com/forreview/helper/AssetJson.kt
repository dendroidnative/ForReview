package com.forreview.helper

import android.content.Context
import java.io.IOException

object AssetJson {

    fun get(context: Context, name: String): String? {
        val json: String?
        try {
            val `is` = context.assets.open(name)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}