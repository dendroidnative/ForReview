package com.forreview.model

import org.apache.commons.lang3.time.DateUtils
import java.util.*
import kotlin.math.roundToLong

data class Meditation(

    val id: String,

    val dayNumber: Int,

    val audioList: List<Audio>,

    val preMeditateList: List<Explanation>,

    val postMeditateList: List<Explanation>,

    private var isCompleted: Boolean,

    var timeCompletedMillis: Long,

    var duration: Long,

    var notification: String,

    var image: String,

    val completedImage: String,

    var title: String,

    var stage: Stage
) {
    private var selectedAudio: Audio? = null

    fun getSelectedAudio(): Audio {
        return selectedAudio ?: audioList.first()
    }

    fun setSelectedAudio(audio: Audio) {
        this.selectedAudio = audio
        this.duration = audio.duration
    }

    fun isActive(): Boolean {
        return stage.getStatus() == Stage.Status.ACTIVE && stage.getCurrentDayNumber() == dayNumber
    }

    fun setCompleted() {
        isCompleted = true
        timeCompletedMillis = System.currentTimeMillis()
    }

    fun isCompleted() = isCompleted

    fun unComplete() {
        isCompleted = false
        timeCompletedMillis = 0
    }

    fun isCompletedAtLeastYesterdayRelativeTo(nextCompletedTimeMillis: Long): Boolean {
        val current = Calendar.getInstance().apply {
            timeInMillis = timeCompletedMillis
        }
        val next = Calendar.getInstance().apply {
            timeInMillis = nextCompletedTimeMillis
        }
        if (DateUtils.isSameDay(current, next)) {
            return true
        } else {
            current.add(Calendar.DAY_OF_MONTH, 1)
            if (DateUtils.isSameDay(current, next)) {
                return true
            }
        }
        return false
    }

    data class Audio(
        val path: String,
        val duration: Long
    ) {
        fun getDurationMin(): Long {
            return (duration.toFloat() / 1000.toFloat() / 60.toFloat()).roundToLong()
        }
    }

    data class Explanation(
        val items: List<Item>
    ) {
        abstract class Item

        data class TextItem(val text: String) : Item()

        data class ImageItem(val image: String) : Item()
    }
}