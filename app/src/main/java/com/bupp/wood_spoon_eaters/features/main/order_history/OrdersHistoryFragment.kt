package com.bupp.wood_spoon_eaters.features.main.order_history


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details.SingleOrderDetailsBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.FragmentOrdersHistoryBinding
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.model.Order
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrdersHistoryFragment: Fragment(R.layout.fragment_orders_history), HeaderView.HeaderViewListener,
    OrdersHistoryAdapter.OrdersHistoryAdapterListener {

    val binding: FragmentOrdersHistoryBinding by viewBinding()
    val viewModel by viewModel<OrdersHistoryViewModel>()
    val mainViewModel by sharedViewModel<MainViewModel>()
    lateinit var adapter: OrdersHistoryAdapter

    companion object{
        fun newInstance() = OrdersHistoryFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Order history")
        initUi()
        initObservers()
    }

    private fun initObservers() {
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner, {
            binding.orderHistoryFragHeightCorrection.isVisible = isVisible
        })
    }

    private fun initUi() {
        with(binding){
            ordersHistoryFragRecyclerView.layoutManager = LinearLayoutManager(context)

            val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.chooser_divider)?.let { decoration.setDrawable(it) }
            ordersHistoryFragRecyclerView.addItemDecoration(decoration)

            viewModel.getOrdersEvent.observe(viewLifecycleOwner, { event ->
                if(event != null){
                    if(event.isSuccess){
                        initList(event.orderHistory!!)
                    }
                }
            })

            adapter = OrdersHistoryAdapter(requireContext(), this@OrdersHistoryFragment)
            ordersHistoryFragRecyclerView.adapter = adapter

            viewModel.getOrderHistory()
        }
    }

    private fun initList(orderHistory: List<Order>) {
        with(binding){
            if(orderHistory.isNotEmpty()){
                adapter.submitList(orderHistory)
            }else{
                ordersHistoryFragEmpty.visibility = View.VISIBLE
                ordersHistoryFragRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun onOrderClick(orderId: Long) {
        SingleOrderDetailsBottomSheet.newInstance(orderId).show(childFragmentManager, Constants.SINGLE_ORDER_DETAILS_BOTTOM_SHEET)
    }


}
