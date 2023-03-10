package com.bupp.wood_spoon_eaters.features.main.order_history


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details.SingleOrderDetailsBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.FragmentOrdersHistoryBinding
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantActivity
import com.bupp.wood_spoon_eaters.features.track_your_order.TrackYourOrderActivity
import com.bupp.wood_spoon_eaters.model.Order
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrdersHistoryFragment: Fragment(R.layout.fragment_orders_history), HeaderView.HeaderViewListener,
    OrdersHistoryAdapter.OrdersHistoryAdapterListener {

    private val trackOrderForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - trackOrderForResult")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val isAfterCancel = data?.getBooleanExtra("isAfterCancel", false)
            if(isAfterCancel!!){
                viewModel.fetchData()
            }
        }
    }

    private var listItemDecorator: OrderHistoryItemDecorator? = null
    var binding: FragmentOrdersHistoryBinding? = null
    val viewModel by viewModel<OrdersHistoryViewModel>()
    val mainViewModel by sharedViewModel<MainViewModel>()
    lateinit var adapter: OrdersHistoryAdapter

    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    companion object{
        fun newInstance() = OrdersHistoryFragment()
        const val TAG = "wowOrderHistory"
    }

    override fun onResume() {
        viewModel.startSilentUpdate()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrdersHistoryBinding.bind(view)

        initUi()
        initObservers()

        viewModel.fetchData()
    }

    private fun initObservers() {
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner, {
            binding!!.orderHistoryFragHeightCorrection.isVisible = it
        })
        viewModel.orderLiveData.observe(viewLifecycleOwner, { event ->
            initList(event)
        })
        viewModel.restaurantInitParamsLiveData.observe(viewLifecycleOwner){
            startActivity(Intent(requireContext(), RestaurantActivity::class.java).putExtra(Constants.ARG_RESTAURANT, it))
        }
    }

    private fun initUi() {
        Log.d("wowStatus","initUi")
        with(binding!!){
            ordersHistoryFragRecyclerView.layoutManager = layoutManager

            adapter = OrdersHistoryAdapter(requireContext(), this@OrdersHistoryFragment)
            adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            ordersHistoryFragRecyclerView.adapter = adapter

            ordersHistoryFragRefreshLayout.setOnRefreshListener {
                viewModel.fetchData()
            }
        }
    }

    private fun initList(orderHistory: List<OrderHistoryBaseItem>) {
        with(binding!!){
            ordersHistoryFragRefreshLayout.isRefreshing = false
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
        viewModel.logEvent(Constants.EVENT_ORDERS_ORDER_HISTORY_CLICK)
    }

    override fun onViewActiveOrderClicked(order: Order, transitionBundle: ActivityOptionsCompat, mapPreview: String) {
        val orderState = order.status
        val statusTitle = order.extendedStatus?.title
        val statusSubTitle = order.extendedStatus?.subtitle

        val intent = Intent(requireContext(), TrackYourOrderActivity::class.java)
            .putExtra("order_id", order.id)
            .putExtra("thumbnail", order.restaurant?.thumbnail?.url)
            .putExtra("restaurant_name", order.restaurant?.restaurantName)
            .putExtra("status_title", statusTitle)
            .putExtra("status_subtitle", statusSubTitle)
            .putExtra("pb_state", orderState.name)
            .putExtra("map_preview_url", mapPreview)
            .putExtra("order_number", order.orderNumber)
            .putExtra("order_eta", order.etaToDisplay)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        activity?.window?.exitTransition = null
//        startActivity(intent, transitionBundle.toBundle())
        trackOrderForResult.launch(intent, transitionBundle)
        viewModel.logTrackOrderClick(order.id)
//        TrackOrderBottomSheet.newInstance(orderId).show(childFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
    }

    override fun onOrderAgainClick(order: Order) {
        viewModel.onOrderAgainClick(order)
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
