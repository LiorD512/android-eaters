package com.bupp.wood_spoon_eaters.custom_views.many_cooks_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.model.Cook
import kotlinx.android.synthetic.main.many_cooks_view.view.*


class ManyCooksView : FrameLayout, ManyCooksViewAdapter.ManyCooksViewListener {

    private var listener: ManyCooksViewListener? = null

    interface ManyCooksViewListener {
        fun onCookClicked(clicked: Cook)
    }

    private lateinit var adapter: ManyCooksViewAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.many_cooks_view, this, true)

        initUi()
    }

    private fun initUi() {
        manyCooksViewList.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        var divider = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        divider.setDrawable(resources.getDrawable(R.drawable.horizontal_trans_divider, null))
        manyCooksViewList.addItemDecoration(divider)
    }

    fun initCooksView(cooks: ArrayList<Cook>, listener: ManyCooksViewListener) {
        this.listener = listener
        adapter = ManyCooksViewAdapter(context, cooks, this)
        manyCooksViewList.adapter = adapter
    }


    override fun onCookViewClick(selected: Cook) {
        listener?.onCookClicked(selected)
    }

    fun setTitle(cooksTitle: String) {
        manyCooksViewTitle.text = cooksTitle
    }

}