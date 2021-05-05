package com.bupp.wood_spoon_eaters.custom_views.stackableTextView

import android.R.drawable
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.stackable_text_view.view.*


class StackableTextView : FrameLayout {

    data class StackablesBundle(
        val stackables: ArrayList<SelectableIcon>
    )

    private lateinit var adapter: StackableTextViewAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.stackable_text_view, this, true)

        initUi()
    }

    fun initUi() {
        adapter = StackableTextViewAdapter(context)

        val layoutManager = FlexboxLayoutManager(context)

        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        stackableTextViewList.layoutManager = layoutManager

        stackableTextViewList.adapter = adapter
    }

    fun initStackableView(icons: List<SelectableIcon>){
        adapter.submitList(icons)
    }


}