package com.bupp.wood_spoon_eaters.custom_views.stackableTextView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import kotlinx.android.synthetic.main.stackable_view_item.view.*

class StackableViewItem : LinearLayout {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.stackable_view_item, this, true)
    }

    fun init(items: ArrayList<SelectableIcon>?) {
        if(items != null) {
            if (items.size > 0)
                stackableViewItem1.text = items[0].name
            if (items.size > 1){
                stackableViewItem2.text = items[1].name
            }else{
                stackableViewItem2Layout.visibility = View.GONE
            }
            if (items.size > 2){
                stackableViewItem3.text = items[2].name
            }else{
                stackableViewItem3Layout.visibility = View.GONE
            }
        }
    }

}