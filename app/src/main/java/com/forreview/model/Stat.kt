package com.forreview.model

import java.util.concurrent.TimeUnit

data class Stat(
    val totalMeditations: Int,
    val longestStreak: Int,
    val vibe: String,
    val achievementIconResId: Int,
    val streakIconResId: Int,
    val totalMeditationTimeMillis: Long
) {
    fun getTotalTimeString(): String {
        return String.format("%d h, %d min",
            TimeUnit.MILLISECONDS.toHours(totalMeditationTimeMillis),
            (TimeUnit.MILLISECONDS.toMinutes(totalMeditationTimeMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalMeditationTimeMillis)))
        )
    }
}