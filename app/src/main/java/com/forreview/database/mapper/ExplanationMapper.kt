package com.forreview.database.mapper

import com.forreview.model.Meditation
import com.forreview.model.entity.MeditationEntity

class ExplanationMapper {

    fun fromEntityList(explanationEntityList: List<MeditationEntity.Explanation>): List<Meditation.Explanation> {
        val explanationList = mutableListOf<Meditation.Explanation>()
        explanationEntityList.forEach { entity ->
            explanationList.add(fromEntity(entity))
        }
        return explanationList
    }

    fun toEntityList(explanationList: List<Meditation.Explanation>): List<MeditationEntity.Explanation> {
        val explanationEntityList = mutableListOf<MeditationEntity.Explanation>()
        explanationList.forEach { explanation ->
            explanationEntityList.add(toEntity(explanation))
        }
        return explanationEntityList
    }

    fun fromEntity(entity: MeditationEntity.Explanation): Meditation.Explanation {
        val items = mutableListOf<Meditation.Explanation.Item>()
        entity.items.forEach { itemEntity ->
            if (itemEntity is MeditationEntity.Explanation.TextItem) {
                items.add(Meditation.Explanation.TextItem(itemEntity.text))
            } else if (itemEntity is MeditationEntity.Explanation.ImageItem) {
                items.add(Meditation.Explanation.ImageItem(itemEntity.image))
            }
        }

        return Meditation.Explanation(items)
    }

    fun toEntity(explanation: Meditation.Explanation): MeditationEntity.Explanation {
        val items = mutableListOf<MeditationEntity.Explanation.Item>()
        explanation.items.forEach { item ->
            if (item is Meditation.Explanation.TextItem) {
                items.add(MeditationEntity.Explanation.TextItem(item.text))
            } else if (item is Meditation.Explanation.ImageItem) {
                items.add(MeditationEntity.Explanation.ImageItem(item.image))
            }
        }
        return MeditationEntity.Explanation(items)
    }
}