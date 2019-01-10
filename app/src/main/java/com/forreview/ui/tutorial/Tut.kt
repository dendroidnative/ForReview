package com.forreview.ui.tutorial

import androidx.annotation.DrawableRes
import java.io.Serializable

data class Tut(
    @DrawableRes val image: Int,
    val title: String,
    val desc: String
): Serializable