package com.forreview.database.mapper

import com.forreview.model.Meditation
import com.forreview.model.entity.MeditationEntity

class AudioMapper {

    fun fromEntityList(audioEntityList: List<MeditationEntity.Audio>): List<Meditation.Audio> {
        val audioList = mutableListOf<Meditation.Audio>()
        audioEntityList.forEach { entity ->
            audioList.add(fromEntity(entity))
        }
        return audioList
    }

    fun toEntityList(audioList: List<Meditation.Audio>): List<MeditationEntity.Audio> {
        val audioListEntity = mutableListOf<MeditationEntity.Audio>()
        audioList.forEach { entity ->
            audioListEntity.add(toAudioEntity(entity))
        }
        return audioListEntity
    }

    fun fromEntity(audioEntity: MeditationEntity.Audio): Meditation.Audio {
        return Meditation.Audio(
            path = audioEntity.path,
            duration = audioEntity.durationMillis
        )
    }

    fun toAudioEntity(audio: Meditation.Audio): MeditationEntity.Audio {
        return MeditationEntity.Audio(
            path = audio.path,
            durationMillis = audio.duration
        )
    }
}