package com.bupp.wood_spoon_eaters.views.feed_header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.databinding.FeedHeaderViewBinding
import com.bupp.wood_spoon_eaters.utils.DateUtils

class FeedHeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: FeedHeaderViewBinding = FeedHeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    var listener: FeedHeaderViewListener? = null
    fun setFeedHeaderViewListener(listener: FeedHeaderViewListener) {
        this.listener = listener
    }

    interface FeedHeaderViewListener {
        fun onHeaderAddressClick()
        fun onHeaderDateClick()
    }

    init {
        initUi(attrs)
    }


    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSLongBtn)
                attr.recycle()

                feedHeaderAddress.setOnClickListener { listener?.onHeaderAddressClick() }
                feedHeaderAddressArrow.setOnClickListener { listener?.onHeaderAddressClick() }
                feedHeaderDate.setOnClickListener { listener?.onHeaderDateClick() }
                feedHeaderDateArrow.setOnClickListener { listener?.onHeaderDateClick() }
            }
        }
    }

    fun setAddress(setAddress: String?) {
        binding.feedHeaderAddress.text = setAddress ?: "Address"
    }

    fun setDate(deliveryTimeParam: SingleColumnTimePickerBottomSheet.DeliveryTimeParam?) {
        var dateStr = "when"
        deliveryTimeParam?.let {
            when (deliveryTimeParam.deliveryTimeType) {
                SingleColumnTimePickerBottomSheet.DeliveryType.TODAY -> {
                    dateStr = "Today"
                }
                SingleColumnTimePickerBottomSheet.DeliveryType.FUTURE -> {
                    dateStr = deliveryTimeParam.date?.let { DateUtils.parseDateToDayDateNumberOrToday(it) } ?: "ERROR"
                }
                else -> {} //DeliveryType.NON_FILTERED
            }
        }
        binding.feedHeaderDate.text = dateStr
    }


    companion object {
        const val TAG = "wowFeedHeaderView"
    }


}
