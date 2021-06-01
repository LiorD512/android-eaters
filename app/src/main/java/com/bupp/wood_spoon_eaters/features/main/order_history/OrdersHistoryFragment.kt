package com.bupp.wood_spoon_eaters.features.main.order_history


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.FragmentOrdersHistoryBinding
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrdersHistoryFragment: Fragment(R.layout.fragment_orders_history), HeaderView.HeaderViewListener,
    OrdersHistoryAdapter.OrdersHistoryAdapterListener {

    lateinit var binding: FragmentOrdersHistoryBinding
    val viewModel by viewModel<OrdersHistoryViewModel>()

    companion object{
        fun newInstance() = OrdersHistoryFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrdersHistoryBinding.bind(view)
        Analytics.with(requireContext()).screen("Order history")
        initUi()
    }

    private fun initUi() {
        with(binding){
            ordersHistoryFragRecyclerView.layoutManager = LinearLayoutManager(context)

            val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.chooser_divider)?.let { decoration.setDrawable(it) }
            ordersHistoryFragRecyclerView.addItemDecoration(decoration)

            viewModel.getOrdersEvent.observe(this@OrdersHistoryFragment, Observer { event ->
                if(event != null){
                    if(event.isSuccess){
                        initList(event.orderHistory!!)
                    }
                }
            })

            viewModel.getOrderHistory()
        }
    }

    private fun initList(orderHistory: List<Order>) {
        with(binding){
            if(orderHistory.isNotEmpty()){
                val adapter = OrdersHistoryAdapter(requireContext(), orderHistory, this@OrdersHistoryFragment)
                ordersHistoryFragRecyclerView.adapter = adapter
            }else{
                ordersHistoryFragEmpty.visibility = View.VISIBLE
                ordersHistoryFragRecyclerView.visibility = View.GONE
            }
        }
    }


    override fun onRateClick(orderId: Long) {
        (activity as MainActivity).loadRateOrder(orderId)
    }

    override fun onReportClick(orderId: Long) {
        (activity as MainActivity).loadReport(orderId)
    }

    override fun onOrderClick(orderId: Long) {
        (activity as MainActivity).loadOrderDetails(orderId)
    }

    fun onRatingDone() {
        initUi()
    }

}
