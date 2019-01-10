package com.forreview.ui.menu.more_meditations


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.forreview.PACK_ID
import com.forreview.R
import com.forreview.base.BaseActivity
import com.forreview.base.BaseFragment
import com.forreview.model.ActiveStageViewState
import com.forreview.model.Meditation
import com.forreview.model.ViewState
import com.forreview.ui.meditation.main.MeditationActivity
import com.forreview.ui.menu.skip_stage.MeditationListViewState
import kotlinx.android.synthetic.main.fragment_unlocked_pack.*
import kotlinx.android.synthetic.main.item_unlocked_meditation.view.*
import kotlinx.android.synthetic.main.unlocked_pack_view.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class UnlockedPackFragment : BaseFragment() {
    private val moreMeditationsViewModel by viewModel<MoreMeditationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unlocked_pack, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribe(moreMeditationsViewModel.singleStageObservable)
        subscribe(moreMeditationsViewModel.meditationObservable)

        arguments?.getString(PACK_ID)?.also {
            (activity as BaseActivity).setTitle(it)
            moreMeditationsViewModel.loadStage(it)
            moreMeditationsViewModel.loadMeditations(it)
        }
    }

    override fun onApplyViewState(viewState: ViewState) {
        super.onApplyViewState(viewState)
        when (viewState) {
            is ActiveStageViewState -> {

            }
            is MeditationListViewState -> {
                initViews(viewState.meditationList as MutableList)
            }
        }
    }

    private fun initViews(meditations: MutableList<Meditation>) {
        val daysList = mutableListOf<MutableList<Meditation>>()
        val firstScreen = mutableListOf<Meditation>()
        val secondScreen = mutableListOf<Meditation>()
        val thirdScreen = mutableListOf<Meditation>()
        val fourthScreen = mutableListOf<Meditation>()
        meditations.forEachIndexed { i, item ->
            when (i) {
                in 0..5 -> {
                    firstScreen.add(item)
                }
                in 6..11 -> {
                    secondScreen.add(item)
                }
                in 12..17 -> {
                    thirdScreen.add(item)
                }
                in 18..23 -> {
                    fourthScreen.add(item)
                }
            }
        }
        daysList.apply {
            if (firstScreen.isNotEmpty()) daysList.add(firstScreen)
            if (secondScreen.isNotEmpty()) daysList.add(secondScreen)
            if (thirdScreen.isNotEmpty()) daysList.add(thirdScreen)
            if (fourthScreen.isNotEmpty()) daysList.add(fourthScreen)
        }
        view_pager.adapter = CustomPagerAdapter(context, daysList)
    }

    inner class CustomPagerAdapter(
        private val context: Context?,
        private val list: MutableList<MutableList<Meditation>>
    ) :
        PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.unlocked_pack_view, collection, false) as ViewGroup
            val cardList = mutableListOf<View>()
            cardList.run {
                add(layout.first_card)
                add(layout.second_card)
                add(layout.third_card)
                add(layout.fourth_card)
                add(layout.fifth_card)
                add(layout.sixth_card)
            }
            collection.addView(layout)
            val item = list[position]
            item.forEachIndexed { i, meditation ->
                val cardView = cardList[i]
                cardView.setOnClickListener {
                    moreMeditationsViewModel.setPaidActiveMeditation(meditation)
                    MeditationActivity.startPaid(activity)
                }
                cardView.visibility = View.VISIBLE
                if (meditation.isCompleted()) {
                    Timber.e("completed image ${meditation.completedImage}")
                    cardView.image.setImageDrawable(getDrawable(meditation.completedImage))
                } else {
                    Timber.e("completed image ${meditation.image}")
                    cardView.image.setImageDrawable(getDrawable(meditation.image))
                }
                cardView.itemTitle.text = meditation.title
            }
            return layout
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        private fun getDrawable(resourceName: String): Drawable? {
            return context?.resources?.getDrawable(
                context.resources.getIdentifier(
                    resourceName,
                    "drawable",
                    context.packageName
                )
            )
        }
    }
}
