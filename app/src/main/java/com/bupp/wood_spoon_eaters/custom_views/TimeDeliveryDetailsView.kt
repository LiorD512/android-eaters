package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.time_delivery_details_view.view.*

class TimeDeliveryDetailsView : LinearLayout {

    var listener: TimeDeliveryDetailsViewListener? = null

    interface TimeDeliveryDetailsViewListener {
        fun onChangeTimeClick() {}
        fun onChangeTimeAsap()
    }

    fun setTimeDeliveryDetailsViewListener(listener: TimeDeliveryDetailsViewListener) {
        this.listener = listener
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.time_delivery_details_view, this, true)
        initUi()
    }



    private fun initUi() {
        timeDetailsViewAsap.setOnClickListener {
            listener?.onChangeTimeAsap()
            onAsap() }
        timeDetailsViewSchedule.setOnClickListener {
            listener?.onChangeTimeClick()
            onSchedule() }
    }

    private fun onAsap() {
        timeDetailsViewAsapV.visibility = View.VISIBLE
        timeDetailsViewScheduleV.visibility = View.GONE
    }

    private fun onSchedule() {
        timeDetailsViewAsapV.visibility = View.GONE
        timeDetailsViewScheduleV.visibility = View.VISIBLE
    }

    fun updateDeliveryDetails(input: String) {
        onSchedule()
        timeDetailsViewScheduleText.text = input
    }


}