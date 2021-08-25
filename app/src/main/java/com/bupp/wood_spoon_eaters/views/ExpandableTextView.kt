package com.bupp.wood_spoon_eaters.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import com.bupp.wood_spoon_eaters.R


class ExpandableTextView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr), View.OnClickListener {

    var listener: ExpandableTextViewListener? = null
    interface ExpandableTextViewListener{
        fun onTextViewExpanded()
    }

    private var viewMoreText = "...View More "
    private var viewLessText = "  View Less "
    private var collapseMaxLines = 2

    private var animator: ValueAnimator? = null
    private var isCollapsing = false
    private var originalText: CharSequence? = null

    private var isExpandable = false
    private var savedState: CharSequence? = null

    init {
        initUi(attrs)
        maxLines = collapseMaxLines
        setOnClickListener(this)
        initAnimator()
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)

            val maxLines = attr.getInt(R.styleable.ExpandableTextView_collapseMaxLines, 2)
            maxLines.let { collapseMaxLines = it }

            val viewMoreText = attr.getString(R.styleable.ExpandableTextView_viewMoreText)
            viewMoreText?.let {
                this.viewMoreText = "...$it "
            }

            val viewLessText = attr.getString(R.styleable.ExpandableTextView_viewLessText)
            viewLessText?.let { this.viewLessText = "  $it " }

            attr.recycle()
        }
    }


    private fun initAnimator() {
        animator = ValueAnimator.ofInt(-1, -1)
        animator?.let { animator ->
            animator.duration = 300
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addUpdateListener(AnimatorUpdateListener { valueAnimator -> updateHeight(valueAnimator.animatedValue as Int) })
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (isCollapsed) {
                        isCollapsing = false
                        maxLines = Int.MAX_VALUE
                        ellipsizeShowLess() //add this line
                    } else {
                        isCollapsing = true
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (!isCollapsed && isCollapsing) {
                        maxLines = collapseMaxLines
                        ellipsizeShowMore() // add this line
                        isCollapsing = false
                    }
                    setWrapContent()
                }
            })
        }
    }

    fun initExpandableTextView(listener: ExpandableTextViewListener){
        this.listener = listener
    }

    override fun setText(text: CharSequence, type: BufferType) {
        originalText = text
        if (!savedState.isNullOrEmpty()) {
            super.setText(savedState, type)
            return
        }
        super.setText(text, type)
    }

    private fun ellipsizeShowMore() {
        savedState = null
        val end = layout.getLineEnd(collapseMaxLines - 1)
        val text = text
        val chars = (layout.getLineEnd(collapseMaxLines - 1)
                - layout.getLineStart(collapseMaxLines - 1))
        val additionalGap = 4
        if (chars + additionalGap < viewMoreText.length) {
            // handle rare case when text has a last  maxLine which has  only few chars and
            // then it goes to the next line .
            // lin such case there is nothing twe cannot replace because postfix
            // length is greater then max line length. Do nothing.
            return
        }

        val builder = SpannableStringBuilder(text)
        builder.replace(end - viewMoreText.length, end, viewMoreText + "\n")
        builder.setSpan(
            ForegroundColorSpan(Color.BLACK),
            end - viewMoreText.length, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            StyleSpan(Typeface.BOLD),
            end - viewMoreText.length, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setTextNoCaching(builder)
    }

    private fun ellipsizeShowLess() {
        if (!originalText.isNullOrEmpty() && isExpandable) {
            savedState = null
            super.setText(originalText)
            val end = text.length
            val builder = SpannableStringBuilder(text)
            builder.append(viewLessText)
            builder.setSpan(
                ForegroundColorSpan(Color.BLACK),
                end, end + viewLessText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.setSpan(
                StyleSpan(Typeface.BOLD),
                end, end + viewLessText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setTextNoCaching(builder)
        }
    }

    private fun setTextNoCaching(text: CharSequence?) {
        super.setText(text, BufferType.NORMAL)
        savedState = text
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (lineCount <= collapseMaxLines) {
            ellipsizeShowLess() // add to fix current bug
            isClickable = false
        } else {
            isClickable = true
            if (!animator!!.isRunning && isCollapsed) {
                ellipsizeShowMore()
                isExpandable = true
            }
        }
    }

    override fun onClick(v: View?) {
        listener?.onTextViewExpanded()
        if (animator!!.isRunning) {
            animatorReverse()
            return
        }
        val endPosition = animateTo()
        val startPosition = height
        animator!!.setIntValues(startPosition, endPosition)
        animatorStart()
    }

    private fun animatorReverse() {
        isCollapsing = !isCollapsing
        animator?.reverse()
    }

    private fun animatorStart() {
        animator?.start()
    }

    private fun animateTo(): Int {
        return if (isCollapsed) {
            layout.height + paddingHeight
        } else {
            (layout.getLineBottom(collapseMaxLines - 1)
                    + layout.bottomPadding + paddingHeight)
        }
    }

    private val paddingHeight: Int
        get() = compoundPaddingBottom + compoundPaddingTop
    private val isCollapsed: Boolean
        get() = Int.MAX_VALUE != maxLines

    private fun updateHeight(animatedValue: Int) {
        val layoutParams = layoutParams
        layoutParams.height = animatedValue
        setLayoutParams(layoutParams)
    }

    private fun setWrapContent() {
        val layoutParams = layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        setLayoutParams(layoutParams)
    }

}