package com.forreview.ui.menu.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.BuildConfig
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.helper.MainNavigation
import com.forreview.ui.menu.skip_stage.SkipToStageDialogFragment
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.menu_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuFragment : BaseFragment() {

    private val viewModel by viewModel<MenuViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        statisticsBtn.imageView.setImageResource(R.drawable.ic_menu_statistics)
        statisticsBtn.textView.text = getString(R.string.fragment_menu_statistics_item)
        statisticsBtn.setOnClickListener {
            MainNavigation.fromMenuToStatistics(activity)
        }

        remindersBtn.imageView.setImageResource(R.drawable.ic_menu_reminders)
        remindersBtn.textView.text = getString(R.string.fragment_menu_reminders_item)
        remindersBtn.setOnClickListener {
            MainNavigation.fromMenuToReminders(activity)
        }

        moreBtn.imageView.setImageResource(R.drawable.ic_menu_more)
        moreBtn.textView.text = getString(R.string.fragment_store_item)
        moreBtn.setOnClickListener {
            MainNavigation.fromMenuToMoreMeditations(activity)
        }

        helpBtn.imageView.setImageResource(R.drawable.ic_menu_help)
        helpBtn.textView.text = getString(R.string.fragment_menu_help_item)
        helpBtn.setOnClickListener {
            MainNavigation.fromMenuToHelp(activity)
        }
        if (BuildConfig.DEBUG) {
            toStageBtn.visibility = View.VISIBLE
            toStageBtn.setOnClickListener {
                SkipToStageDialogFragment.show(childFragmentManager)
            }
        }
    }

    companion object {
        const val TAG = "MenuFragment"
    }
}
