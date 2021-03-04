package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.common.Constants
import kotlinx.android.synthetic.main.delivery_details_view.view.*
import kotlinx.android.synthetic.main.track_order_details_section.view.*

class DeliveryDetailsView : LinearLayout {

    var listener: DeliveryDetailsViewListener? = null

    interface DeliveryDetailsViewListener {
        fun onChangeLocationClick() {}
        fun onChangeTimeClick() {}
        fun onChangePaymentClick() {}
        fun onNationwideShippingChange() {}
    }

    fun setDeliveryDetailsViewListener(listener: DeliveryDetailsViewListener) {
        this.listener = listener
    }

    private var type: Int = 0
    private var changeable: Boolean = false
    private var isGrey: Boolean = false

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
            if (a.hasValue(R.styleable.DeliveryDetailsAttrs_changeable)) {
                changeable = a.getBoolean(R.styleable.DeliveryDetailsAttrs_changeable, true)
                setChangeable(changeable)
            }
            if (a.hasValue(R.styleable.DeliveryDetailsAttrs_isSelectionGray)) {
                isGrey = a.getBoolean(R.styleable.DeliveryDetailsAttrs_isSelectionGray, false)
                if(isGrey){
                    deliveryDetailsViewInput.setTextColor(ContextCompat.getColor(context, R.color.dark_50))
                }
            }
            if (a.hasValue(R.styleable.DeliveryDetailsAttrs_btnTitle)) {
                val btnText = a.getString(R.styleable.DeliveryDetailsAttrs_btnTitle)
                btnText?.let{
                    deliveryDetailsViewChangeBtn.text = btnText
                }
            }
        }

        deliveryDetailsViewChangeBtn.setOnClickListener { onChange() }

    }

    fun setChangeable(changeable: Boolean) {
        if (changeable) {
            deliveryDetailsViewChangeBtn.visibility = View.VISIBLE
            deliveryDetailsViewChangeBtn.isClickable = true
        } else {
            deliveryDetailsViewChangeBtn.visibility = View.INVISIBLE
            deliveryDetailsViewChangeBtn.isClickable = false
        }
    }

    private fun initUi(type: Int) {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_location)
                deliveryDetailsViewTitle.text = "Delivery Address"
            }
            Constants.DELIVERY_DETAILS_LOCATION_PROFILE -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_location)
                deliveryDetailsViewTitle.text = "Manage Addresses"
            }
            Constants.DELIVERY_DETAILS_NATIONWIDE_SHIPPING -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_location)
                deliveryDetailsViewTitle.text = "Nationwide Delivery Time"
                deliveryDetailsViewInput.text = "Select Delivery Method"
            }
            Constants.DELIVERY_DETAILS_TIME -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_time)
                deliveryDetailsViewTitle.text = "Delivery Time"
            }
            Constants.DELIVERY_DETAILS_CHECKOUT_DELIVERY -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_time)
                deliveryDetailsViewTitle.text = "Delivery"
            }
            Constants.DELIVERY_DETAILS_PAYMENT -> {
                deliveryDetailsViewIcon.setImageResource(R.drawable.icons_credit_card)
                deliveryDetailsViewTitle.text = "Payment Method"
            }
        }
    }


    private fun onChange() {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION, Constants.DELIVERY_DETAILS_LOCATION_PROFILE -> {
                listener?.onChangeLocationClick()
            }
            Constants.DELIVERY_DETAILS_TIME -> {
                listener?.onChangeTimeClick()
            }
            Constants.DELIVERY_DETAILS_PAYMENT -> {
                listener?.onChangePaymentClick()
            }
            Constants.DELIVERY_DETAILS_NATIONWIDE_SHIPPING -> {
                listener?.onNationwideShippingChange()
            }
        }
    }

    fun updateNationwideShippingDetails(input: String){
        deliveryDetailsViewInput.text = input
    }

    fun updateDeliveryDetails(input: String) {
        deliveryDetailsViewInput.text = input
    }

    fun updateDeliveryFullDetails(address: Address?) {
        address?.let{
            deliveryDetailsViewInput.text = address.streetLine1
            deliveryDetailsViewDelivery.text = address.getDropoffLocationStr()
            deliveryDetailsViewDelivery.visibility = View.VISIBLE

            deliveryDetailsViewInput.text = "${it.streetLine1}, #${it.streetLine2}"
            deliveryDetailsViewAptNumber.text = "${it.city?.name ?: ""}, ${it.state?.name ?: ""} ${it.zipCode}"
            deliveryDetailsViewAptNumber.visibility = View.VISIBLE

            address.notes?.let{
                deliveryDetailsViewNote.text = address.notes
                deliveryDetailsViewNote.visibility = View.VISIBLE
            }
        }
    }



}