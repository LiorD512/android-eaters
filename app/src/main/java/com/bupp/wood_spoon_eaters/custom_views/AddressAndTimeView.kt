package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.address_and_time_view.view.*

class AddressAndTimeView : LinearLayout {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.address_and_time_view, this, true)
    }


    fun setLocation(text: String?) {
        locationDetailsViewLocation.text = text ?: "updating location..."
    }

    fun setTime(text: String?) {
        locationDetailsViewTime.text = text ?: "ASAP"
    }

}