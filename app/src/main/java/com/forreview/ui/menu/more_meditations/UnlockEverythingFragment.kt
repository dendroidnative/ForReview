package com.forreview.ui.menu.more_meditations


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.R
import com.forreview.SKU_EVERYTHING_PACK
import com.forreview.base.BaseFragment
import com.forreview.helper.MainNavigation
import com.forreview.helper.PurchaseController
import kotlinx.android.synthetic.main.fragment_unlock_everything.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class UnlockEverythingFragment : BaseFragment(), PurchaseController.BillingInterface {
    override fun onBillingOK() {

    }

    override fun onBillingFailure(message: String) {

    }

    override fun purchaseSuccess() {
        MainNavigation.pop(activity)
    }

    override fun alreadyOwned() {

    }

    private val moreMeditationsViewModel by sharedViewModel<MoreMeditationsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unlock_everything, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        unlock_everything_button?.setOnClickListener {
            activity?.also {
                moreMeditationsViewModel.requestPurchase(it, SKU_EVERYTHING_PACK, this)
            }
        }
    }
}
