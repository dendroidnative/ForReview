package com.forreview.ui.menu.more_meditations


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.*
import com.forreview.*
import com.forreview.base.BaseFragment
import com.forreview.helper.MainNavigation
import com.forreview.helper.PurchaseController
import kotlinx.android.synthetic.main.fragment_meditation_pack.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class MeditationPackFragment : BaseFragment(), PurchaseController.BillingInterface {
    override fun onBillingOK() {

    }

    override fun onBillingFailure(message: String) {

    }

    override fun purchaseSuccess() {
        updateViews()
    }

    override fun alreadyOwned() {

    }

    private val moreMeditationsViewModel by sharedViewModel<MoreMeditationsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meditation_pack, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()

        updateViews()
    }

    private fun updateViews() {
        moreMeditationsViewModel.getPurchasedPacks().forEach {
            if (it == arguments?.getString(PACK_ID) || it == SKU_EVERYTHING_PACK) {
                arguments?.getString(PACK_ID)?.also {
                    Timber.e("pack $it")
                    when (it) {
                        SKU_END_OF_DAY_PACK -> {
                            meditation_pack_title.text = getString(R.string.meditation_pack_how_to_use)
                            meditation_pack_text.text =
                                    getString(R.string.meditation_pack_fragment_end_of_the_day_text_paid)
                            meditation_pack_unlock_pack.setOnClickListener {
                                MainNavigation.fromMeditationPacksToUnlockedPackFragment(activity,
                                    END_OF_DAY_PACK_TITLE
                                )
                            }
                        }

                        SKU_ANXIETY_PACK -> {
                            meditation_pack_title.text = getString(R.string.meditation_pack_how_to_use)
                            meditation_pack_text.text = getString(R.string.meditation_pack_fragment_anxiety_text_paid)
                            meditation_pack_unlock_pack.setOnClickListener {
                                MainNavigation.fromMeditationPacksToUnlockedPackFragment(activity,
                                    ANXIETY_PACK_TITLE
                                )
                            }
                        }

                        SKU_CALM_ON_THE_GO -> {
                            meditation_pack_title.text = getString(R.string.meditation_pack_how_to_use)
                            meditation_pack_text.text =
                                    getString(R.string.meditation_pack_fragment_calm_on_the_go_text_paid)
                            meditation_pack_unlock_pack.setOnClickListener {
                                MainNavigation.fromMeditationPacksToUnlockedPackFragment(activity,
                                    CALM_ON_THE_GO_PACK_TITLE
                                )
                            }
                        }

                        SKU_RECENTERING_PACK -> {
                            meditation_pack_title.text = getString(R.string.meditation_pack_how_to_use)
                            meditation_pack_text.text =
                                    getString(R.string.meditation_pack_fragment_sos_recentering_text_paid)
                            meditation_pack_unlock_pack.setOnClickListener {
                                MainNavigation.fromMeditationPacksToUnlockedPackFragment(activity,
                                    RECENTERING_PACK_TITLE
                                )
                            }
                        }

                    }
                }
                meditation_pack_unlock_pack.text = getString(R.string.meditation_pack_fragment_button_paid)
                meditation_pack_unlock_everithing.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        when (arguments?.getString(PACK_ID)) {
            SKU_END_OF_DAY_PACK -> {
                meditation_pack_title.text = getString(R.string.meditation_pack_fragment_end_of_the_day_title)
                meditation_pack_text.text = getString(R.string.meditation_pack_fragment_end_of_the_day_text)
                meditation_pack_image.setImageResource(R.drawable.end_of_the_day_icon)
                meditation_pack_unlock_pack.setOnClickListener {
                    activity?.also {
                        moreMeditationsViewModel.requestPurchase(it, SKU_END_OF_DAY_PACK, this)
                    }
                }
            }

            SKU_ANXIETY_PACK -> {
                meditation_pack_title.text = getString(R.string.meditation_pack_fragment_anxiety_title)
                meditation_pack_text.text = getString(R.string.meditation_pack_fragment_anxiety_text)
                meditation_pack_image.setImageResource(R.drawable.store_rainy)
                meditation_pack_unlock_pack.setOnClickListener {
                    activity?.also {
                        moreMeditationsViewModel.requestPurchase(it, SKU_ANXIETY_PACK, this)
                    }
                }
            }

            SKU_CALM_ON_THE_GO -> {
                meditation_pack_title.text = getString(R.string.meditation_pack_fragment_calm_on_the_go_title)
                meditation_pack_text.text = getString(R.string.meditation_pack_fragment_calm_on_the_go_text)
                meditation_pack_image.setImageResource(R.drawable.calm_on_the_go_icon)
                meditation_pack_unlock_pack.setOnClickListener {
                    activity?.also {
                        moreMeditationsViewModel.requestPurchase(it, SKU_CALM_ON_THE_GO, this)
                    }
                }
            }

            SKU_RECENTERING_PACK -> {
                meditation_pack_title.text = getString(R.string.meditation_pack_fragment_sos_recentering_title)
                meditation_pack_text.text = getString(R.string.meditation_pack_fragment_sos_recentering_text)
                meditation_pack_image.setImageResource(R.drawable.recentering_icon)
                meditation_pack_unlock_pack.setOnClickListener {
                    activity?.also {
                        moreMeditationsViewModel.requestPurchase(it, SKU_RECENTERING_PACK, this)
                    }
                }
            }
        }
        meditation_pack_unlock_everithing.setOnClickListener {
            activity?.also {
                moreMeditationsViewModel.requestPurchase(it, SKU_EVERYTHING_PACK, this)
            }
        }
    }
}
