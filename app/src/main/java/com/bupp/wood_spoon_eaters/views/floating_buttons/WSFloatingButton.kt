package com.bupp.wood_spoon_eaters.views.floating_buttons

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.WsFloatingButtonBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil

class WSFloatingButton @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: WsFloatingButtonBinding = WsFloatingButtonBinding.inflate(LayoutInflater.from(context), this, true)
    private var isShowing = false

    var listener: WSFloatingButtonListener? = null
    fun setWSFloatingBtnListener(listener: WSFloatingButtonListener) {
        this.listener = listener
        listener.onFloatingCartStateChanged(isShowing)
    }

    interface WSFloatingButtonListener {
        fun onFloatingCartStateChanged(isShowing: Boolean)
    }

    init {
        initUi(attrs)
    }


    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSFloatingButton)

                val type = attr.getInt(R.styleable.WSFloatingButton_floating_button_type, 0)
                type.let { updateUiByType(it) }

                attr.recycle()
            }
        }
    }

    private fun updateUiByType(type: Int) {
        with(binding) {
            when (type) {
                Constants.VIEW_CART -> {
                    //default state
                }
                Constants.ADD_TO_CART -> {
                    wsFloatingBtnSubTitle.visibility = View.GONE
                    wsFloatingBtnIcon.visibility = View.INVISIBLE
                    wsFloatingBtnTitle.text = "Add to cart"
                    show()
                }
                Constants.PLACE_ORDER -> {
                    wsFloatingBtnSubTitle.visibility = View.GONE
                    wsFloatingBtnIcon.visibility = View.INVISIBLE
                    wsFloatingBtnTitle.text = "Place an order"
                    show()
                }
                else -> {
                }
            }
        }
    }

    fun updateFloatingCartButton(restaurantName: String, quantity: Int) {
        Log.d(TAG, "updateFloatingCartButton: $quantity")
        if (quantity > 0) {
            binding.wsFloatingBtnSubTitle.text = "$restaurantName"
            binding.wsFloatingBtnPrice.text = "$quantity"
            binding.floatingCartBtnLayout.visibility = View.VISIBLE
            if (!isShowing) {
                show()
            }
        } else {
            hide()
        }
    }

    fun show() {
        isShowing = true
        AnimationUtil().enterFromBottomWithAlpha(binding.floatingCartBtnLayout)
        listener?.onFloatingCartStateChanged(isShowing)
        binding.floatingCartBtnLayout.visibility = View.VISIBLE
    }

    fun hide() {
        isShowing = false
        AnimationUtil().exitToBottomWithAlpha(binding.floatingCartBtnLayout)
        binding.floatingCartBtnLayout.visibility = View.GONE
        listener?.onFloatingCartStateChanged(false)
    }

    fun updateFloatingBtnPrice(price: String) {
        binding.wsFloatingBtnPrice.text = price
    }

    fun updateFloatingBtnTitle(text: String) {
        binding.wsFloatingBtnTitle.text = text
    }


    companion object {
        const val TAG = "wowFloatingCartButton"
    }


}
