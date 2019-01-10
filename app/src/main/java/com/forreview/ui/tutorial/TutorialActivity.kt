package com.forreview.ui.tutorial

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.forreview.R
import com.forreview.base.BaseActivity
import com.forreview.helper.GlideHelper
import com.forreview.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_tutorial.*
import timber.log.Timber

class TutorialActivity : BaseActivity() {

    private val list = mutableListOf<Tut>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        GlideHelper.loadBg(this, root, R.drawable.tutorial_bg)

        list.add(
            Tut(
                R.drawable.tutorial_1,
                getString(R.string.tutorial_1_title),
                getString(R.string.tutorial_1_body)
            )
        )
        list.add(
            Tut(
                R.drawable.tutorial_2,
                getString(R.string.tutorial_2_title),
                getString(R.string.tutorial_2_body)
            )
        )
        list.add(
            Tut(
                R.drawable.tutorial_3,
                getString(R.string.tutorial_3_title),
                getString(R.string.tutorial_3_body)
            )
        )

        setSkip()

        skipBtn.setOnClickListener {
            done()
        }

        doneBtn.setOnClickListener {
            done()
        }

        viewPager.adapter = Adapter(supportFragmentManager)
        pageIndicatorView.setViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Timber.d("position %s, last %s", position, list.lastIndex)
                when (position) {
                    list.lastIndex -> {
                        setDone()
                    }
                    else -> {
                        setSkip()
                    }
                }
            }
        })
    }

    private fun setSkip() {
        skipBtn.visibility = View.VISIBLE
        doneBtn.visibility = View.INVISIBLE

        skipBtn.isEnabled = true
        doneBtn.isEnabled = false
    }

    private fun setDone() {
        skipBtn.visibility = View.INVISIBLE
        doneBtn.visibility = View.VISIBLE

        skipBtn.isEnabled = false
        doneBtn.isEnabled = true
    }

    private fun done() {
        skipBtn.isEnabled = false
        doneBtn.isEnabled = false
        MainActivity.start(this)
        finish()
    }

    inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return TutorialFragment.newInstance(list[position])
        }

        override fun getCount(): Int {
            return list.size
        }
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, TutorialActivity::class.java))
        }
    }
}
