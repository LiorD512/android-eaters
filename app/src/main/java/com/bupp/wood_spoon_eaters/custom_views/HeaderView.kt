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
import com.bupp.wood_spoon_eaters.views.UserImageVideoView


class HeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), UserImageVideoView.UserImageVideoViewListener{

    private var binding: HeaderViewBinding = HeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    var watcher: AutoCompleteTextWatcher? = getAutoCompleteTextWatcher()

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
        fun handleHeaderSep(shouldShow: Boolean){}
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
            headerViewProfileBtn.setUserImageVideoViewListener(this@HeaderView)

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
            override fun handleInputString(str: String) {
                Log.d("wowHeaderView", "afterTextChanged: $str")
                listener?.onHeaderTextChange(str)
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
                    headerViewBackBtn.visibility = View.VISIBLE
                    headerViewSearchLayout.visibility = View.VISIBLE
                    headerViewFilterBtn.visibility = View.VISIBLE
                    headerViewFilterBtn.alpha = 0.5f
                }
                Constants.HEADER_VIEW_TYPE_SIGNUP -> {
                    headerViewTitle.visibility = View.VISIBLE
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
                Constants.HEADER_VIEW_TYPE_CLOSE_NO_TITLE -> {
                    headerViewCloseBtn.visibility = View.VISIBLE
                    listener?.handleHeaderSep(false)

                }
                Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_DONE -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewDoneBtn.visibility = View.VISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
                Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_NEXT -> {
                    headerViewTitle.visibility = VISIBLE
                    headerViewNextBtn.visibility = View.VISIBLE
                    headerViewCloseBtn.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    private fun hideAll() {
        with(binding) {
            headerViewTitle.visibility = GONE
            headerViewCloseBtn.visibility = View.GONE
            headerViewBackBtn.visibility = View.GONE
            headerViewDoneBtn.visibility = View.GONE
            headerViewSaveBtn.visibility = View.GONE
            headerViewNextBtn.visibility = View.GONE
            headerViewFeedLayout.visibility = View.GONE
            headerViewSearchLayout.visibility = View.GONE
            listener?.handleHeaderSep(true)
        }
    }

    fun setTitle(title: String) {
        binding.headerViewTitle.text = title
    }

}