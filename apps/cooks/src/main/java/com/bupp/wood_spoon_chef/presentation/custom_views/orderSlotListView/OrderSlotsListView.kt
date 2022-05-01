package com.bupp.wood_spoon_chef.presentation.custom_views.orderSlotListView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.OrderSlotsListViewBinding
import com.bupp.wood_spoon_chef.data.remote.model.OrderItem

class OrderSlotsListView : LinearLayout{

    private var binding: OrderSlotsListViewBinding = OrderSlotsListViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.order_slots_list_view, this, true)
    }

    fun init(items: List<OrderItem>?) {
        if(items != null){
            binding.orderSlotsList.layoutManager = LinearLayoutManager(context)
            val adapter = OrderSlotListAdapter(context!!, items)
            binding.orderSlotsList.adapter = adapter
        }
    }

}