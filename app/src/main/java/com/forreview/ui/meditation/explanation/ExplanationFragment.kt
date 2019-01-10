package com.forreview.ui.meditation.explanation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.helper.ResByName
import com.forreview.model.Meditation
import kotlinx.android.synthetic.main.fragment_explanation.*
import timber.log.Timber

abstract class ExplanationFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_explanation, container, false)
    }

    protected fun applyExplanation(explanation: Meditation.Explanation) {
        container.removeAllViews()
        explanation.items.forEach { item ->
            when(item) {
                is Meditation.Explanation.TextItem -> {
                    addText(container, item)
                }
                is Meditation.Explanation.ImageItem -> {
                    addImage(container, item)
                }
            }
        }
    }

    protected fun applyMeditation(meditation: Meditation) {
        dayTextView.text = getString(R.string.fragment_explanation_day, meditation.dayNumber + 1)
        stageTextView.text = getString(R.string.fragment_explanation_stage)
        stageNameTextView.text = meditation.stage.title
    }

    private fun addText(container: LinearLayout, item: Meditation.Explanation.TextItem) {
        Timber.d("addText")
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_explanation_text, null, false)
        itemView.findViewById<TextView>(R.id.textView).text = item.text
        container.addView(itemView)
    }

    private fun addImage(container: LinearLayout, item: Meditation.Explanation.ImageItem) {
        Timber.d("addImage")
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_explanation_image, null, false)
        itemView.findViewById<ImageView>(R.id.imageView).setImageResource(ResByName.drawable(context!!, item.image))
        container.addView(itemView)
    }
}