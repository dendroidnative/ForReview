package com.forreview.model

data class StageJson(
    val title: String,
    val subtitle: String,
    val type: String,
    val startIcon: String,
    val imageBackground: String,
    val videoBackground: String,
    val meditations: List<MeditationJson>
) {
    data class MeditationJson(
        val audio: List<AudioJson>,
        val preMeditate: String,
        val postMeditate: String,
        val notification: String?,
        val image: String?,
        val completedImage: String?,
        val title: String?
    ) {
        data class AudioJson(
            val path: String,
            val durationMillis: Long
        )
    }
}