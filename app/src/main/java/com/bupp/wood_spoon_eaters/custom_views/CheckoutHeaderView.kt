package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.provider.SyncStateContract
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

    fun updateHeaderUi(title: String?, subtitle: String?, iconType: Int){
        setTitle(title)
        setSubtitle(subtitle)
        initUi(iconType)
    }

    fun setOnIconClickListener(function: () -> Unit) {
        binding.checkoutHeaderBtn.setOnClickListener{
            function.invoke()
        }
    }

    private fun setTitle(title: String?) {
        binding.checkoutHeaderTitle.text = title
        binding.checkoutHeaderTitle.isVisible = !title.isNullOrEmpty()
    }

    private fun setSubtitle(subtitle: String?) {
        binding.checkoutHeaderSubtitle.text = subtitle
        binding.checkoutHeaderSubtitle.isVisible = !subtitle.isNullOrEmpty()
    }

    private fun initUi(iconType: Int) {
        when(iconType){
            Constants.HEADER_ICON_BACK -> {
                binding.checkoutHeaderBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back_grey))
            }
            Constants.HEADER_ICON_CLOSE -> {
                binding.checkoutHeaderBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close_grey))
            }
        }
    }

}