package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.blue_btn.view.*
import kotlinx.android.synthetic.main.plus_minus_view.view.*


class PlusMinusView : FrameLayout {

    private var listener: PlusMinusInterface? = null
    private var position: Int = -1
    private var quantityLeft: Int = -1

    fun setPlusMinusListener(listener: PlusMinusInterface, position: Int = 0, initalCounter: Int = 0, quantityLeft: Int = 1) {
        this.listener = listener
        this.position = position
        this.counter = initalCounter
        this.quantityLeft = quantityLeft
        plusMinusCounter.text = "$counter"
    }

    fun updateCounterUiOnly(count: Int) {
        counter = count
        plusMinusCounter.text = "$counter"
    }

    interface PlusMinusInterface {
        fun onPlusMinusChange(counter: Int, position: Int)
    }

    var counter: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.plus_minus_view, this, true)

        plusMinusMinus.setOnClickListener {
            if (counter > 0) {
                counter--
                plusMinusCounter.text = "$counter"
                listener?.onPlusMinusChange(counter, position)

                if(counter == quantityLeft){
                    plusMinusPlus.isEnabled = false
                    plusMinusPlus.alpha = 0.5f
                }else{
                    plusMinusPlus.isEnabled = true
                    plusMinusPlus.alpha = 1f
                }
            }
        }
        plusMinusPlus.setOnClickListener {
            counter++
            plusMinusCounter.text = "$counter"
            listener?.onPlusMinusChange(counter, position)

            if(counter == quantityLeft){
                plusMinusPlus.isEnabled = false
                plusMinusPlus.alpha = 0.5f
            }else{
                plusMinusPlus.isEnabled = true
                plusMinusPlus.alpha = 1f
            }
        }
    }

}