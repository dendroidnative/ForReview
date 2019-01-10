package com.forreview.helper

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.forreview.*
import com.google.gson.Gson
import com.forreview.model.Reminder


interface PrefsHelper {
    fun isFirstLaunch(): Boolean
    fun setFirstLaunch(isFirstLaunch: Boolean): Boolean
    fun getMeditationReminder(): Reminder
    fun setMeditationReminder(reminder: Reminder)
    fun setLastMeditationTime(timeMillis: Long)
    fun getLastMeditationTime(): Long
    fun activateAnxietyPack()
    fun activateCalmOnTheGoPack()
    fun activateEndOfDayPack()
    fun activateRecenteringPack()
    fun activateEverythingPack()
    fun getPurchasedPacks(): MutableList<String>
}

private const val FIRST_LAUNCH = "FIRST_LAUNCH"
private const val MEDITATION_REMINDER = "MEDITATION_REMINDER"
private const val LAST_MEDITATION_TIME = "LAST_MEDITATION_TIME"

@SuppressLint("ApplySharedPref")
class PrefsHelperImpl(private val pref: SharedPreferences) : PrefsHelper {
    override fun isFirstLaunch() = pref.getBoolean(FIRST_LAUNCH, true)
    override fun setFirstLaunch(isFirstLaunch: Boolean) = pref.edit().putBoolean(FIRST_LAUNCH, isFirstLaunch).commit()

    override fun getMeditationReminder(): Reminder {
        val reminderString = pref.getString(MEDITATION_REMINDER, null)
        return if (reminderString == null) {
            Reminder(
                isSet = false,
                timeMillis = System.currentTimeMillis(),
                days = Reminder.Days.DAILY
            )
        } else {
            Gson().fromJson(reminderString, Reminder::class.java)
        }
    }

    override fun setMeditationReminder(reminder: Reminder) {
        pref.edit().putString(MEDITATION_REMINDER, Gson().toJson(reminder)).commit()
    }

    override fun setLastMeditationTime(timeMillis: Long) {
        pref.edit().putLong(LAST_MEDITATION_TIME, timeMillis).commit()
    }

    override fun getLastMeditationTime(): Long {
        return pref.getLong(LAST_MEDITATION_TIME, 0)
    }


    //todo cahnge false to true to activate right pack or all packs
    override fun getPurchasedPacks(): MutableList<String> {
        val purchasedPack = mutableListOf<String>()
        if(pref.getBoolean(SKU_EVERYTHING_PACK, false)){
            purchasedPack.add(SKU_EVERYTHING_PACK)
        }
        if(pref.getBoolean(SKU_ANXIETY_PACK, false)){
            purchasedPack.add(SKU_ANXIETY_PACK)
        }
        if(pref.getBoolean(SKU_CALM_ON_THE_GO, false)){
            purchasedPack.add(SKU_CALM_ON_THE_GO)
        }
        if(pref.getBoolean(SKU_END_OF_DAY_PACK, false)){
            purchasedPack.add(SKU_END_OF_DAY_PACK)
        }
        if(pref.getBoolean(SKU_RECENTERING_PACK, false)){
            purchasedPack.add(SKU_RECENTERING_PACK)
        }
        if(pref.getBoolean(SKU_EVERYTHING_PACK, false)){
            purchasedPack.add(SKU_EVERYTHING_PACK)
        }
        return purchasedPack
    }

    override fun activateAnxietyPack() {
        pref.edit().putBoolean(SKU_ANXIETY_PACK, true).commit()
    }

    override fun activateCalmOnTheGoPack() {
        pref.edit().putBoolean(SKU_CALM_ON_THE_GO, true).commit()
    }

    override fun activateEndOfDayPack() {
        pref.edit().putBoolean(SKU_END_OF_DAY_PACK, true).commit()
    }

    override fun activateRecenteringPack() {
        pref.edit().putBoolean(SKU_RECENTERING_PACK, true).commit()
    }

    override fun activateEverythingPack() {
        pref.edit().putBoolean(SKU_EVERYTHING_PACK, true).commit()
    }

}