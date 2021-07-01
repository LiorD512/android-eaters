//package com.bupp.wood_spoon_eaters.features.main.order_details
//
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.databinding.OrderDetailsFragmentBinding
//import com.bupp.wood_spoon_eaters.model.Order
//import com.bupp.wood_spoon_eaters.utils.DateUtils
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class OrderDetailsFragment : Fragment(R.layout.order_details_fragment) {
//
//    lateinit var binding: OrderDetailsFragmentBinding
//    private lateinit var adapter: OrderDetailsAdapter
//    val viewModel by viewModel<OrderDetailsViewModel>()
//    private var orderId: Long = -1
//
//    companion object {
//        fun newInstance(orderId: Long) = OrderDetailsFragment().apply {
//            arguments = Bundle().apply {
//                putLong("orderId",orderId)
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.getLong("orderId",-1)?.let{
//            orderId = it
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding = OrderDetailsFragmentBinding.bind(view)
//
//        initUi()
//    }
//
//    private fun initUi() {
//        with(binding){
//            viewModel.orderDetails.observe(this@OrderDetailsFragment, Observer { orderDetails ->
//                orderHistoryPb.hide()
//                if (orderDetails.isSuccess) {
//                    handleOrderItems(orderDetails.order!!)
//                }
//            })
//
//            orderHistoryPb.show()
//            viewModel.getOrderDetails(orderId)
//        }
//
//    }
//
//    private fun handleOrderItems(curOrder: Order) {
//        with(binding){
//            orderDetailsFragDishesRecycler.layoutManager = LinearLayoutManager(context)
//            adapter = OrderDetailsAdapter(requireContext(), curOrder.orderItems!!)
//
//
//            var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//            divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
//            orderDetailsFragDishesRecycler.addItemDecoration(divider)
//
//            orderDetailsFragDishesRecycler.adapter = adapter
//
//            val firstOrderItem = curOrder.orderItems.first()
//            Glide.with(requireContext()).load(firstOrderItem.dish.thumbnail).into(orderDetailsFragDishImage)
//
//            val tax: Double? = curOrder.tax?.value
//            val serviceFee = curOrder.serviceFee?.value
//            val deliveryFee = curOrder.deliveryFee?.value
//            val discount = curOrder.discount?.value
//            val smallOrderFee = curOrder.minPrice?.value
//
//            orderDetailsFragTax.text = "$$tax"
//            orderDetailsFragServiceFee.text = "$$serviceFee"
//            orderDetailsFragDeliveryFee.text = "$$deliveryFee"
//
//            smallOrderFee?.let{
//                if(it > 0){
//                    orderDetailsSmallOrderFeeLayout.visibility = View.VISIBLE
//                    orderDetailsSmallOrderFeeSep.visibility = View.VISIBLE
//                    orderDetailsSmallOrderFee.text = "$$it"
//                }
//            }
//
//            orderDetailsFragPromoDiscount.text = "$$discount" //todo - add this when server ready
//            orderDetailsFragOrderId.text = "${curOrder.orderNumber}"
//
//            orderDetailsFragCookName.text = "By cook ${curOrder.cook?.getFullName()}"
//
//            if(curOrder.estDeliveryTime != null){
//                val date = DateUtils.parseDateToUsDate(curOrder.estDeliveryTime)
//                val time = DateUtils.parseDateToUsTime(curOrder.estDeliveryTime)
//                orderDetailsFragOrderDate.text = "$date at $time"
//            }else{
//                orderDetailsFragOrderDate.text = "${curOrder.estDeliveryTimeText}"
//            }
//
//            orderDetailsFragTotalPrice.text = curOrder.total?.formatedValue
//
//            if(curOrder.status.equals("cancelled")){
//                orderDetailsStatus.text = "Order Cancelled"
//                orderDetailsStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
//            }
//        }
//
//
//    }
//}
