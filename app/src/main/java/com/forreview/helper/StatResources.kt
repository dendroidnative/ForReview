package com.forreview.helper

import android.content.Context
import com.forreview.R

class StatResources(
    private val context: Context
) {

    fun getVibe(completedMeditationCount: Int): String {
        return when (completedMeditationCount) {
            in 0..5 -> context.getString(R.string.stat_vibe_1)
            in 6..10 -> context.getString(R.string.stat_vibe_2)
            in 11..15 -> context.getString(R.string.stat_vibe_3)
            in 16..20 -> context.getString(R.string.stat_vibe_4)
            in 21..25 -> context.getString(R.string.stat_vibe_5)
            in 26..30 -> context.getString(R.string.stat_vibe_6)
            in 31..35 -> context.getString(R.string.stat_vibe_7)
            in 36..40 -> context.getString(R.string.stat_vibe_8)
            in 41..45 -> context.getString(R.string.stat_vibe_9)
            else -> context.getString(R.string.stat_vibe_10)
        }
    }

    fun getAchievementIconResId(completedMeditationCount: Int): Int {
        return when (completedMeditationCount) {
            in 0..10 -> R.drawable.stat_meditation_10
            in 11..25 -> R.drawable.stat_meditation_25
            else -> R.drawable.stat_meditation_50
        }
    }

    fun getStreakIconResId(completedMeditationCount: Int): Int {
        return when (completedMeditationCount) {
            in 0..10 -> R.drawable.stat_meditation_streak_10
            in 11..25 -> R.drawable.stat_meditation_streak_25
            else -> R.drawable.stat_meditation_streak_50
        }
    }
}