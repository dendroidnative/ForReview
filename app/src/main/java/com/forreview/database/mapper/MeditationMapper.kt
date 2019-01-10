package com.forreview.database.mapper

import android.database.Cursor
import com.forreview.database.converter.AudioPathConverter
import com.forreview.database.converter.ExplanationConverter
import com.forreview.model.Meditation
import com.forreview.model.entity.MeditationEntity

class MeditationMapper(
    private val stageMapper: StageMapper,
    private val audioMapper: AudioMapper,
    private val explanationMapper: ExplanationMapper
) {

    fun listFromCursor(cursor: Cursor): MutableList<Meditation> {
        val list = mutableListOf<Meditation>()
        cursor.moveToFirst()
        do {
            list.add(fromCursor(cursor))
        } while (cursor.moveToNext())

        return list
    }

    fun toEntityList(meditationList: MutableList<Meditation>): MutableList<MeditationEntity> {
        val meditationListEntity = mutableListOf<MeditationEntity>()
        meditationList.forEach { meditation ->
            meditationListEntity.add(toEntity(meditation))
        }
        return meditationListEntity
    }

    fun fromCursor(cursor: Cursor): Meditation {
        val preMeditationEntityList =
            ExplanationConverter().fromDb(cursor.getString(cursor.getColumnIndex("preMeditateList")))
        val postMeditationEntityList =
            ExplanationConverter().fromDb(cursor.getString(cursor.getColumnIndex("postMeditateList")))
        val audioEntityList =
            AudioPathConverter().fromDb(cursor.getString(cursor.getColumnIndex("meditationAudioList")))

        return Meditation(
            id = cursor.getString(cursor.getColumnIndex("meditationId")),
            dayNumber = cursor.getInt(cursor.getColumnIndex("meditationDayNumber")),
            audioList = audioMapper.fromEntityList(audioEntityList),
            preMeditateList = explanationMapper.fromEntityList(preMeditationEntityList),
            postMeditateList = explanationMapper.fromEntityList(postMeditationEntityList),
            isCompleted = cursor.getInt(cursor.getColumnIndex("isMeditationCompleted")) != 0,
            timeCompletedMillis = cursor.getLong(cursor.getColumnIndex("timeCompletedMillis")),
            duration = cursor.getLong(cursor.getColumnIndex("duration")),
            stage = stageMapper.fromCursor(cursor),
            notification = cursor.getString(cursor.getColumnIndex("notification")),
            image = cursor.getString(cursor.getColumnIndex("image")),
            completedImage = cursor.getString(cursor.getColumnIndex("completedImage")),
            title = cursor.getString(cursor.getColumnIndex("title"))
        )
    }

    fun toEntity(meditation: Meditation): MeditationEntity {
        return MeditationEntity(
            dayNumber = meditation.dayNumber,
            audioList = audioMapper.toEntityList(meditation.audioList),
            preMeditateList = explanationMapper.toEntityList(meditation.preMeditateList),
            postMeditateList = explanationMapper.toEntityList(meditation.postMeditateList),
            isCompleted = meditation.isCompleted(),
            timeCompletedMillis = meditation.timeCompletedMillis,
            stageId = meditation.stage.id,
            duration = meditation.duration,
            notification = meditation.notification,
            image = meditation.image,
            completedImage = meditation.completedImage,
            title = meditation.title,
            id = meditation.id
        )
    }
}