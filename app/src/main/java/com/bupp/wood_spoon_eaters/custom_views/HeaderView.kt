package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.header_view.view.*


class HeaderView : FrameLayout {

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

    var listener: HeaderViewListener? = null

    fun setHeaderViewListener(listenerInstance: HeaderViewListener) {
        listener = listenerInstance
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
    }

    fun setType(type: Int?, title: String? = "") {
        initUi(type)
        headerViewTitle.text = title
    }

    private fun initClicks() {
        headerViewBackBtn.setOnClickListener {
            listener?.onHeaderBackClick()
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
        headerViewProfileBtn.setOnClickListener {
            listener?.onHeaderProfileClick()
        }
    }

    private fun initUi(type: Int?) {
        hideAll()
        when (type) {
            Constants.HEADER_VIEW_TYPE_FEED -> {
                headerViewSearchBtn.visibility = View.VISIBLE
                headerViewProfileBtn.visibility = View.VISIBLE
                headerViewLocationDetailsView.visibility = View.VISIBLE
            }
            Constants.HEADER_VIEW_TYPE_SEARCH -> {
                headerViewBackBtn.visibility = View.VISIBLE
                headerViewFilterBtn.visibility = View.VISIBLE
            }
            Constants.HEADER_VIEW_TYPE_SIGNUP -> {
                headerViewTitle.visibility = View.VISIBLE
                headerViewSkipBtn.visibility = View.VISIBLE
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
            Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_SAVE -> {
                headerViewTitle.visibility = VISIBLE
                headerViewSaveBtn.visibility = View.VISIBLE
                headerViewCloseBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun hideAll() {
        headerViewTitle.visibility = GONE
        headerViewCloseBtn.visibility = View.GONE
        headerViewBackBtn.visibility = View.GONE
        headerViewProfileBtn.visibility = View.GONE
        headerViewDoneBtn.visibility = View.GONE
        headerViewSaveBtn.visibility = View.GONE
        headerViewSearchBtn.visibility = View.GONE
        headerViewFilterBtn.visibility = View.GONE
    }

    fun setLocationTitle(time: String?,location: String?){
        //todo - fix this by sending objects?
        headerViewLocationDetailsView.setTime(time!!)
        headerViewLocationDetailsView.setLocation(location!!)
    }

    fun isSkipable(isSkipable: Boolean) {
        if(isSkipable){
            headerViewSkipBtn.visibility = View.VISIBLE
        }else{
            headerViewSkipBtn.visibility = View.GONE
        }
    }
}