package com.bupp.wood_spoon_eaters.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.WsEditTextBinding
import com.bupp.wood_spoon_eaters.databinding.WsLongBtnBinding
import com.bupp.wood_spoon_eaters.databinding.WsTitleValueViewBinding
import com.bupp.wood_spoon_eaters.utils.Utils

class WSTitleValueView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsTitleValueViewBinding = WsTitleValueViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
         initUi(attrs)
    }


    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {

            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSLongBtn)

//                val title = attr.getString(R.styleable.WSLongBtn_title)
//                longBtnTitle.text = title
//
//                val icon = attr.getDrawable(R.styleable.WSLongBtn_WSicon)
//                setIcon(icon)
//
//                val showSep = attr.getBoolean(R.styleable.WSLongBtn_showSep, true)
//                handleSep(showSep)

                attr.recycle()
            }
        }
    }


    private fun setIcon(icon: Drawable?) {
//        icon?.let{
//            binding.longBtnIcon.setImageDrawable(icon)
//            binding.longBtnIcon.visibility = View.VISIBLE
//        }
    }


    private fun handleSep(showSep: Boolean) {
//        if(!showSep){
//            binding.longBtnSep.visibility = View.GONE
//        }
    }


}
