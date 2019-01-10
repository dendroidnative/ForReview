package com.forreview.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class StageEntity(

    @ColumnInfo(name = "stageTitle")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "stageSubtitle")
    @SerializedName("subtitle")
    val subtitle: String,

    @ColumnInfo(name = "stageType")
    @SerializedName("type")
    val type: Type,

    @ColumnInfo(name = "stageStartIcon")
    @SerializedName("startMeditationIconPath")
    val startMeditationIconPath: String,

    @ColumnInfo(name = "stageStaticBgPath")
    @SerializedName("staticBgPath")
    val staticBgPath: String,

    @ColumnInfo(name = "stageDynamicBgPath")
    @SerializedName("dynamicBgPath")
    val dynamicBgPath: String,

    @ColumnInfo(name = "stageStatus")
    @SerializedName("stageStatus")
    val status: Status,

    @ColumnInfo(name = "currentDayNumber")
    @SerializedName("currentDayNumber")
    val currentDayNumber: Int,

    @ColumnInfo(name = "daysCount")
    @SerializedName("daysCount")
    val daysCount: Int,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "stageId")
    @SerializedName("title")
    val id: String = UUID.randomUUID().toString()
) {

    enum class Status {
        BLOCKED, COMPLETED, ACTIVE
    }

    enum class Type {
        PAID, FREE
    }
}