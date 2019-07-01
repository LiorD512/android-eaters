package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.location_details_view.view.*

class AddressAndTimeView : LinearLayout {

    private var listener: LocationDetailsViewListener? = null

    interface LocationDetailsViewListener {
        fun onTimeChange(str: String?)
        fun onLocationChange(str: String?)

        fun onLocationClick()
        fun onTimeClick()
    }

    fun setLocationDetailsViewListener(listener: LocationDetailsViewListener) {
        this.listener = listener
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.location_details_view, this, true)

        locationDetailsViewLocation.setOnClickListener {
            listener?.onLocationClick()
        }

        locationDetailsViewTime.setOnClickListener {
            listener?.onTimeClick()
        }
    }

    fun getLocation(): String {
        return locationDetailsViewLocation.text.toString()
    }

    fun setLocation(text: String) {
        listener?.onLocationChange(text)
        return locationDetailsViewLocation.setText(text)
    }

    fun getTime(): String {
        return locationDetailsViewTime.text.toString()
    }

    fun setTime(text: String) {
        listener?.onTimeChange(text)
        return locationDetailsViewTime.setText(text)
    }

}