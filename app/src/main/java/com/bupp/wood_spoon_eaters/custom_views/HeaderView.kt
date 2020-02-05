package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.text_watcher.AutoCompleteTextWatcher
import kotlinx.android.synthetic.main.header_view.view.*
import kotlinx.android.synthetic.main.input_title_view.view.*


class HeaderView : FrameLayout, UserImageView.UserImageViewListener {

    protected var watcher: AutoCompleteTextWatcher? = getAutoCompleteTextWatecher()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.header_view, this, true)

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
                headerViewSep.visibility = View.GONE
            }
            a.recycle()
        }

        initClicks()
    }

    private var type: Int? = -1
    var listener: HeaderViewListener? = null

    fun setHeaderViewListener(listenerInstance: HeaderViewListener, eater: Eater? = null) {
        if(eater != null){
            headerViewProfileBtn.setUser(eater)
        }
        listener = listenerInstance
    }

    fun refreshUserUi(eater: Eater? = null){
        if(eater != null){
            headerViewProfileBtn.setUser(eater)
        }
    }

    interface HeaderViewListener {
        fun onHeaderBackClick() {}
        fun onHeaderDoneClick() {}
        fun onHeaderSaveClick() {}
        fun onHeaderSkipClick() {}
        fun onHeaderCloseClick() {}
        fun onHeaderSearchClick() {}
        fun onHeaderFilterClick() {}
        fun onHeaderProfileClick() {}
        fun onHeaderTextChange(str: String) {}
        fun onHeaderAddressAndTimeClick() {}
        fun onHeaderSettingsClick() {}
    }

    fun setType(type: Int?, title: String? = "") {
        this.type = type
        initUi(type)
        headerViewTitle.text = title
    }

    private fun initClicks() {
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
        headerViewProfileBtn.setUserImageViewListener(this)

        headerViewSearchClean.setOnClickListener {
            headerViewSearchInput.text.clear()
        }

        headerViewSettingsBtn.setOnClickListener {
            listener?.onHeaderSettingsClick()
        }

        headerViewAddressAndTime.setOnClickListener {
            listener?.onHeaderAddressAndTimeClick()
        }

        setTitleInputListener()
    }

    private fun setTitleInputListener(){
        headerViewSearchInput.addTextChangedListener(watcher)
    }

    private fun getAutoCompleteTextWatecher(): AutoCompleteTextWatcher{
        return object : AutoCompleteTextWatcher() {
            override fun handleInputString(str: String) {
                Log.d("wowHeaderView", "afterTextChanged: $str")
                listener?.onHeaderTextChange(str)
            }
        }
    }

    private fun checkInputState() {
        if(headerViewSearchInput.text.isEmpty()){
            listener?.onHeaderBackClick()
        }else{
            headerViewSearchInput.text.clear()
            listener?.onHeaderTextChange("")
        }
    }

    override fun onUserImageClick(cook: Cook?) {
        listener?.onHeaderProfileClick()
    }

    private fun initUi(type: Int?) {
        hideAll()
        when (type) {
            Constants.HEADER_VIEW_TYPE_FEED -> {
                headerViewLocationLayout.visibility = View.VISIBLE
            }
            Constants.HEADER_VIEW_TYPE_SEARCH -> {
                headerViewSep.visibility = View.GONE
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
                headerViewSaveBtn.isEnabled = false
            }
            Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_SAVE -> {
                headerViewTitle.visibility = VISIBLE
                headerViewSaveBtn.visibility = View.VISIBLE
                headerViewCloseBtn.visibility = View.VISIBLE
            }
            Constants.HEADER_VIEW_TYPE_BACK_TITLE_SETTINGS -> {
                headerViewTitle.visibility = VISIBLE
                headerViewSettingsBtn.visibility = View.VISIBLE
                headerViewBackBtn.visibility = View.VISIBLE
            }
            Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_DONE -> {
                headerViewTitle.visibility = VISIBLE
                headerViewDoneBtn.visibility = View.VISIBLE
                headerViewCloseBtn.visibility = View.VISIBLE
            }
            Constants.HEADER_VIEW_TYPE_EVENT -> {
                headerViewLocationLayout.visibility = View.VISIBLE
                headerViewSearchBtn.visibility = View.INVISIBLE
                headerViewProfileBtn.visibility = View.INVISIBLE
                headerViewCloseBtn.visibility = View.VISIBLE
                headerViewAddressAndTime.setEnabled(false)
                headerViewAddressAndTime.alpha = 0.5f
            }
        }
    }

    private fun hideAll() {
        headerViewAddressAndTime.alpha = 1.0f
        headerViewTitle.visibility = GONE
        headerViewCloseBtn.visibility = View.GONE
        headerViewBackBtn.visibility = View.GONE
        headerViewDoneBtn.visibility = View.GONE
        headerViewSaveBtn.visibility = View.GONE
        headerViewSettingsBtn.visibility = View.GONE
        headerViewLocationLayout.visibility = View.GONE
        headerViewSearchLayout.visibility = View.GONE
    }

    fun setLocationTitle(time: String? = null, location: String? = null) {
            headerViewAddressAndTime.setTime(time)
            headerViewAddressAndTime.setLocation(location)
    }

    fun isSkipable(isSkipable: Boolean) {
        if (isSkipable) {
            headerViewSkipBtn.visibility = View.VISIBLE
        } else {
            headerViewSkipBtn.visibility = View.GONE
        }
    }

    fun updateSearchTitle(str: String) {
        headerViewSearchInput.removeTextChangedListener(watcher)
        headerViewSearchInput.setText(str)
        setTitleInputListener()
    }

    fun setSaveButtonClickable(isClickable: Boolean) {
        headerViewSaveBtn.isEnabled = isClickable
    }

    fun setDoneButtonClickable(isEnabled: Boolean) {
        headerViewDoneBtn.isEnabled = isEnabled
    }

    fun updateFilterUi(isEnabled: Boolean) {
        headerViewFilterBtn.isSelected = isEnabled
    }

    fun setTitle(title: String) {
        headerViewTitle.text = title
    }
}