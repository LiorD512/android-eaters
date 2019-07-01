package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.location_details_view.view.*

class AddressAndTimeView : LinearLayout {

    private var inputType: Int = 0
    private var listener: LocationDetailsViewListener? = null

    interface LocationDetailsViewListener {
        fun onTimeChange(str: String?)
        fun onLocationChange(str: String?)
    }

    fun setLocationDetailsViewListener(listener: LocationDetailsViewListener) {
        this.listener = listener
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.location_details_view, this, true)
    }

    fun getLocation(): String {
        return locationDetailsViewLocation.text.toString()
    }

    fun setLocation(text: String) {
        return locationDetailsViewLocation.setText(text)
    }

    fun getTime(): String {
        return locationDetailsViewTime.text.toString()
    }

    fun setTime(text: String) {
        return locationDetailsViewTime.setText(text)
    }

}