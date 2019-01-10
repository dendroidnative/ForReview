package com.forreview.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.forreview.database.converter.AudioPathConverter
import com.forreview.database.converter.ExplanationConverter
import com.forreview.database.converter.StageStatusConverter
import com.forreview.database.converter.StageTypeConverter
import com.forreview.model.entity.MeditationEntity
import com.forreview.model.entity.StageEntity

@Database(entities = [MeditationEntity::class, StageEntity::class], version = 8)
@TypeConverters(
    ExplanationConverter::class,
    StageStatusConverter::class,
    AudioPathConverter::class,
    StageTypeConverter::class)
abstract class MyDatabase: RoomDatabase() {

    abstract fun meditationDao(): MeditationDao

    abstract fun stageDao(): StagesDao
}