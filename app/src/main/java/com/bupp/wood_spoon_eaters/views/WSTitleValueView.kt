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
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSTitleValueView)

                val title = attr.getString(R.styleable.WSTitleValueView_title)
                setTitle(title)

                val value = attr.getString(R.styleable.WSTitleValueView_subTitle)
                setValue(value)

                val toolTip = attr.getInt(R.styleable.WSTitleValueView_tip_type, 0)
                if(toolTip != 0){
                    titleValueViewToolTip.customInit(context, attrs)
                    titleValueViewToolTip.visibility = View.VISIBLE
                }

                attr.recycle()
            }
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
