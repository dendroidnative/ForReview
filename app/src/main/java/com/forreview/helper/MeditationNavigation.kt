package com.forreview.helper

import android.app.Activity
import android.os.Bundle
import androidx.navigation.Navigation
import com.forreview.R
import com.forreview.ui.meditation.main.MeditationMainViewModel
import timber.log.Timber

object MeditationNavigation {

    fun fromStartMeditationToMeditation(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_startMeditationFragment_to_meditationFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromStartMeditationToDurationSelection(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_startMeditationFragment_to_durationSelectorFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromStartMeditationToPreMeditation(activity: Activity?, infoPosition: Int) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_startMeditationFragment_to_preMeditationFragment,
                    Bundle().apply { putInt(MeditationMainViewModel.KEY_INFO_POSITION, infoPosition) })
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromDurationSelectionToMeditation(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_durationSelectorFragment_to_meditationFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromDurationSelectionToPreMeditation(activity: Activity?, infoPosition: Int) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(
                    R.id.action_durationSelectorFragment_to_preMeditationFragment,
                    Bundle().apply { putInt(MeditationMainViewModel.KEY_INFO_POSITION, infoPosition) })
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun preMeditationSelf(activity: Activity?, infoPosition: Int) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(
                    R.id.action_preMeditationFragment_self,
                    Bundle().apply { putInt(MeditationMainViewModel.KEY_INFO_POSITION, infoPosition) })
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromPreMeditationToMeditation(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_preMeditationFragment_to_meditationFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromMeditationToWakeup(activity: Activity?) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_meditationFragment_to_wakeUpFragment)
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun fromWakeupToPostMeditation(activity: Activity?, infoPosition: Int) {
        activity ?: return
        try {
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_wakeupFragment_to_postMeditationFragment,
                    Bundle().apply { putInt(MeditationMainViewModel.KEY_INFO_POSITION, infoPosition) })
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun postMeditationSelf(activity: Activity?, infoPosition: Int) {
        activity ?: return
        try {
            Timber.e("postMeditationSelf $infoPosition")
            Navigation.findNavController(activity, R.id.meditation_nav_fragment)
                .navigate(R.id.action_postMeditationFragment_self,
                    Bundle().apply { putInt(MeditationMainViewModel.KEY_INFO_POSITION, infoPosition) })
        } catch (exc: Exception) {
            //ignore
        }
    }

    fun popToStages(activity: Activity?) {
        activity?.finish()
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun currentFragment(activity: Activity?): Int? {
        activity ?: return null
        return Navigation.findNavController(activity, R.id.meditation_nav_fragment).currentDestination?.id
    }

    fun completeMeditation(activity: Activity?): Int? {
        activity ?: return null
        return Navigation.findNavController(activity, R.id.meditation_nav_fragment).currentDestination?.id
    }
}