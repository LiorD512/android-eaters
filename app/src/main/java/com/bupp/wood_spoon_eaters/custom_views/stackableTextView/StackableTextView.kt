package com.bupp.wood_spoon_eaters.custom_views.stackableTextView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.SelectableIcon
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
        stackableTextViewList.layoutManager = LinearLayoutManager(context)
        adapter = StackableTextViewAdapter(context)
        stackableTextViewList.adapter = adapter
    }

    fun initStackableView(icons: ArrayList<SelectableIcon>){
        val bundles: ArrayList<StackablesBundle> = arrayListOf()
        var shouldStop = false
        var i = 0
        while(!shouldStop && i < icons.size){
            val iconsBundle: ArrayList<SelectableIcon> = arrayListOf()
            repeat(3){
                if(i < icons.size){
                    iconsBundle.add(icons.get(i))
                }else{
                    shouldStop = true
                }
                i++
            }

            val stackableBundle: StackablesBundle = StackablesBundle(iconsBundle)
            bundles.add(stackableBundle)
        }
        adapter.addItem(bundles)
        adapter.notifyDataSetChanged()
    }
}