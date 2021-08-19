package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.WsTitleValueViewBinding

class WSTitleValueView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsTitleValueViewBinding = WsTitleValueViewBinding.inflate(LayoutInflater.from(context), this, true)

    interface WSTitleValueListener{
        fun onCustomToolTipClick()
    }

    init {
         initUi(attrs)
    }

    private var listener: WSTitleValueListener? = null
    fun setWSTitleValueListener(listener: WSTitleValueListener){
        this.listener = listener
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {

            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSTitleValueView)

                val title = attr.getString(R.styleable.WSTitleValueView_title)
                setTitle(title)

                val value = attr.getString(R.styleable.WSTitleValueView_subTitle)
                setValue(value)

                val isBold = attr.getBoolean(R.styleable.WSTitleValueView_is_bold, false)
                setStyle(isBold)

                val toolTip = attr.getInt(R.styleable.WSTitleValueView_tip_type, 0)
                if(toolTip != 0){
                    titleValueViewToolTip.customInit(context, attrs)
                    titleValueViewToolTip.visibility = View.VISIBLE

                    if(toolTip == Constants.TOOL_TIP_CUSTOM_CLICK){
                        titleValueViewToolTip.disable()
                        titleValueViewToolTip.setOnClickListener {
                            listener?.onCustomToolTipClick()
                        }
                    }
                }

                attr.recycle()
            }
        }
    }

    private fun setStyle(isBold: Boolean) {
        if(isBold){
            TextViewCompat.setTextAppearance(binding.titleValueViewTitle, R.style.LatoBold13Black)
            TextViewCompat.setTextAppearance(binding.titleValueViewValue, R.style.LatoBold13Black)
        }
    }

    fun setValue(value: String?) {
        value?.let{
            binding.titleValueViewValue.text = it
        }
    }

    fun setTitle(title: String?) {
        title?.let{
            binding.titleValueViewTitle.text = it
        }
    }

}
