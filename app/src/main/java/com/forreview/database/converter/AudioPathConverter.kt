package com.forreview.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.forreview.model.entity.MeditationEntity
import timber.log.Timber

class AudioPathConverter {

    @TypeConverter
    fun fromDb(list: String): List<MeditationEntity.Audio> {
        try {
            return Gson().fromJson(list, object : TypeToken<MutableList<MeditationEntity.Audio>>() {}.type)
        } catch (exc: Exception) {
            Timber.e(exc)
            //ignore
        }
        return mutableListOf()
    }

    @TypeConverter
    fun toDb(list: List<MeditationEntity.Audio>): String {
        return Gson().toJson(list)
    }
}