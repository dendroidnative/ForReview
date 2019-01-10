package com.forreview.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.forreview.model.entity.MeditationEntity
import org.json.JSONArray
import timber.log.Timber

class ExplanationConverter {

    @TypeConverter
    fun fromDb(list: String): List<MeditationEntity.Explanation> {
        try {
            val explanationList = mutableListOf<MeditationEntity.Explanation>()

            val explanationJsonArray = JSONArray(list)
            for (i in 0 until explanationJsonArray.length()) {
                val explanationJson = explanationJsonArray.getJSONObject(i)
                if (explanationJson.has("items")) {
                    val itemJsonArray = explanationJson.getJSONArray("items")

                    val itemList = mutableListOf<MeditationEntity.Explanation.Item>()
                    for(j in 0 until itemJsonArray.length()) {
                        val itemJson = itemJsonArray.getJSONObject(j)
                        if (itemJson.has("text")) {
                            val text = itemJson.getString("text")
                            itemList.add(MeditationEntity.Explanation.TextItem(text))
                        } else if(itemJson.has("image")) {
                            val image = itemJson.getString("image")
                            itemList.add(MeditationEntity.Explanation.ImageItem(image))
                        }
                    }

                    explanationList.add(MeditationEntity.Explanation(itemList))
                }
            }
            return explanationList
//            return Gson().fromJson(list, object : TypeToken<MutableList<MeditationEntity.Explanation>>() {}.type)
        } catch (exc: Exception) {
            Timber.e(exc)
            //ignore
        }
        return mutableListOf()
    }

    @TypeConverter
    fun toDb(list: List<MeditationEntity.Explanation>): String {
        return Gson().toJson(list)
    }

}