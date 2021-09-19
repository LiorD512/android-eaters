package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.PlusMinusViewBinding
import com.bupp.wood_spoon_eaters.utils.Utils


class PlusMinusView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: PlusMinusViewBinding = PlusMinusViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var counter: Int = 0
    private var listener: PlusMinusInterface? = null
    private var position: Int = -1
    private var quantityLeft: Int = -1
    private var canReachZero: Boolean = true

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.PlusMinusView)

            val iconSize = attr.getInt(R.styleable.PlusMinusView_iconsSize, 0)
            updateIconSize(iconSize)

            attr.recycle()
        }
    }

    private fun updateIconSize(iconSize: Int) {
        with(binding) {
            val width = Utils.toPx(iconSize)
            val height = Utils.toPx(iconSize)
            val layoutParams = LayoutParams(width,height)
            plusMinusPlus.layoutParams = layoutParams
            plusMinusPlus.layoutParams = layoutParams

            plusMinusMinus.layoutParams = layoutParams
            plusMinusMinus.layoutParams = layoutParams
        }
    }

    fun initSimplePlusMinus(listener: PlusMinusInterface, initialCounter: Int = 0, quantityLeft: Int = 1) {
        Log.d("setSimplePlusMinus", "initialCounter $initialCounter, quantityLeft: $quantityLeft")
        this.listener = listener
        this.counter = initialCounter
        this.quantityLeft = quantityLeft
        this.canReachZero = false

        binding.plusMinusCounter.text = "$counter"

        if (counter >= this.quantityLeft) {
            handlePlus(false)
        }

        if (!canReachZero && counter == 1) {
            handleMinus(false)
        }

        initUi()
    }


    interface PlusMinusInterface {
        fun onPlusMinusChange(counter: Int, position: Int)
    }


    private fun initUi() {
        binding.plusMinusMinus.setOnClickListener {
            if (counter > 0) {
                counter--
                binding.plusMinusCounter.text = "$counter"
                listener?.onPlusMinusChange(counter, position)
                Utils.vibrate(context, 50)

                if (counter == quantityLeft) {
                    handlePlus(false)
                } else {
                    handlePlus(true)
                }
                if (!canReachZero && counter == 1) {
                    handleMinus(false)
                }
            }
        }
        binding.plusMinusPlus.setOnClickListener {
            counter++
            binding.plusMinusCounter.text = "$counter"
            listener?.onPlusMinusChange(counter, position)
            Utils.vibrate(context, 50)

            if (counter >= quantityLeft) {
                handlePlus(false)
            } else {
                handlePlus(true)
            }
            handleMinus(true)
        }

    }

    private fun handlePlus(isEnabled: Boolean) {
        binding.plusMinusPlus.isEnabled = isEnabled
        if (isEnabled) {
            binding.plusMinusPlus.alpha = 1f
        } else {
            binding.plusMinusPlus.alpha = 0.5f
        }
    }

    private fun handleMinus(isEnabled: Boolean) {
        binding.plusMinusMinus.isEnabled = isEnabled
        if (isEnabled) {
            binding.plusMinusMinus.alpha = 1f
        } else {
            binding.plusMinusMinus.alpha = 0.5f
        }
    }

}