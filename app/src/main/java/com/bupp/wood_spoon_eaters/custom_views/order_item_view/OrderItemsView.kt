package com.bupp.wood_spoon_eaters.custom_views.order_item_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.model.OrderItem
import kotlinx.android.synthetic.main.order_items_view.view.*


class OrderItemsView : LinearLayout {

    var adapter: OrderItemsViewAdapter? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.order_items_view, this, true)

        initUi()
    }

    private fun initUi() {
        orderItemsViewRecyclerView.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider))

        orderItemsViewRecyclerView.addItemDecoration(divider)
    }

    fun setOrderItems(context: Context, orderItems: List<OrderItem>, listener: OrderItemsViewAdapter.OrderItemsViewAdapterListener) {
        adapter = OrderItemsViewAdapter(context, listener)
        orderItemsViewRecyclerView.adapter = adapter
        adapter!!.submitList(orderItems)
//        ingredientsAdapter?.setOrderItemRequests()
    }

//    fun getAllDishPriceValue(): Double {
//        if(adapter != null){
//            return adapter!!.getAllDishPriceValue()
//        }else{
//            return 0.0
//        }
//    }
//
//    fun getOrderItemsQuantity(): Int {
//        if (adapter != null) {
//            return adapter!!.getOrderItemsQuantity()
//        } else {
//            return 0
//        }
//    }

}