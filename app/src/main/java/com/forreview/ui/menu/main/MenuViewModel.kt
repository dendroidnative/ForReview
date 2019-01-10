package com.forreview.ui.menu.main

import com.forreview.base.BaseViewModel
import com.forreview.datamanager.DataManager

abstract class MenuViewModel: BaseViewModel() {
}

class MenuViewModelImpl(
    private val dataManager: DataManager
): MenuViewModel() {

}