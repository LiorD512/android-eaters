package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.selectable_btn.view.*


class SelectableBtn : FrameLayout {

    interface SelectableBtnListener {
        fun onSelectableBtnClicked(btn: SelectableBtn)
    }

    fun setSelectableBtnListener(listener: SelectableBtnListener) {
        this.listener = listener
    }

    var listener: SelectableBtnListener? = null

    var isBtnSelected = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.selectable_btn, this, true)

        selectableBtnBackground.setOnClickListener { click() }

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SelectableBtn)
            val imageStr = a.getResourceId(R.styleable.SelectableBtn_image, 0)
            if (imageStr != 0) {
                Glide.with(context).load(imageStr).into(selectableBtnItem)
            }
            if (a.hasValue(R.styleable.SelectableBtn_title)) {
                var title = a.getString(R.styleable.SelectableBtn_title)
                selectableBtnName.text = title
            }
            a.recycle()
        }
    }

    private fun click() {
        isBtnSelected = !isBtnSelected
        setBtnEnabled(isBtnSelected)
        listener?.onSelectableBtnClicked(this)
    }

    public fun setBtnEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            selectableBtnItem.setColorFilter(
                ContextCompat.getColor(context, R.color.teal_blue),
                android.graphics.PorterDuff.Mode.MULTIPLY
            );
            selectableBtnName.isSelected = true
        } else {
            selectableBtnItem.setColorFilter(0)
            selectableBtnName.isSelected = false
        }
    }

    fun initBtn(icon: String, title: String) {
        Glide.with(context).load(icon).into(selectableBtnItem)
        selectableBtnName.text = title
    }
}