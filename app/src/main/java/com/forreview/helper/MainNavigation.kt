package com.forreview.helper

import android.app.Activity
import android.os.Bundle
import androidx.navigation.Navigation
import com.forreview.PACK_ID
import com.forreview.R

object MainNavigation {

    fun fromStagesToMenu(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_stagesFragment_to_menuFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMenuToStatistics(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_menuFragment_to_statisticsFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMenuToReminders(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_menuFragment_to_remindersFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMenuToMoreMeditations(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_menuFragment_to_nested_navigation)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMenuToHelp(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_menuFragment_to_helpFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun pop(activity: Activity?) {
        activity ?: return
        try {
            val navigation = Navigation.findNavController(activity, R.id.main_nav_fragment)
            if (navigation.currentDestination?.id != navigation.graph.startDestination) {
                navigation.popBackStack()
            }
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMoreMeditationsToMeditationPack(activity: Activity?, packId: String) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_moreMeditationsFragment_to_meditationPackFragment2, Bundle().apply { putString(PACK_ID, packId) })
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMoreMeditationsToUnlockEverythingFragment(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_moreMeditationsFragment_to_unlockEverythingFragment2)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMeditationPacksToUnlockedPackFragment(activity: Activity?, title:String) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.main_nav_fragment)
                .navigate(R.id.action_meditationPackFragment2_to_unlockedPackFragment, Bundle().apply {
                    putString(PACK_ID, title)
                })
        } catch (exc: Exception) {
            //ignore
        }
    }


//    fun fromStagesToStartMeditation(activity: Activity?) {
//        activity ?: return
//        try {
//            Navigation.findNavController(activity, R.title.main_nav_fragment)
//                .navigate(R.title.action_stagesFragment_to_startMeditationFragment)
//        } catch (exc: Exception) {
    //ignore
//        }
//    }


//    fun popToStages(activity: Activity?) {
//        activity ?: return
//        try {
//            Navigation.findNavController(activity, R.title.main_nav_fragment)
//                .popBackStack(R.title.stagesFragment, false)
//        } catch (exc: Exception) {
    //ignore
//        }
//    }
}