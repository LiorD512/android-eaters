package com.bupp.wood_spoon_chef.presentation.custom_views


import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import me.grantland.widget.AutofitHelper

/**
 * A [TextView] that re-sizes its text to be no larger than the width of the view.
 *
 * @attr ref R.styleable.AutofitTextView_sizeToFit
 * @attr ref R.styleable.AutofitTextView_minTextSize
 * @attr ref R.styleable.AutofitTextView_precision
 */
class AutofitTextView : AppCompatTextView, AutofitHelper.OnTextSizeChangeListener {

    /**
     * Returns the [AutofitHelper] for this View.
     */
    private var autofitHelper: AutofitHelper? = null

    /**
     * Returns whether or not the text will be automatically re-sized to fit its constraints.
     */

    /**
     * Returns the maximum size (in pixels) of the text in this View.
     */

    /**
     * Returns the amount of precision used to calculate the correct text size to fit within its
     * bounds.
     */

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        autofitHelper = AutofitHelper.create(this, attrs, defStyle)
            .addOnTextSizeChangeListener(this)
    }

    // Getters and Setters

    /**
     * {@inheritDoc}
     */
    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        if (autofitHelper != null) {
            autofitHelper!!.setTextSize(unit, size)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun setLines(lines: Int) {
        super.setLines(lines)
        if (autofitHelper != null) {
            autofitHelper!!.maxLines = lines
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        if (autofitHelper != null) {
            autofitHelper!!.maxLines = maxLines
        }
    }

    override fun onTextSizeChange(textSize: Float, oldTextSize: Float) {
        // do nothing
    }
}