package com.forreview.database.mapper

import android.database.Cursor
import com.forreview.model.Stage
import com.forreview.model.entity.StageEntity

class StageMapper {

    fun fromEntityList(entityList: List<StageEntity>): MutableList<Stage> {
        val list = mutableListOf<Stage>()
        entityList.forEach { entity ->
            fromEntity(entity)?.also {
                list.add(it)
            }
        }
        return list
    }

    fun toEntityList(stageList: MutableList<Stage>): List<StageEntity> {
        val list = mutableListOf<StageEntity>()
        stageList.forEach { stage ->
            list.add(toEntity(stage))
        }
        return list
    }

    fun fromEntity(entity: StageEntity?): Stage? {
        return if (entity == null) null else
            Stage(
                id = entity.id,
                title = entity.title,
                subtitle = entity.subtitle,
                type = Stage.Type.valueOf(entity.type.name),
                startMeditationIconPath = entity.startMeditationIconPath,
                staticBgPath = entity.staticBgPath,
                dynamicBgPath = entity.dynamicBgPath,
                status = Stage.Status.valueOf(entity.status.name),
                currentDayNumber = entity.currentDayNumber,
                daysCount = entity.daysCount
            )
    }

    fun toEntity(stage: Stage): StageEntity {
        return StageEntity(
            title = stage.title,
            subtitle = stage.subtitle,
            type = StageEntity.Type.valueOf(stage.getType().name),
            startMeditationIconPath = stage.startMeditationIconPath,
            staticBgPath = stage.staticBgPath,
            dynamicBgPath = stage.dynamicBgPath,
            status = StageEntity.Status.valueOf(stage.getStatus().name),
            currentDayNumber = stage.getCurrentDayNumber(),
            daysCount = stage.daysCount,
            id = stage.id
        )
    }

    fun fromCursor(cursor: Cursor): Stage {
        return Stage(
            id = cursor.getString(cursor.getColumnIndex("stageId")),
            title = cursor.getString(cursor.getColumnIndex("stageTitle")),
            subtitle = cursor.getString(cursor.getColumnIndex("stageSubtitle")),
            type = Stage.Type.valueOf(cursor.getString(cursor.getColumnIndex("stageType"))),
            startMeditationIconPath = cursor.getString(cursor.getColumnIndex("stageStartIcon")),
            staticBgPath = cursor.getString(cursor.getColumnIndex("stageStaticBgPath")),
            dynamicBgPath = cursor.getString(cursor.getColumnIndex("stageDynamicBgPath")),
            status = Stage.Status.valueOf(cursor.getString(cursor.getColumnIndex("stageStatus"))),
            currentDayNumber = cursor.getInt(cursor.getColumnIndex("currentDayNumber")),
            daysCount = cursor.getInt(cursor.getColumnIndex("daysCount"))
        )
    }
}