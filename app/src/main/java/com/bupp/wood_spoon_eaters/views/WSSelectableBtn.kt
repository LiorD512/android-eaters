package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WsSelectableBtnBinding


class WSSelectableBtn @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: WsSelectableBtnBinding = WsSelectableBtnBinding.inflate(LayoutInflater.from(context), this, true)

    init {
         initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let{

            val attr = context.obtainStyledAttributes(attrs, R.styleable.WSSelectableBtn)

            val title = attr.getString(R.styleable.WSSelectableBtn_title)
            binding.wsSelectableBtnText.text = title

            val icon = attr.getDrawable(R.styleable.WSSelectableBtn_WSicon)
            setIcon(icon)

            attr.recycle()

        }
    }

    fun setBtnSelected(isSelected: Boolean){
        binding.wsSelectableBtnBkg.isSelected = isSelected

    }

    private fun setIcon(icon: Drawable?) {
        binding.wsSelectableBtnIcon.setImageDrawable(icon)
    }

}
