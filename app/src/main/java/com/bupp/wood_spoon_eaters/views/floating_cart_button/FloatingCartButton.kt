package com.bupp.wood_spoon_eaters.views.floating_cart_button

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
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
import com.bupp.wood_spoon_eaters.databinding.FloatingCartButtonBinding
import com.bupp.wood_spoon_eaters.databinding.WsEditTextBinding
import com.bupp.wood_spoon_eaters.databinding.WsLongBtnBinding
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import java.text.DecimalFormat

class FloatingCartButton @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: FloatingCartButtonBinding = FloatingCartButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
         initUi(attrs)
    }


    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            with(binding) {
//                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSLongBtn)
//                attr.recycle()
            }
        }
    }

    fun updateFloatingCartButton(price: Double) {
        Log.d(TAG, "updateFloatingCartButton: $price")
        val priceStr = DecimalFormat("##.##").format(price)
        binding.floatingCartBtnPrice.text = "$$priceStr"
    }


    companion object{
        const val TAG = "wowFloatingCartButton"
    }



}
