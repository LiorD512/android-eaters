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
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Order
import kotlinx.android.synthetic.main.fragment_orders_history.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrdersHistoryFragment() : Fragment(), HeaderView.HeaderViewListener,
    OrdersHistoryAdapter.OrdersHistoryAdapterListener {

    val viewModel by viewModel<OrdersHistoryViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders_history, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        ordersHistoryFragRecyclerView.layoutManager = LinearLayoutManager(context)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(context!!, R.drawable.chooser_divider)?.let { decoration.setDrawable(it) }
        ordersHistoryFragRecyclerView.addItemDecoration(decoration)

        viewModel.getOrdersEvent.observe(this, Observer { event ->
            if(event != null){
                if(event.isSuccess){
                    initList(event.orderHistory!!)
                }
            }
        })

        viewModel.getOrderHistory()
    }

    private fun initList(orderHistory: ArrayList<Order>) {
        var adapter = OrdersHistoryAdapter(context!!, orderHistory, this)
        ordersHistoryFragRecyclerView.adapter = adapter
    }


    override fun onRateClick(orderId: Long) {
        (activity as MainActivity).loadRateOrder(orderId)
    }

    override fun onReportClick(orderId: Long) {
        (activity as MainActivity).loadReport(orderId)
    }

    fun onRatingDone() {
        initUi()
    }

}
