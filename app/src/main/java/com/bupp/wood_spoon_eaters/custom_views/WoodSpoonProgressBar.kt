package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.woodspoon_progress_bar.view.*


class WoodSpoonProgressBar : FrameLayout {

    private var isBlue: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.woodspoon_progress_bar, this, true)

        if (attrs != null) {
            val attrSet = context.obtainStyledAttributes(attrs, R.styleable.WoodSpoonProgressBar)

            if (attrSet.hasValue(R.styleable.WoodSpoonProgressBar_isBlueStyle)) {
                isBlue = attrSet.getBoolean(R.styleable.WoodSpoonProgressBar_isBlueStyle, false)
            }
        }
        setProgressBarStyle()
    }

    fun show() {
        progressBarLayout.visibility = View.VISIBLE
    }

    fun hide() {
        progressBar.stop()
        progressBarLayout.visibility = View.GONE
    }

    private fun setProgressBarStyle() {
        if (isBlue) {
            progressBar.defaultColor = ContextCompat.getColor(context, R.color.white)
            progressBar.selectedColor = ContextCompat.getColor(context, R.color.white)
            progressBar.firstShadowColor = ContextCompat.getColor(context, R.color.white)
            progressBar.secondShadowColor = ContextCompat.getColor(context, R.color.white)
            progressBarLayout.background = ContextCompat.getDrawable(context, R.color.teal_blue_80)
        } else {
            progressBar.defaultColor = ContextCompat.getColor(context, R.color.teal_blue)
            progressBar.selectedColor = ContextCompat.getColor(context, R.color.teal_blue)
            progressBar.firstShadowColor = ContextCompat.getColor(context, R.color.teal_blue)
            progressBar.secondShadowColor = ContextCompat.getColor(context, R.color.teal_blue)
            progressBarLayout.background = ContextCompat.getDrawable(context, R.color.white_80)
        }
    }
}