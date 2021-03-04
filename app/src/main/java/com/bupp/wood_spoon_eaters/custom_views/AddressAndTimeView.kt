package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.databinding.AddressAndTimeViewBinding


class AddressAndTimeView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: AddressAndTimeViewBinding = AddressAndTimeViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var listener: AddressAndTimeViewListener? = null
    interface AddressAndTimeViewListener{
        fun onAddressClick()
        fun onTimeClick()
    }

    fun setAddressAndTimeViewListener(listener: AddressAndTimeViewListener){
        this.listener = listener

        binding.locationDetailsViewLocation.setOnClickListener {
            this.listener?.onAddressClick()
        }

        binding.locationDetailsViewTimeLayout.setOnClickListener {
            this.listener?.onTimeClick()
        }
    }


    fun setLocation(text: String?) {
        binding.locationDetailsViewLocation.text = text ?: "Address"
    }

    fun setTime(text: String?) {
        binding.locationDetailsViewTime.text = text ?: "Now"
    }

    fun enableLocationClick(isEnabled: Boolean) {
        binding.locationDetailsViewLocation.isEnabled = isEnabled
    }


}