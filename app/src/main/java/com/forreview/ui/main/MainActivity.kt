package com.forreview.ui.main

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.forreview.DateChangeReceiver
import com.forreview.R
import com.forreview.base.BaseActivity
import com.forreview.helper.GlideHelper
import com.forreview.helper.MainNavigation
import com.forreview.model.ActiveStageViewState
import com.forreview.model.ViewState
import com.forreview.ui.MainViewModel
import com.forreview.ui.main.stages.StagesFragment
import com.forreview.ui.menu.help.HelpFragment
import com.forreview.ui.menu.main.MenuFragment
import com.forreview.ui.menu.more_meditations.MeditationPackFragment
import com.forreview.ui.menu.more_meditations.MoreMeditationsFragment
import com.forreview.ui.menu.more_meditations.UnlockEverythingFragment
import com.forreview.ui.menu.reminders.RemindersFragment
import com.forreview.ui.menu.statistics.StatisticsFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val dateChangeReceiver = DateChangeReceiver()

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)

        subscribe(viewModel.stageObservable)

        registerReceiver(dateChangeReceiver, IntentFilter(Intent.ACTION_DATE_CHANGED))


        assets.list("")?.forEach {
            Timber.tag("DownloaderActivity").d("asset: $it")
        }
    }

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentViewCreated(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            view: View,
            savedInstanceState: Bundle?
        ) {
            when (fragment) {
                is StagesFragment -> {
                    menuBtn.setImageResource(R.drawable.hamburger_menu)
                    menuBtn.setOnClickListener {
                        MainNavigation.fromStagesToMenu(this@MainActivity)
                    }
                }
                else -> {
                    menuBtn.setImageResource(R.drawable.ic_arrow_back_black_24dp)
                    menuBtn.setOnClickListener {
                        MainNavigation.pop(this@MainActivity)
                    }
                }
            }

            when (fragment) {
                is StagesFragment -> {
                    titleTextView.text = ""
                }
                is MenuFragment -> {
                    titleTextView.text = getString(R.string.title_menu)
                }
                is StatisticsFragment -> {
                    titleTextView.text = getString(R.string.title_statistics)
                }
                is RemindersFragment -> {
                    titleTextView.text = getString(R.string.title_reminders)
                }
                is HelpFragment -> {
                    titleTextView.text = getString(R.string.title_help)
                }
                is MoreMeditationsFragment -> {
                    titleTextView.text = getString(R.string.title_more_meditations)
                }
                is MeditationPackFragment -> {
                    titleTextView.text = getString(R.string.title_meditation_pack)
                }
                is UnlockEverythingFragment -> {
                    titleTextView.text = getString(R.string.title_meditation_pack)
                }
            }
        }
    }

    override fun onApplyViewState(viewState: ViewState) {
        when (viewState) {
            is ActiveStageViewState -> {
                GlideHelper.loadBg(this, root, viewState.stage.staticBgPath)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dateChangeReceiver)
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }
}