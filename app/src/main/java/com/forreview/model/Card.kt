package com.forreview.model

import com.forreview.R

class CardList() {
    fun getEndOfTheDaysCardsScreenOne(): MutableList<Card> {
        val list = mutableListOf<Card>()
        return list.apply {
            add(
                Card(
                    R.drawable.end_sleep,
                    R.string.unlocked_meditation_end_of_the_day_sleep
                )
            )
            add(
                Card(
                    R.drawable.end_cant_sleep,
                    R.string.unlocked_meditation_end_of_the_day_cant_sleep
                )
            )
            add(
                Card(
                    R.drawable.end_7min,
                    R.string.unlocked_meditation_end_of_the_day_7_pause
                )
            )
            add(
                Card(
                    R.drawable.end_treat,
                    R.string.unlocked_meditation_end_of_the_day_treat
                )
            )
            add(
                Card(
                    R.drawable.end_breathing,
                    R.string.unlocked_meditation_end_of_the_day_calming_breathing
                )
            )
        }
    }

    fun getEndOfTheDaysCardsScreenTwo(): MutableList<Card> {
        val list = mutableListOf<Card>()
        return list.apply {
            add(
                Card(
                    R.drawable.end_5min,
                    R.string.unlocked_meditation_end_of_the_day_five_minute_break
                )
            )
            add(
                Card(
                    R.drawable.end_sense_aware,
                    R.string.unlocked_meditation_end_of_the_day_sense_aware
                )
            )
        }
    }

    fun getCalmOnTheGoScreenOne(): MutableList<Card> {
        val list = mutableListOf<Card>()
        return list.apply {
            add(
                Card(
                    R.drawable.end_5min,
                    R.string.unlocked_meditation_end_of_the_day_five_minute_break
                )
            )
        }
    }

    class Card(val image: Int, val title: Int)
}