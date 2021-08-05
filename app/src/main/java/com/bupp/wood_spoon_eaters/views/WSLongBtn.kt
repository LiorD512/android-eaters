package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WsLongBtnBinding

class WSLongBtn @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsLongBtnBinding = WsLongBtnBinding.inflate(LayoutInflater.from(context), this, true)

    init {
         initUi(attrs)
    }


    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {

            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSLongBtn)

                val title = attr.getString(R.styleable.WSLongBtn_title)
                longBtnTitle.text = title

                val icon = attr.getDrawable(R.styleable.WSLongBtn_WSicon)
                setIcon(icon)

                val showSep = attr.getBoolean(R.styleable.WSLongBtn_showSep, true)
                handleSep(showSep)

                attr.recycle()
            }
        }
    }


    private fun setIcon(icon: Drawable?) {
        icon?.let{
            binding.longBtnIcon.setImageDrawable(icon)
            binding.longBtnIcon.visibility = View.VISIBLE
        }
    }


    private fun handleSep(showSep: Boolean) {
        if(!showSep){
            binding.longBtnSep.visibility = View.GONE
        }
    }


}
