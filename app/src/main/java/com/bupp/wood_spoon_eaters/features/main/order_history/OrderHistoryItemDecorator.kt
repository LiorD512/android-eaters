package com.bupp.wood_spoon_eaters.features.main.order_history

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*

class OrderHistoryItemDecorator(val context: Context) : ItemDecoration() {

    private val defaultSep = ContextCompat.getDrawable(context, R.drawable.sep_silver)
    private val defaultPadding = Utils.toPx(15)

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width
        parent.adapter?.let { adapter ->
            val childCount = Objects.requireNonNull(adapter).itemCount
            for (i in 0 until childCount) {
//               Log.d(TAG, "i: $i")
                val child = parent.getChildAt(i)
                child?.let{
                   when(child.id){
                       R.id.orderHistoryArchiveMainLayout -> {
                           val params = child.layoutParams as RecyclerView.LayoutParams
                           val top = child.bottom + params.bottomMargin
                           val bottom = top + defaultSep!!.intrinsicHeight
                           defaultSep.setBounds(left + defaultPadding, top, right - defaultPadding, bottom)
                           defaultSep.draw(canvas)
                       }
                }
               }

            }
        }
    }


    companion object{
        const val TAG = "wowOrderHistoryDeco"
    }
}