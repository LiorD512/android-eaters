package com.bupp.wood_spoon_eaters.features.main.order_details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.order_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderDetailsFragment() : Fragment() {

    private lateinit var adapter: OrderDetailsAdapter
    val viewModel by viewModel<OrderDetailsViewModel>()
    private var orderId: Long = -1

    companion object {
        fun newInstance(orderId: Long) = OrderDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong("orderId",orderId)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getLong("orderId",-1)?.let{
            orderId = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.order_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {

        viewModel.orderDetails.observe(this, Observer { orderDetails ->
            orderHistoryPb.hide()
            if (orderDetails.isSuccess) {
                handleOrderItems(orderDetails.order!!)
            }
        })

        orderHistoryPb.show()
        viewModel.getOrderDetails(orderId)

    }

    private fun handleOrderItems(curOrder: Order) {
        orderDetailsFragDishesRecycler.layoutManager = LinearLayoutManager(context)
        adapter = OrderDetailsAdapter(context!!, curOrder.orderItems)


        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        orderDetailsFragDishesRecycler.addItemDecoration(divider)

        orderDetailsFragDishesRecycler.adapter = adapter

        val firstOrderItem = curOrder.orderItems.first()
        Glide.with(context!!).load(firstOrderItem.dish.thumbnail).apply(RequestOptions.circleCropTransform()).into(orderDetailsFragDishImage)

        val tax: Double = curOrder.tax.value
        val serviceFee = curOrder.serviceFee.value
        val deliveryFee = curOrder.deliveryFee.value
        val discount = curOrder.discount.value
        val smallOrderFee = curOrder.minPrice?.value

        orderDetailsFragTax.text = "$$tax"
        orderDetailsFragServiceFee.text = "$$serviceFee"
        orderDetailsFragDeliveryFee.text = "$$deliveryFee"

        smallOrderFee?.let{
            if(it > 0){
                orderDetailsSmallOrderFeeLayout.visibility = View.VISIBLE
                orderDetailsSmallOrderFeeSep.visibility = View.VISIBLE
                orderDetailsSmallOrderFee.text = "$$it"
            }
        }

        orderDetailsFragPromoDiscount.text = "$$discount" //todo - add this when server ready
        orderDetailsFragOrderId.text = "${curOrder.orderNumber}"

        orderDetailsFragCookName.text = "By cook ${curOrder.cook.getFullName()}"
        val date = Utils.parseDDateToUsDate(curOrder.estDeliveryTime)
        val time = Utils.parseDDateToUsTime(curOrder.estDeliveryTime)
        orderDetailsFragOrderDate.text = "$date at $time"

        orderDetailsFragTotalPrice.text = curOrder.total.formatedValue

        if(curOrder.status.equals("cancelled")){
            orderDetailsStatus.text = "Order Cancelled"
            orderDetailsStatus.setTextColor(ContextCompat.getColor(context!!, R.color.red))
        }


    }
}
