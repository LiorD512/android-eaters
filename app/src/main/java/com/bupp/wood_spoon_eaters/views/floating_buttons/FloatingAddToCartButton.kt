package com.bupp.wood_spoon_eaters.views.floating_buttons

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.databinding.FloatingAddToCartButtonBinding
import com.bupp.wood_spoon_eaters.databinding.FloatingCartButtonBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil

class FloatingAddToCartButton @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: FloatingAddToCartButtonBinding = FloatingAddToCartButtonBinding.inflate(LayoutInflater.from(context), this, true)

    fun updateAddToCartButton(price: String) {
        binding.floatingAddToCartPrice.text = price
    }

    fun updateButtonText(text: String){
        binding.floatingAddToCartTitle.text  = text
    }

    companion object{
        const val TAG = "wowFloatingAddCartButton"
    }


}
