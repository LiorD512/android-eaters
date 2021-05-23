package com.bupp.wood_spoon_eaters.custom_views.stackableTextView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.databinding.StackableTextViewBinding
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class StackableTextView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr){

    private var binding: StackableTextViewBinding = StackableTextViewBinding.inflate(LayoutInflater.from(context), this, true)


    data class StackablesBundle(
        val stackables: ArrayList<SelectableIcon>
    )

    private lateinit var adapter: StackableTextViewAdapter

    init{
        initUi()
    }

    fun initUi() {
        with(binding){
            adapter = StackableTextViewAdapter(context)

            val layoutManager = FlexboxLayoutManager(context)

            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            stackableTextViewList.layoutManager = layoutManager

            stackableTextViewList.adapter = adapter
        }
    }

    fun initStackableView(icons: List<SelectableIcon>){
        adapter.submitList(icons)
    }


}