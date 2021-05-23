package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.AutoCompleteTextWatcher
import com.bupp.wood_spoon_eaters.databinding.HeaderViewBinding
import com.bupp.wood_spoon_eaters.views.UserImageView


class HeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), UserImageView.UserImageViewListener, AddressAndTimeView.AddressAndTimeViewListener {

    private var binding: HeaderViewBinding = HeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    protected var watcher: AutoCompleteTextWatcher? = getAutoCompleteTextWatcher()

    init{
        with(binding){
            if (attrs != null) {
                val a = context.obtainStyledAttributes(attrs, R.styleable.HeaderViewAttrs)
                if (a.hasValue(R.styleable.HeaderViewAttrs_title)) {
                    var title = a.getString(R.styleable.HeaderViewAttrs_title)
                    headerViewTitle.text = title
                }
                if (a.hasValue(R.styleable.HeaderViewAttrs_type)) {
                    var type = a.getInt(R.styleable.HeaderViewAttrs_type, Constants.HEADER_VIEW_TYPE_FEED)
                    initUi(type)
                }
                var isWithSep = a.getBoolean(R.styleable.HeaderViewAttrs_isWithSep, true)
                if (!isWithSep) {
    //                headerViewSep.visibility = View.GONE
                }
                a.recycle()
            }

            initClicks()
        }
    }

    private var type: Int? = -1
    var listener: HeaderViewListener? = null

    fun setHeaderViewListener(listenerInstance: HeaderViewListener, eater: Eater? = null) {
        if(eater != null){
            binding.headerViewProfileBtn.setUser(eater)
        }
        listener = listenerInstance
        binding.headerViewAddressAndTime.setAddressAndTimeViewListener(this)
    }

    fun refreshUserUi(eater: Eater? = null){
        if(eater != null){
            binding.headerViewProfileBtn.setUser(eater)
        }
    }

    interface HeaderViewListener {
        fun onHeaderBackClick() {}
        fun onHeaderDoneClick() {}
        fun onHeaderSaveClick() {}
        fun onHeaderNextClick() {}
        fun onHeaderSkipClick() {}
        fun onHeaderCloseClick() {}
        fun onHeaderSearchClick() {}
        fun onHeaderFilterClick() {}
        fun onHeaderProfileClick() {}
        fun onHeaderTextChange(str: String) {}
        fun onHeaderAddressClick() {}
        fun onHeaderTimeClick() {}
        fun onHeaderSettingsClick() {}
    }

    fun setType(type: Int?, title: String? = "") {
        this.type = type
        with(binding){
            initUi(type)
            headerViewTitle.text = title
        }
    }

    private fun initClicks() {
        with(binding){
            headerViewBackBtn.setOnClickListener {
                when(type){
                    Constants.HEADER_VIEW_TYPE_SEARCH -> {
                        checkInputState()
                    }
                    else -> listener?.onHeaderBackClick()
                }
            }
            headerViewDoneBtn.setOnClickListener {
                listener?.onHeaderDoneClick()
            }
            headerViewSkipBtn.setOnClickListener {
                listener?.onHeaderSkipClick()
            }
            headerViewSaveBtn.setOnClickListener {
                listener?.onHeaderSaveClick()
            }
            headerViewCloseBtn.setOnClickListener {
                listener?.onHeaderCloseClick()
            }
            headerViewSearchBtn.setOnClickListener {
                listener?.onHeaderSearchClick()
            }
            headerViewFilterBtn.setOnClickListener {
                listener?.onHeaderFilterClick()
            }
            headerViewNextBtn.setOnClickListener {
                listener?.onHeaderNextClick()
            }
            headerViewProfileBtn.setUserImageViewListener(this@HeaderView)

            headerViewSearchClean.setOnClickListener {
                headerViewSearchInput.text.clear()
            }

            setTitleInputListener()
        }
    }

    private fun setTitleInputListener(){
        binding.headerViewSearchInput.addTextChangedListener(watcher)
    }

    private fun getAutoCompleteTextWatcher(): AutoCompleteTextWatcher {
        return object : AutoCompleteTextWatcher(450) {
            override fun handleInputString(input: String) {
                Log.d("wowHeaderView", "afterTextChanged: $input")
                listener?.onHeaderTextChange(input)
            }
        }
    }

    private fun checkInputState() {
        with(binding){
            if(headerViewSearchInput.text.isEmpty()){
                listener?.onHeaderBackClick()
            }else{
                headerViewSearchInput.text.clear()
                listener?.onHeaderTextChange("")
            }
        }
    }

    override fun onUserImageClick(cook: Cook?) {
        listener?.onHeaderProfileClick()
    }

    private fun initUi(type: Int?) {
        hideAll()
        with(binding) {
            when (type) {
                Constants.HEADER_VIEW_TYPE_FEED -> {
                    headerViewFeedLayout.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_SEARCH -> {
//                headerViewSep.visibility = View.GONE
                    headerViewBackBtn.visibility = View.VISIBLE
                    headerViewSearchLayout.visibility = View.VISIBLE
                    headerViewFilterBtn.visibility = View.VISIBLE
                    headerViewFilterBtn.alpha = 0.5f
                }
                Constants.HEADER_VIEW_TYPE_SIGNUP -> {
                    headerViewTitle.visibility = View.VISIBLE
//                headerViewSkipBtn.visibility = View.VISIBLE
                    headerViewBackBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_BACK_TITLE -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewBackBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_BACK_TITLE_DONE -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewBackBtn.visibility = View.VISIBLE
                    headerViewDoneBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewBackBtn.visibility = View.VISIBLE
                    headerViewSaveBtn.visibility = View.VISIBLE
//                headerViewSaveBtn.isEnabled = false
                }
                Constants.HEADER_VIEW_TYPE_CLOSE_TITLE -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_SAVE -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewSaveBtn.visibility = View.VISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_BACK_TITLE_SETTINGS -> {
                    headerViewTitle.visibility = VISIBLE
//                headerViewSettingsBtn.visibility = View.VISIBLE
                    headerViewBackBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_DONE -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewDoneBtn.visibility = View.VISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_EVENT -> {
                    headerViewFeedLayout.visibility = View.VISIBLE
                    headerViewSearchBtn.visibility = View.INVISIBLE
                    headerViewProfileBtn.visibility = View.INVISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                    headerViewAddressAndTime.setEnabled(false)
                    headerViewAddressAndTime.alpha = 0.5f
                }
                Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_NEXT -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewNextBtn.visibility = View.VISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideAll() {
        with(binding) {
            headerViewAddressAndTime.alpha = 1.0f
            headerViewTitle.visibility = GONE
            headerViewCloseBtn.visibility = View.GONE
            headerViewBackBtn.visibility = View.GONE
            headerViewDoneBtn.visibility = View.GONE
            headerViewSaveBtn.visibility = View.GONE
            headerViewNextBtn.visibility = View.GONE
//        headerViewSettingsBtn.visibility = View.GONE
            headerViewFeedLayout.visibility = View.GONE
            headerViewSearchLayout.visibility = View.GONE
        }
    }

    fun setLocationTitle(location: String? = null) {
        binding.headerViewAddressAndTime.setLocation(location)
    }
    fun setDeliveryTime(time: String?) {
        binding.headerViewAddressAndTime.setTime(time)
    }

    fun isSkipable(isSkipable: Boolean) {
        with(binding){
            if (isSkipable) {
                headerViewSkipBtn.visibility = View.VISIBLE
            } else {
                headerViewSkipBtn.visibility = View.GONE
            }
        }
    }

    fun updateSearchTitle(str: String) {
        with(binding){
            headerViewSearchInput.removeTextChangedListener(watcher)
            headerViewSearchInput.setText(str)
            setTitleInputListener()
        }
    }

    fun setSaveButtonClickable(isClickable: Boolean) {
        with(binding){
            headerViewSaveBtn.isEnabled = isClickable
            headerViewSaveBtn.isSelected = isClickable
        }
    }

    fun setDoneButtonClickable(isEnabled: Boolean) {
        with(binding){
            headerViewDoneBtn.isEnabled = isEnabled
        }
    }

    fun updateFilterUi(isEnabled: Boolean) {
        binding.headerViewFilterBtn.isSelected = isEnabled
    }

    fun setTitle(title: String) {
        binding.headerViewTitle.text = title
    }

    override fun onAddressClick() {
        listener?.onHeaderAddressClick()
    }

    override fun onTimeClick() {
        listener?.onHeaderTimeClick()
    }

    fun enableLocationClick(isEnable: Boolean) {
        binding.headerViewAddressAndTime.enableLocationClick(isEnable)
    }
}