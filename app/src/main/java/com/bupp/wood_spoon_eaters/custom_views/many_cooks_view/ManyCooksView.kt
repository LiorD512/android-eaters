package com.bupp.wood_spoon_eaters.custom_views.many_cooks_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.databinding.ManyCooksViewBinding
import com.bupp.wood_spoon_eaters.model.Cook


class ManyCooksView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), ManyCooksViewAdapter.ManyCooksViewListener {

    private var listener: ManyCooksViewListener? = null
    private var binding: ManyCooksViewBinding = ManyCooksViewBinding.inflate(LayoutInflater.from(context), this, true)

    interface ManyCooksViewListener {
        fun onCookClicked(clicked: Cook)
    }

    private lateinit var adapter: ManyCooksViewAdapter

    init{
        initUi()
    }

    private fun initUi() {
        binding.manyCooksViewList.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
    }

    fun initCooksView(cooks: ArrayList<Cook>, listener: ManyCooksViewListener) {
        this.listener = listener
        adapter = ManyCooksViewAdapter(context, cooks, this)
        binding.manyCooksViewList.adapter = adapter
    }


    override fun onCookViewClick(selected: Cook) {
        listener?.onCookClicked(selected)
    }

    fun setTitle(cooksTitle: String) {
        binding.manyCooksViewTitle.text = cooksTitle
    }

}