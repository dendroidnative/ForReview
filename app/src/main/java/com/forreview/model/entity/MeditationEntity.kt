package com.forreview.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = StageEntity::class,
            parentColumns = arrayOf("stageId"),
            childColumns = arrayOf("stageId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class MeditationEntity(

    @ColumnInfo(name = "meditationDayNumber")
    @SerializedName("dayNumber")
    val dayNumber: Int,

    @ColumnInfo(name = "meditationAudioList")
    @SerializedName("audioList")
    val audioList: List<Audio>,

    @ColumnInfo(name = "preMeditateList")
    @SerializedName("preMeditateList")
    val preMeditateList: List<Explanation>,

    @ColumnInfo(name = "postMeditateList")
    @SerializedName("postMeditateList")
    val postMeditateList: List<Explanation>,

    @ColumnInfo(name = "isMeditationCompleted")
    @SerializedName("isCompleted")
    val isCompleted: Boolean,

    @ColumnInfo(name = "timeCompletedMillis")
    @SerializedName("timeCompletedMillis")
    val timeCompletedMillis: Long,

    @ColumnInfo(name = "duration")
    @SerializedName("duration")
    val duration: Long,

    @ColumnInfo(name = "stageId")
    @SerializedName("stageId")
    val stageId: String,

    @ColumnInfo(name = "notification")
    @SerializedName("notification")
    val notification: String,

    @ColumnInfo(name = "image")
    @SerializedName("image")
    val image: String,

    @ColumnInfo(name = "completedImage")
    @SerializedName("completedImage")
    val completedImage: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "meditationId")
    @SerializedName("title")
    val id: String = UUID.randomUUID().toString()
) {

    data class Explanation( //Has Converter
        val items: List<Item>
    ) {
        abstract class Item

        data class TextItem(val text: String) : Item()

        data class ImageItem(val image: String) : Item()
    }

    data class Audio(
        val path: String,
        val durationMillis: Long
    )
}