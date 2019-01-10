package com.forreview.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition

object GlideHelper {

    fun load(context: Context?, imageView: ImageView, @DrawableRes resId: Int) {
        context ?: return

        Glide.with(context)
            .load(resId)
            .into(imageView)
    }

    fun load(context: Context?, imageView: ImageView, path: String) {
        context ?: return

        Glide.with(context)
            .load(path)
            .into(imageView)
    }

    fun loadBg(context: Context?, view: View, resourceName: String) {
        context ?: return

        val resId = ResByName.drawable(context, resourceName)

        loadBg(context, view, resId)
    }

    fun loadBg(context: Context?, view: View, @DrawableRes resId: Int) {
        context ?: return

        Glide.with(context)
            .load(resId)
            .into(object : ViewTarget<View, Drawable>(view) {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    view.background = resource
                }
            })
    }
}