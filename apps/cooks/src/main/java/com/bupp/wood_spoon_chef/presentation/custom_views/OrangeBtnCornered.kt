package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.OrangeBtnCorneredBinding
import com.bupp.wood_spoon_chef.utils.Utils

class OrangeBtnCornered : FrameLayout {

    private var binding: OrangeBtnCorneredBinding =
        OrangeBtnCorneredBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.OrangeBtnCornered)
            if (a.hasValue(R.styleable.OrangeBtnCornered_title)) {
                val title = a.getString(R.styleable.OrangeBtnCornered_title)
                binding.orangeBtnText.text = title
            }
            if (a.hasValue(R.styleable.OrangeBtnCornered_isPink)) {
                val isPink = a.getBoolean(R.styleable.OrangeBtnCornered_isPink, false)
                if (isPink) {
                    setPinkUi()
                }
            }
            if (a.hasValue(R.styleable.OrangeBtnCornered_isOrangeish)) {
                val isOrangeish = a.getBoolean(R.styleable.OrangeBtnCornered_isOrangeish, false)
                if (isOrangeish) {
                    setOrangeishUi()
                }
            }
            if (a.hasValue(R.styleable.OrangeBtnCornered_isBtnSmall)) {
                val isSmall = a.getBoolean(R.styleable.OrangeBtnCornered_isBtnSmall, false)
                if (isSmall) {
                    setSmallUI()
                }
            }

            if (a.hasValue(R.styleable.OrangeBtnCornered_isPinkOrangeishTitle)) {
                val isPinkWithOrangeishTitle = a.getBoolean(R.styleable.OrangeBtnCornered_isPinkOrangeishTitle, false)
                if (isPinkWithOrangeishTitle) {
                    setPinkUiWithOrangeishUi()
                }
            }

            a.recycle()
        }
    }

    fun setText(text: String) {
        binding.orangeBtnText.text = text
    }

    private fun setPinkUiWithOrangeishUi() {
        with(binding) {
            orangeBtnText.setTextColor(ContextCompat.getColor(root.context, R.color.orangeish))
            orangeBtnBackground.background =
                ContextCompat.getDrawable(root.context, R.drawable.rectangle_pink_btn_cornered)
        }
    }

    private fun setPinkUi() {
        with(binding) {
            orangeBtnText.setTextColor(ContextCompat.getColor(root.context, R.color.coral))
            orangeBtnBackground.background =
                ContextCompat.getDrawable(root.context, R.drawable.rectangle_pink_btn_cornered)
        }
    }

    private fun setOrangeishUi() {
        binding.apply {
            orangeBtnText.setTextColor(ContextCompat.getColor(root.context, R.color.orangeish))
            orangeBtnBackground.background =
                ContextCompat.getDrawable(root.context, R.drawable.rectangle_orangish_12_cornered)
        }

    }

    private fun setSmallUI() {
        val topBottomPadding = Utils.toPx(5)
        val leftRightPadding = Utils.toPx(12)

        with(binding) {
            orangeBtnText.setPadding(
                leftRightPadding,
                topBottomPadding,
                leftRightPadding,
                topBottomPadding
            )
            orangeBtnText.textSize = 11.5F
        }
    }

}