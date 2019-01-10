package com.forreview.database

import android.content.Context
import com.google.gson.Gson
import com.forreview.helper.AssetJson
import com.forreview.helper.ResByName
import com.forreview.model.StageJson
import com.forreview.model.entity.MeditationEntity
import com.forreview.model.entity.StageEntity
import com.forreview.utils.AudioUtils
import timber.log.Timber

class DataInitializer(
    private val context: Context
) {

    fun initMeditationWithStages(): Pair<List<StageEntity>, List<MeditationEntity>> {

        val stageList = mutableListOf<StageEntity>()
        val meditationList = mutableListOf<MeditationEntity>()

        context.assets.list("stages")?.forEachIndexed { stageIndex, stageName ->
            if (stageIndex > context.assets.list("stages").size) return@forEachIndexed
            val stageString = AssetJson.get(context, "stages/$stageName")
            val stageFromJson = Gson().fromJson(stageString, StageJson::class.java)

            val stageStatus = if (stageIndex == 0) {
                StageEntity.Status.ACTIVE
            } else {
                StageEntity.Status.BLOCKED
            }

            val stageType = when (stageFromJson.type) {
                "FREE" -> StageEntity.Type.FREE
                else -> StageEntity.Type.PAID
            }

            val stage = StageEntity(
                title = context.resources.getString(
                    context.resources.getIdentifier(
                        stageFromJson.title,
                        "string",
                        context.packageName
                    )
                ),
                subtitle = context.resources.getString(
                    context.resources.getIdentifier(
                        stageFromJson.subtitle,
                        "string",
                        context.packageName
                    )
                ),
                type = stageType,
                startMeditationIconPath = stageFromJson.startIcon,
                staticBgPath = stageFromJson.imageBackground,
                dynamicBgPath = stageFromJson.videoBackground,
                status = stageStatus,
                currentDayNumber = 0,
                daysCount = stageFromJson.meditations.size
            )

            stageList.add(stage)

            stageFromJson.meditations.forEachIndexed { meditationIndex, meditationJson ->
                val audioList = mapAudio(meditationJson.audio)

                meditationList.add(
                    MeditationEntity(
                        dayNumber = meditationIndex,
                        audioList = audioList,
                        preMeditateList = mapExplanationMeditateList(meditationJson.preMeditate),
                        postMeditateList = mapExplanationMeditateList(meditationJson.postMeditate),
                        isCompleted = false,
                        timeCompletedMillis = 0,
                        duration = audioList.firstOrNull()?.durationMillis ?: 0,
                        notification = if (meditationJson.notification.isNullOrEmpty()) "" else getStringFromResource(
                            meditationJson.notification
                        ),
                        image = if (meditationJson.image.isNullOrEmpty()) "" else meditationJson.image,
                        completedImage = if (meditationJson.completedImage.isNullOrEmpty()) "" else meditationJson.completedImage,
                        title = if (meditationJson.title.isNullOrEmpty()) "" else getStringFromResource(
                            meditationJson.title
                        ),
                        stageId = stage.id
                    )
                )
            }
        }

//        Timber.e("Stages")
//        stageList.forEach {
//            Timber.d("stage: $it")
//        }

//        Timber.e("Meditations")
//        meditationList.forEach {
//            Timber.d("meditation: $it")
//        }

        return Pair(stageList, meditationList)
    }

    private fun mapAudio(audio: List<StageJson.MeditationJson.AudioJson>): List<MeditationEntity.Audio> {
        val audioEntity = mutableListOf<MeditationEntity.Audio>()
        audio.forEach { audioJson ->

            val duration = AudioUtils.getDuration(context, ResByName.expansionFileUri(context, audioJson.path))

            Timber.d("mapAudio $duration - ${audioJson.path}")

            audioEntity.add(
                MeditationEntity.Audio(
                    path = audioJson.path,
                    durationMillis = duration
                )
            )
        }
        return audioEntity
    }

    private fun mapExplanationMeditateList(resourceName: String): List<MeditationEntity.Explanation> {
        val list = mutableListOf<MeditationEntity.Explanation>()
        try {
            val string = getStringFromResource(resourceName)

            if (string.isNotBlank()) {
                string.split("@").forEach { str ->
                    val explanationItems = fetchExplanationItems(str)
                    list.add(
                        MeditationEntity.Explanation(
                            items = explanationItems
                        )
                    )
                }
            }
        } catch (exc: Exception) {
            //ignore
        }
        return list
    }

    private fun getStringFromResource(resourceName: String): String {
        return context.resources.getString(
            context.resources.getIdentifier(
                resourceName,
                "string",
                context.packageName
            )
        )
    }

    private fun fetchExplanationItems(str: String): List<MeditationEntity.Explanation.Item> {
        val list = mutableListOf<MeditationEntity.Explanation.Item>()
        str.split("##").forEach {
            if (it.isNotBlank()) {
                if (it.contains("image/")) {
                    list.add(MeditationEntity.Explanation.ImageItem(it.replace("image/", "")))
                } else {
                    list.add(MeditationEntity.Explanation.TextItem(it))
                }
            }
        }
        return list
    }
}