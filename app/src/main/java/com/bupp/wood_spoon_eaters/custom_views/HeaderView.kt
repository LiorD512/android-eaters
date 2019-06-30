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
                var type = a.getInt(R.styleable.HeaderViewAttrs_type, Constants.HEADER_VIEW_TYPE_TITLE)
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
        fun onHeaderCloseClick() {}
        fun onHeaderBackClick() {}
        fun onHeaderProfileClick() {}
        fun onHeaderSkipClick() {}
        fun onHeaderSaveClick() {}
        fun onHeaderSearchClick() {}
        fun onHeaderFilterClick() {}
    }

    fun setType(type: Int?, title: String?) {
        headerViewTitle.text = title
        initUi(type)
    }

    private fun initClicks() {
        headerViewCloseBtn.setOnClickListener {
            listener?.onHeaderCloseClick()
        }
        headerViewBackBtn.setOnClickListener {
            listener?.onHeaderBackClick()
        }
        headerViewProfileBtn.setOnClickListener {
            listener?.onHeaderProfileClick()
        }
        headerViewSkipBtn.setOnClickListener {
            listener?.onHeaderSkipClick()
        }
        headerViewSaveBtn.setOnClickListener {
            listener?.onHeaderSaveClick()
        }
        headerViewSearchBtn.setOnClickListener {
            listener?.onHeaderSearchClick()
        }
        headerViewFilterBtn.setOnClickListener {
            listener?.onHeaderFilterClick()
        }
    }

    private fun initUi(type: Int?) {
        hideAll()
        when (type) {
            Constants.HEADER_VIEW_TYPE_TITLE -> {
                headerViewTitle.visibility = VISIBLE

            }
            Constants.HEADER_VIEW_TYPE_TITLE_SKIP -> {
                headerViewSkipBtn.visibility = View.VISIBLE
                headerViewTitle.visibility = VISIBLE
            }
            Constants.HEADER_VIEW_TYPE_IMAGE_LOCATION_SEARCH -> {
                headerViewLocationDetailsView.visibility = View.VISIBLE
                headerViewTitle.visibility = GONE
            }
        }
    }

    private fun hideAll() {
        headerViewCloseBtn.visibility = View.GONE
        headerViewBackBtn.visibility = View.GONE
        headerViewProfileBtn.visibility = View.GONE
        headerViewSkipBtn.visibility = View.GONE
        headerViewSaveBtn.visibility = View.GONE
        headerViewSearchBtn.visibility = View.GONE
        headerViewFilterBtn.visibility = View.GONE
    }

    fun setLocationTitle(time: String?,location: String?){
        headerViewLocationDetailsView.setTime(time!!)
        headerViewLocationDetailsView.setLocation(location!!)
    }
}