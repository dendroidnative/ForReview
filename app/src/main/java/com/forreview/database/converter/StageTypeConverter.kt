package com.forreview.database.converter

import androidx.room.TypeConverter
import com.forreview.model.entity.StageEntity

class StageTypeConverter {

    @TypeConverter
    fun fromDb(value: String): StageEntity.Type {
        return StageEntity.Type.valueOf(value)
    }

    @TypeConverter
    fun toDb(value: StageEntity.Type): String {
        return value.name
    }
}