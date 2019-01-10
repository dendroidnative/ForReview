package com.forreview.ui.menu.more_meditations


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.*
import com.forreview.*
import com.forreview.base.BaseFragment
import com.forreview.helper.MainNavigation
import kotlinx.android.synthetic.main.fragment_more_meditations.*
import kotlinx.android.synthetic.main.item_more_meditations.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MoreMeditationsFragment : BaseFragment() {
    private val moreMeditationsViewModel by sharedViewModel<MoreMeditationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_meditations, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViews()

        updateViews()
    }

    private fun updateViews() {
        moreMeditationsViewModel.getPurchasedPacks().forEach {
            when (it) {
                SKU_EVERYTHING_PACK -> {
                    unlockAll()
                    unlock_everything_button.text =
                            getString(R.string.more_meditations_fragment_unlocked_everything)
                    unlock_everything_button.setOnClickListener {

                    }
                    return
                }
                SKU_END_OF_DAY_PACK -> {
                    end_of_the_day_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
                    end_of_the_day_item.itemButton.setOnClickListener {
                        MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                            SKU_END_OF_DAY_PACK
                        )
                    }
                }
                SKU_ANXIETY_PACK -> {
                    anxiety_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
                    anxiety_item.itemButton.setOnClickListener {
                        MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                            SKU_ANXIETY_PACK
                        )
                    }
                }
                SKU_CALM_ON_THE_GO -> {
                    calm_on_the_go_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
                    calm_on_the_go_item.itemButton.setOnClickListener {
                        MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                            SKU_CALM_ON_THE_GO
                        )
                    }
                }
                SKU_RECENTERING_PACK -> {
                    recentering_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
                    recentering_item.itemButton.setOnClickListener {
                        MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                            SKU_RECENTERING_PACK
                        )
                    }
                }
            }
        }
    }

    private fun unlockAll(){
        end_of_the_day_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
        end_of_the_day_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_END_OF_DAY_PACK
            )
        }

        anxiety_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
        anxiety_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_ANXIETY_PACK
            )
        }

        calm_on_the_go_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
        calm_on_the_go_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_CALM_ON_THE_GO
            )
        }

        recentering_item.itemButton.text = getString(R.string.more_meditations_fragment_go)
        recentering_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_RECENTERING_PACK
            )
        }
    }

    private fun initViews() {
        end_of_the_day_item.itemTitle.text = getString(R.string.meditation_pack_fragment_end_of_the_day_title)
        end_of_the_day_item.image.setImageResource(R.drawable.end_of_the_day_icon)
        end_of_the_day_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_END_OF_DAY_PACK
            )
        }

        calm_on_the_go_item.itemTitle.text = getString(R.string.meditation_pack_fragment_calm_on_the_go_title)
        calm_on_the_go_item.image.setImageResource(R.drawable.calm_on_the_go_icon)
        calm_on_the_go_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_CALM_ON_THE_GO
            )
        }

        anxiety_item.itemTitle.text = getString(R.string.meditation_pack_fragment_anxiety_title)
        anxiety_item.image.setImageResource(R.drawable.store_rainy)
        anxiety_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_ANXIETY_PACK
            )
        }

        recentering_item.itemTitle.text = getString(R.string.meditation_pack_fragment_sos_recentering_title)
        recentering_item.image.setImageResource(R.drawable.recentering_icon)
        recentering_item.itemButton.setOnClickListener {
            MainNavigation.fromMoreMeditationsToMeditationPack(activity,
                SKU_RECENTERING_PACK
            )
        }

        unlock_everything_button.setOnClickListener {
            MainNavigation.fromMoreMeditationsToUnlockEverythingFragment(activity)
        }
    }

    companion object {
        const val TAG = "MoreMeditationsFragment"
    }
}
