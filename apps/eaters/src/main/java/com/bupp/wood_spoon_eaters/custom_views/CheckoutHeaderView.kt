package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.CheckoutHeaderViewBinding


class CheckoutHeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr){

    private var binding: CheckoutHeaderViewBinding = CheckoutHeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    var listener: CheckoutHeaderListener? = null
    interface CheckoutHeaderListener{
        fun onCloseBtnClick(){}
        fun onBackBtnClick(){}
    }

    init{
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CheckoutHeaderView)

            if (a.hasValue(R.styleable.CheckoutHeaderView_title)) {
                val title = a.getString(R.styleable.CheckoutHeaderView_title)
                setTitle(title)
            }
            if (a.hasValue(R.styleable.CheckoutHeaderView_subtitle)) {
                val subtitle = a.getString(R.styleable.CheckoutHeaderView_subtitle)
                setSubtitle(subtitle)
            }
            if (a.hasValue(R.styleable.CheckoutHeaderView_iconType)) {
                val iconType = a.getInt(R.styleable.CheckoutHeaderView_iconType, Constants.HEADER_ICON_CLOSE)
                initUi(iconType)
            }
            a.recycle()
        }

    }



    fun setCheckoutHeaderListener(listener: CheckoutHeaderListener) {
        this.listener = listener
    }

    fun setTitle(title: String?) {
        binding.checkoutHeaderTitle.text = title
        binding.checkoutHeaderTitle.isVisible = !title.isNullOrEmpty()
    }

    fun setSubtitle(subtitle: String?) {
        binding.checkoutHeaderSubtitle.text = subtitle
        binding.checkoutHeaderSubtitle.isVisible = !subtitle.isNullOrEmpty()
    }

    private fun initUi(iconType: Int) {
        when(iconType){
            Constants.HEADER_ICON_BACK -> {
                binding.checkoutHeaderBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back_grey))
                binding.checkoutHeaderBtn.setOnClickListener {
                    listener?.onBackBtnClick()
                }
            }
            Constants.HEADER_ICON_CLOSE -> {
                binding.checkoutHeaderBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close_grey))
                binding.checkoutHeaderBtn.setOnClickListener {
                    listener?.onCloseBtnClick()
                }
            }
        }


    }

}