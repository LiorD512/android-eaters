package com.bupp.wood_spoon_eaters.views.floating_cart_button

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.databinding.FloatingCartButtonBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import java.text.DecimalFormat

class FloatingCartButton @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: FloatingCartButtonBinding = FloatingCartButtonBinding.inflate(LayoutInflater.from(context), this, true)
    private var isShowing = false

    var listener: FloatingCartButtonListener? = null
    fun setFloatingCartBtnListener(listener: FloatingCartButtonListener){
        this.listener = listener
    }

    interface FloatingCartButtonListener{
        fun onFloatingCartStateChanged(isShowing: Boolean)
    }

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

    fun updateFloatingCartButton(restaurantName: String, quantity: Int) {
        Log.d(TAG, "updateFloatingCartButton: $quantity")
        if(quantity > 0){
            binding.floatingCartBtnTitle.text = "$restaurantName"
            binding.floatingCartBtnPrice.text = "$quantity"
            binding.floatingCartBtnLayout.visibility = View.VISIBLE
            if(!isShowing){
                isShowing = true
                AnimationUtil().enterFromBottomWithAlpha(binding.floatingCartBtnLayout)
            }
            listener?.onFloatingCartStateChanged(isShowing)
        }else {
            if(isShowing){
                AnimationUtil().exitToBottomWithAlpha(binding.floatingCartBtnLayout)
            }
            isShowing = false
            listener?.onFloatingCartStateChanged(isShowing)
        }
    }

    fun hide(){
        binding.floatingCartBtnLayout.visibility = View.GONE
        listener?.onFloatingCartStateChanged(false)
    }


    companion object{
        const val TAG = "wowFloatingCartButton"
    }



}
