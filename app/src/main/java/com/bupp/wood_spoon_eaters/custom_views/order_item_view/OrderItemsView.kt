//package com.bupp.wood_spoon_eaters.custom_views.order_item_view
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
//import com.bupp.wood_spoon_eaters.databinding.OrderItemsViewBinding
//import com.bupp.wood_spoon_eaters.model.OrderItem
//
//
//class OrderItemsView @JvmOverloads
//constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
//    LinearLayout(context, attrs, defStyleAttr), OrderItemsViewAdapter.OrderItemsViewAdapterListener {
//
//    private var listener: OrderItemsListener? = null
//    interface OrderItemsListener{
//        fun onAddBtnClicked()
//        fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {}
//    }
//
//    private var binding: OrderItemsViewBinding = OrderItemsViewBinding.inflate(LayoutInflater.from(context), this, true)
//    private var adapter: OrderItemsViewAdapter? = null
//
//    init{
//        if (attrs != null) {
//            val a = context.obtainStyledAttributes(attrs, R.styleable.OrderItemsView)
//
//            val showAddBtn = a.getBoolean(R.styleable.OrderItemsView_showAddBtn, true)
//            if (!showAddBtn) {
//                binding.orderItemsViewAddBtn.visibility = GONE
//            }
//        }
//
//        initUi()
//    }
//
//    private fun initUi() {
//        with(binding){
//            orderItemsViewRecyclerView.layoutManager = LinearLayoutManager(context)
//            val divider = DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider))
//            orderItemsViewRecyclerView.addItemDecoration(divider)
//
//            orderItemsViewAddBtn.setOnClickListener{
//                listener?.onAddBtnClicked()
//            }
//        }
//    }
//
//    fun setOrderItems(context: Context, orderItems: List<OrderItem>, listener: OrderItemsListener? = null) {
//        this.listener = listener
//        adapter = OrderItemsViewAdapter(context, this)
//        binding.orderItemsViewRecyclerView.adapter = adapter
//        adapter!!.submitList(orderItems)
//    }
//
//    override fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {
//        listener?.onDishCountChange(curOrderItem, isOrderItemsEmpty)
//    }
//
//
//}