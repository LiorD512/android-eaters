package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WsTitleValueViewBinding

class WSTitleValueView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsTitleValueViewBinding = WsTitleValueViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var currentToolType: Int = 0

    interface WSTitleValueListener{
        fun onToolTipClick(type: Int)
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

                val isBold = attr.getBoolean(R.styleable.WSTitleValueView_isBold, false)
                setStyle(isBold)

                val toolTipType = attr.getInt(R.styleable.WSTitleValueView_tip_type, 0)
                if(toolTipType != 0){
                    currentToolType = toolTipType
//                    titleValueViewToolTip.customInit(context, attrs)
                    titleValueViewToolTipBtn.visibility = View.VISIBLE

//                    titleValueViewToolTip.disable()

                }

                attr.recycle()

                titleValueViewToolTipBtn.setOnClickListener {
                    Log.d("wowToolTip","currentToolType: $currentToolType $listener")
                    listener?.onToolTipClick(currentToolType)
                }
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
