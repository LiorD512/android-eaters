package com.bupp.wood_spoon_eaters.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.CustomDetailsViewBinding

@SuppressLint("CustomViewStyleable")
class CustomDetailsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: CustomDetailsViewBinding = CustomDetailsViewBinding.inflate(LayoutInflater.from(context), this, true)


    var listener: CustomDetailsViewListener? = null

    interface CustomDetailsViewListener {
        fun onCustomDetailsClick(type: Int) {}
    }

    fun setDeliveryDetailsViewListener(listener: CustomDetailsViewListener) {
        this.listener = listener
    }

    private var type: Int = 0
    private var changeable: Boolean = false
    private var isGrey: Boolean = false

    init {
        with(binding) {
            if (attrs != null) {
                val a = context.obtainStyledAttributes(attrs, R.styleable.CustomDetailsAttrs)
                if (a.hasValue(R.styleable.CustomDetailsAttrs_detailsType)) {
                    type = a.getInt(R.styleable.CustomDetailsAttrs_detailsType, Constants.DELIVERY_DETAILS_LOCATION)
                    initUi(type)
                }
                if (a.hasValue(R.styleable.CustomDetailsAttrs_changeable)) {
                    changeable = a.getBoolean(R.styleable.CustomDetailsAttrs_changeable, true)
                    setChangeable(changeable)
                }

                val showSep = a.getBoolean(R.styleable.CustomDetailsAttrs_showSep, true)
                handleSep(showSep)

                if (a.hasValue(R.styleable.CustomDetailsAttrs_isSelectionGray)) {
                    isGrey = a.getBoolean(R.styleable.CustomDetailsAttrs_isSelectionGray, false)
                    if (isGrey) {
                        customDetailsViewSubtitle.setTextColor(ContextCompat.getColor(context, R.color.dark_50))
                    }
                }
                if (a.hasValue(R.styleable.CustomDetailsAttrs_btnTitle)) {
                    val btnText = a.getString(R.styleable.CustomDetailsAttrs_btnTitle)
                    btnText?.let {
                        customDetailsViewChangeBtn.setTitle(btnText)
                    }
                }
                if (a.hasValue(R.styleable.CustomDetailsAttrs_title)) {
                    val title = a.getString(R.styleable.CustomDetailsAttrs_title)
                    title?.let {
                        customDetailsViewTitle.text = it
                    }
                }

                val icon = a.getDrawable(R.styleable.CustomDetailsAttrs_WSicon)
                setIcon(icon)

                a.recycle()
            }
            customDetailsViewChangeBtn.setOnClickListener { onChange() }

        }
    }


    private fun setIcon(icon: Drawable?) {
        icon?.let{
            binding.customDetailsViewIcon.setImageDrawable(icon)
            binding.customDetailsViewIcon.visibility = View.VISIBLE
        }
    }

    private fun handleSep(showSep: Boolean) {
        if(!showSep){
            binding.customDetailsViewSep.visibility = View.GONE
        }
    }

    fun setChangeable(changeable: Boolean) {
        with(binding){
            if (changeable) {
                customDetailsViewChangeBtn.visibility = View.VISIBLE
                customDetailsViewChangeBtn.isClickable = true
            } else {
                customDetailsViewChangeBtn.visibility = View.INVISIBLE
                customDetailsViewChangeBtn.isClickable = false
            }
        }
    }

    private fun initUi(type: Int) {
        with(binding) {
            when (type) {
                Constants.DELIVERY_DETAILS_LOCATION -> {
                    customDetailsViewIcon.setImageResource(R.drawable.ic_icons_location_bold)
                    customDetailsViewTitle.text = "Delivery address"
                }
                Constants.DELIVERY_DETAILS_LOCATION_PROFILE -> {
                    customDetailsViewIcon.setImageResource(R.drawable.ic_icons_location_bold)
                    customDetailsViewTitle.text = "Manage addresses"
                }
                Constants.DELIVERY_DETAILS_NATIONWIDE_SHIPPING -> {
                    customDetailsViewIcon.setImageResource(R.drawable.icons_time)
                    customDetailsViewTitle.text = "Nationwide delivery time"
                    customDetailsViewSubtitle.text = "Select delivery method"
                }
                Constants.DELIVERY_DETAILS_TIME -> {
                    customDetailsViewIcon.setImageResource(R.drawable.icons_time)
                    customDetailsViewTitle.text = "Delivery time"
                }
                Constants.DELIVERY_DETAILS_CHECKOUT_DELIVERY -> {
                    customDetailsViewIcon.setImageResource(R.drawable.icons_time)
                    customDetailsViewTitle.text = "Delivery"
                }
                Constants.DELIVERY_DETAILS_PAYMENT -> {
                    customDetailsViewIcon.setImageResource(R.drawable.icons_credit_card)
                    customDetailsViewTitle.text = "Payment method"
                }
                Constants.DELIVERY_DETAILS_PROMO_CODE -> {
                    customDetailsViewIcon.setImageResource(R.drawable.icons_promo)
                    customDetailsViewTitle.text = "Promo code"
                    customDetailsViewSubtitle.text = "Enter a WoodSpoon promo code"
                }
            }
        }
    }


    private fun onChange() {
        listener?.onCustomDetailsClick(type)

    }

    fun updateSubTitle(input: String) {
        binding.customDetailsViewSubtitle.text = input
    }

    fun updateDeliveryDetails(input: String) {
        binding.customDetailsViewTitle.text = "Delivery Time"
        binding.customDetailsViewSubtitle.text = input
    }

    fun updateDeliveryFullDetails(address: Address?) {
        with(binding){
            address?.let {
                val floor = address.addressSlug
                customDetailsViewTitle.text = "${it.streetLine1}, #${it.streetLine2}"
                val city = it.city?.name?.let{"${it},"} ?: ""
                val state = it.state?.name?.let{"${it},"} ?: ""
                customDetailsViewSubtitle.text = "$city $state ${it.zipCode ?: ""}"
                address.notes?.let {
                    customDetailsViewExtraText.text = address.notes
                    customDetailsViewExtraText.visibility = View.VISIBLE
                }
            }
        }
    }


}