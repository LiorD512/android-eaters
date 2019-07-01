package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.delivery_details_view.view.*

class DeliveryDetailsView : LinearLayout{

    var listener: DeliveryDetailsViewListener? = null
    interface DeliveryDetailsViewListener{
        fun onChangeLocationClick()
        fun onChangeTimeClick()
    }

    fun setDeliveryDetailsViewListener(listener: DeliveryDetailsViewListener){
        this.listener = listener
    }

    private var type: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.delivery_details_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DeliveryDetailsAttrs)
            if (a.hasValue(R.styleable.DeliveryDetailsAttrs_detailsType)) {
                type = a.getInt(R.styleable.DeliveryDetailsAttrs_detailsType, Constants.DELIVERY_DETAILS_LOCATION)
                initUi(type)
            }
        }

        deliveryDetailsViewChangeBtn.setOnClickListener { onChange() }

    }

    private fun initUi(type: Int) {
        when(type){
            Constants.DELIVERY_DETAILS_LOCATION -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_location)
                deliveryDetailsViewTitle.text = "Delivery Address"
            }
            Constants.DELIVERY_DETAILS_TIME -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_time)
                deliveryDetailsViewTitle.text = "When"
            }
        }
    }


    private fun onChange() {
        when(type){
            Constants.DELIVERY_DETAILS_LOCATION -> {
                listener?.onChangeLocationClick()
            }
            Constants.DELIVERY_DETAILS_TIME -> {
                listener?.onChangeTimeClick()
            }
        }
    }

    fun updateDeliveryDetails(address: String? = null, time: String? = null){
        if(address != null){
            deliveryDetailsViewInput.text = address
        }else{
            deliveryDetailsViewInput.text = time
        }
    }



}