package com.bupp.wood_spoon_eaters.views.feed_header

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeedHeaderViewBinding
import com.bupp.wood_spoon_eaters.databinding.FloatingCartButtonBinding
import java.text.DecimalFormat

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
        fun onHeaderTimeClick()
    }

    init {
         initUi(attrs)
    }


    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSLongBtn)
                attr.recycle()
            }
        }
    }




    companion object{
        const val TAG = "wowFeedHeaderView"
    }



}
