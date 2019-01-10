package com.forreview.database.converter

import androidx.room.TypeConverter
import com.forreview.model.entity.StageEntity

class StageStatusConverter {

    @TypeConverter
    fun fromDb(value: String): StageEntity.Status {
        return StageEntity.Status.valueOf(value)
    }

    @TypeConverter
    fun toDb(value: StageEntity.Status): String {
        return value.name
    }
}