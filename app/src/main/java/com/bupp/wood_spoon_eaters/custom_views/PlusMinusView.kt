package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.PlusMinusViewBinding
import com.bupp.wood_spoon_eaters.databinding.WsEditTextBinding
import kotlinx.android.synthetic.main.blue_btn.view.*
import kotlinx.android.synthetic.main.plus_minus_view.view.*


class PlusMinusView@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: PlusMinusViewBinding = PlusMinusViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var counter: Int = 0
    private var listener: PlusMinusInterface? = null
    private var position: Int = -1
    private var quantityLeft: Int = -1
    private var canReachZero: Boolean = true

    fun setPlusMinusListener(listener: PlusMinusInterface, position: Int = 0, initialCounter: Int = 0, quantityLeft: Int? = 1, canReachZero: Boolean = true) {
        Log.d("wowPlusMinus","initialCounter $initialCounter, quantityLeft: $quantityLeft")
        this.listener = listener
        this.position = position
        this.counter = initialCounter
        this.canReachZero = canReachZero

        quantityLeft?.let{
            this.quantityLeft = it
        }
        plusMinusCounter.text = "$counter"

        if(counter >= this.quantityLeft){
            handlePlus(false)
        }

        if(!canReachZero && counter == 1){
            handleMinus(false)
        }

        initUi()
    }

    fun updateCounterUiOnly(count: Int) {
        counter = count
        plusMinusCounter.text = "$counter"
    }

    fun setViewEnabled(isEnabled: Boolean) {
        handleMinus(isEnabled)
        handlePlus(isEnabled)
    }

    interface PlusMinusInterface {
        fun onPlusMinusChange(counter: Int, position: Int)
    }


    private fun initUi() {
        plusMinusMinus.setOnClickListener {
            if (counter > 0) {
                if(!canReachZero && counter == 1){
                    handleMinus(false)
                }else{
                    counter--
                    plusMinusCounter.text = "$counter"
                    listener?.onPlusMinusChange(counter, position)

                    if(counter == quantityLeft){
                        handlePlus(false)
                    }else{
                        handlePlus(true)
                    }
                }
            }
        }
        plusMinusPlus.setOnClickListener {
            counter++
            plusMinusCounter.text = "$counter"
            listener?.onPlusMinusChange(counter, position)

            if(counter >= quantityLeft){
                handlePlus(false)
            }else{
                handlePlus(true)
            }
            handleMinus(true)
        }
    }

    private fun handlePlus(isEnabled: Boolean){
        plusMinusPlus.isEnabled = isEnabled
        if(isEnabled){
            plusMinusPlus.alpha = 1f
            plusMinusCounter.alpha = 1f
        }else{
            plusMinusPlus.alpha = 0.5f
            plusMinusCounter.alpha = 0.5f
        }
    }

    private fun handleMinus(isEnabled: Boolean){
        plusMinusMinus.isEnabled = isEnabled
        if(isEnabled){
            plusMinusMinus.alpha = 1f
//            plusMinusCounter.alpha = 1f
        }else{
            plusMinusMinus.alpha = 0.5f
//            plusMinusCounter.alpha = 0.5f
        }
    }

}