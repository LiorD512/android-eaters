package com.bupp.wood_spoon_eaters.views.feed_header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeedHeaderViewBinding

class FeedHeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: FeedHeaderViewBinding = FeedHeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    var listener: FeedHeaderViewListener? = null
    fun setFeedHeaderViewListener(listener: FeedHeaderViewListener){
        this.listener = listener
    }

    interface FeedHeaderViewListener{
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

    fun setDate(date: String?) {
        binding.feedHeaderDate.text = date ?: "When"
    }




    companion object{
        const val TAG = "wowFeedHeaderView"
    }



}
