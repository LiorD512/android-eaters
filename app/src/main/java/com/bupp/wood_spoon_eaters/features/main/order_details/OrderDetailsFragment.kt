package com.bupp.wood_spoon_eaters.features.main.order_details

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
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import kotlinx.android.synthetic.main.order_details_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class OrderDetailsFragment : Fragment() {

    private lateinit var adapter: OrderDetailsAdapter
    val viewModel by viewModel<OrderDetailsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.order_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {

        viewModel.orderItems.observe(this, Observer { orderItems ->
            if (orderItems != null) {
                handleOrderItems(orderItems)
            }
        })

        viewModel.getDumbOrderItems()

    }

    private fun handleOrderItems(orderItems: OrderDetailsViewModel.OrderDetails) {
        orderDetailsFragDishesRecycler.layoutManager = LinearLayoutManager(context)
        adapter = OrderDetailsAdapter(context!!, orderItems.orders)


        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        orderDetailsFragDishesRecycler.addItemDecoration(divider)

        orderDetailsFragDishesRecycler.adapter = adapter
    }
}
