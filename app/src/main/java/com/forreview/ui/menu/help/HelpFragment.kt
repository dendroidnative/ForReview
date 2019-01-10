package com.forreview.ui.menu.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.R
import com.forreview.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HelpFragment : BaseFragment() {

    private val viewModel by viewModel<HelpViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    companion object {
        const val TAG = "HelpFragment"
    }
}
