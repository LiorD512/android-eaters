package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.HeaderViewBinding
import com.bupp.wood_spoon_chef.utils.AnimationUtil

class HeaderView  @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr){

    private var binding: HeaderViewBinding = HeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.HeaderViewAttrs)
            if (a.hasValue(R.styleable.HeaderViewAttrs_title)) {
                val title = a.getString(R.styleable.HeaderViewAttrs_title)
                binding.headerViewTitle.text = title
            }
            if (a.hasValue(R.styleable.HeaderViewAttrs_type)) {
                val type = a.getInt(R.styleable.HeaderViewAttrs_type, Constants.HEADER_VIEW_TYPE_TITLE)
                initUi(type)
            }
            if (a.hasValue(R.styleable.HeaderViewAttrs_backIcon)) {
                val icon = a.getResourceId(
                    R.styleable.HeaderViewAttrs_backIcon,
                    0
                )
                setBackButtonIcon(icon)
            }

            val isWithSep = a.getBoolean(R.styleable.HeaderViewAttrs_isWithSep, true)
            if(!isWithSep){
                binding.headerViewSep.visibility = View.GONE
            }
            a.recycle()
        }

        initClicks()
    }

    var listener: HeaderViewListener? = null

    fun setHeaderViewListener(listenerInstance: HeaderViewListener){
        listener = listenerInstance
    }


    interface HeaderViewListener{
        fun onHeaderBackClick(){}
        fun onHeaderSaveClick(){}
        fun onHeaderDoneClick(){}
        fun onHeaderNextClick(){}
        fun onHeaderSettingsClick(){}
        fun onHeaderCloseClick(){}
        fun onHeaderAddClick(){}
    }

    fun setType(type: Int?, title: String?){
        binding.headerViewTitle.text = title
        initUi(type)
    }

    private fun initClicks() {
        with(binding) {
            headerViewBackBtn.setOnClickListener {
                listener?.onHeaderBackClick()
            }
            headerViewDoneBtn.setOnClickListener {
                listener?.onHeaderDoneClick()
            }
            headerViewNextBtn.setOnClickListener {
                listener?.onHeaderNextClick()
            }
            headerViewSaveBtn.setOnClickListener {
                listener?.onHeaderSaveClick()
            }
            headerViewSettingsBtn.setOnClickListener {
                listener?.onHeaderSettingsClick()
            }
            headerViewCloseBtn.setOnClickListener {
                listener?.onHeaderCloseClick()
            }
            headerViewAddBtn.setOnClickListener {
                listener?.onHeaderAddClick()
            }
        }
    }

    private fun initUi(type: Int?) {
        with(binding){
            hideAll()
            when (type) {

                Constants.HEADER_VIEW_TYPE_TITLE -> {
                }
                Constants.HEADER_VIEW_TYPE_TITLE_BACK -> {
                    headerViewBackBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_TITLE_CLOSE_DONE -> {
                    headerViewCloseBtn.visibility = View.VISIBLE
                    headerViewDoneBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_TITLE_CLOSE_NEXT -> {
                    headerViewNextBtn.visibility = View.VISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_TITLE_CLOSE -> {
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_TITLE_BACK_SAVE -> {
                    headerViewSaveBtn.visibility = View.VISIBLE
                    headerViewBackBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_TITLE_NOTIFICATIONS_SETTINGS -> {
                    headerViewSettingsBtn.visibility = View.VISIBLE
//                headerViewNotificationBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_TITLE_BACK_NEXT -> {
                    headerViewBackBtn.visibility = View.VISIBLE
                    headerViewNextBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_TITLE_ADD -> {
                    headerViewAddBtn.visibility = View.VISIBLE
                    AnimationUtil().enterFromRight(headerViewAddBtn)
                }
            }
        }
    }

    private fun hideAll() {
        with(binding) {
            headerViewBackBtn.visibility = View.GONE
            headerViewDoneBtn.visibility = View.GONE
            headerViewNextBtn.visibility = View.GONE
            headerViewCloseBtn.visibility = View.GONE
            headerViewSettingsBtn.visibility = View.GONE
            if(headerViewAddBtn.alpha > 0){
                AnimationUtil().exitToRight(headerViewAddBtn)
            }
        }
    }

    fun setTitle(title: String){
        binding.headerViewTitle.text = title
    }

    fun setBackButtonIcon(@DrawableRes backIcon: Int){
        binding.headerViewBackBtn.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    backIcon
                )
            )
        }
    }


}