package com.forreview.ui.custom

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.forreview.R
import kotlinx.android.synthetic.main.stage_progress.view.*

class StageProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.stage_progress, this, true)
    }

    fun setProgress(current: Int, max: Int) {
        countTextView.text = context.getString(R.string.stage_progress_day_count, current, max)
        progressBar.max = max
        if (Build.VERSION.SDK_INT >= 24) {
            progressBar.setProgress(current, true)
        } else {
            progressBar.progress = current
        }
    }
}