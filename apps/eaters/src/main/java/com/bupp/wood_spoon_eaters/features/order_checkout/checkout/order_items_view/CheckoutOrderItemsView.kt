package com.bupp.wood_spoon_eaters.features.order_checkout.checkout.order_items_view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.CheckoutOrderItemsViewBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.order_items_view.CheckoutOrderItemsAdapter.CheckoutOrderItemsAdapterListener
import com.bupp.wood_spoon_eaters.databinding.OrderItemsViewBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.models.CheckoutAdapterItem
import com.bupp.wood_spoon_eaters.model.OrderItem


class CheckoutOrderItemsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var listener: CheckoutOrderItemsViewListener? = null

    interface CheckoutOrderItemsViewListener {
        fun onEditOrderBtnClicked()
    }

    private var binding: CheckoutOrderItemsViewBinding = CheckoutOrderItemsViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var adapterCheckout: CheckoutOrderItemsAdapter? = null

    init {
        initUi()
    }

    private fun initUi() {
        with(binding) {
            orderItemsViewRecyclerView.layoutManager = LinearLayoutManager(context)

            val divider: Drawable? = ContextCompat.getDrawable(context, R.drawable.line_divider)
            orderItemsViewRecyclerView.addItemDecoration(DividerItemDecorator(divider))

            orderItemsViewEditOrderBtn.setOnClickListener {
                listener?.onEditOrderBtnClicked()
            }
        }
    }

    fun initView(viewListener: CheckoutOrderItemsViewListener?, adapterListener: CheckoutOrderItemsAdapterListener?) {
        this.listener = viewListener
        adapterCheckout = CheckoutOrderItemsAdapter(adapterListener)
        binding.orderItemsViewRecyclerView.initSwipeableRecycler(adapterCheckout!!)
    }


    fun submitList(orderItems: List<CheckoutAdapterItem>) {
        adapterCheckout?.submitList(orderItems)
    }

    fun setBtnText(btnText: String){
        binding.orderItemsViewEditOrderBtn.setTitle(btnText)
    }

}