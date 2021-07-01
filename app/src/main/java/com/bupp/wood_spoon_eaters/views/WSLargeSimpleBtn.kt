package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.WsCounterEditTextBinding
import com.bupp.wood_spoon_eaters.databinding.WsLargeSimpleBtnBinding
import com.bupp.wood_spoon_eaters.databinding.WsSimpleBtnBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils

class WSLargeSimpleBtn @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsLargeSimpleBtnBinding = WsLargeSimpleBtnBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSSimpleBtn)

                val title = attr.getString(R.styleable.WSSimpleBtn_title)
                title?.let { setTitle(it) }

                attr.recycle()
            }
        }
    }

    fun setTitle(title: String) {
        with(binding){
            wsSimpleBtn.text = title
        }
    }

}
