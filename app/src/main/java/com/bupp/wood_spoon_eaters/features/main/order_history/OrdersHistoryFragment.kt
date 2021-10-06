package com.bupp.wood_spoon_eaters.features.main.order_history


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details.SingleOrderDetailsBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.track_order.TrackOrderBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.FragmentOrdersHistoryBinding
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrdersHistoryFragment: Fragment(R.layout.fragment_orders_history), HeaderView.HeaderViewListener,
    OrdersHistoryAdapter.OrdersHistoryAdapterListener {

    private var listItemDecorator: OrderHistoryItemDecorator? = null
    var binding: FragmentOrdersHistoryBinding? = null
    val viewModel by viewModel<OrdersHistoryViewModel>()
    val mainViewModel by sharedViewModel<MainViewModel>()
    lateinit var adapter: OrdersHistoryAdapter

    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    companion object{
        fun newInstance() = OrdersHistoryFragment()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData()

        mainViewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_ORDERS)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrdersHistoryBinding.bind(view)
//        Analytics.with(requireContext()).screen("Order history")



        initUi()
        initObservers()
    }

    private fun initObservers() {
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner, {
            binding!!.orderHistoryFragHeightCorrection.isVisible = it
        })
        viewModel.orderLiveData.observe(viewLifecycleOwner, { event ->
            initList(event)
        })
    }

    private fun initUi() {
        Log.d("wowStatus","initUi")
        with(binding!!){
            ordersHistoryFragRecyclerView.layoutManager = layoutManager

            adapter = OrdersHistoryAdapter(requireContext(), this@OrdersHistoryFragment)
            adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            ordersHistoryFragRecyclerView.adapter = adapter

        }
    }

    private fun initList(orderHistory: List<OrderHistoryBaseItem>) {
        with(binding!!){
            if(orderHistory.isNotEmpty()){
                listItemDecorator?.let{
                    ordersHistoryFragRecyclerView.removeItemDecoration(it)
                }
                listItemDecorator = OrderHistoryItemDecorator(requireContext())
                ordersHistoryFragRecyclerView.addItemDecoration(listItemDecorator!!)

                adapter.submitList(orderHistory)
//                ordersHistoryFragRecyclerView.smoothScrollToPosition(0)
                ordersHistoryFragEmpty.visibility = View.GONE
                ordersHistoryFragRecyclerView.visibility = View.VISIBLE
            }else{
                ordersHistoryFragEmpty.visibility = View.VISIBLE
                ordersHistoryFragRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun onOrderClick(orderId: Long) {
        SingleOrderDetailsBottomSheet.newInstance(orderId).show(childFragmentManager, Constants.SINGLE_ORDER_DETAILS_BOTTOM_SHEET)
    }

    override fun onViewActiveOrderClicked(orderId: Long) {
        TrackOrderBottomSheet.newInstance(orderId).show(childFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
    }

    override fun onPause() {
        viewModel.endUpdates()
        super.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}
