package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.custom_views.adapters.RateLastOrderAdapter
import com.bupp.wood_spoon_eaters.custom_views.metrics_view.MetricsViewAdapter
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ReviewRequest
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.rate_last_order_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class RateLastOrderDialog(val orderId: Long, val listener: RateDialogListener) : DialogFragment(),
    InputTitleView.InputTitleViewListener, RateLastOrderAdapter.RateOrderAdapterListener,
    MetricsViewAdapter.MetricsViewAdapterListener, CompoundButton.OnCheckedChangeListener {

    interface RateDialogListener{
        fun onRatingDone()
    }

    private var accuracyRating: Int? = null
    private var deliveryRating: Int? = null

    private var isRated: Boolean = false
    private lateinit var adapter: RateLastOrderAdapter
    val viewModel by viewModel<RateLastOrderViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.rate_last_order_dialog, null)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        rateLastOrderNotes.setInputTitleViewListener(this)
        rateLastOrderCloseBtn.setOnClickListener { dismiss() }
        rateLastOrderDoneBtn.setBtnEnabled(false)

        rateLastOrderDoneBtn.setOnClickListener {
            onDoneClick()
        }

        rateLastOrderAccuracyNegative.setOnCheckedChangeListener(this)
        rateLastOrderAccuracyPositive.setOnCheckedChangeListener(this)
        rateLastOrderDeliveryNegative.setOnCheckedChangeListener(this)
        rateLastOrderDeliveryPositive.setOnCheckedChangeListener(this)

        viewModel.getLastOrder.observe(this, Observer { orderEvent ->
            rateLastOrderPb.hide()
            if (orderEvent.isSuccess) {
                handleOrderDetails(orderEvent.order!!)
            }
        })

        viewModel.postRating.observe(this, Observer { postRatingEvent ->
            rateLastOrderPb.hide()
            if (postRatingEvent.isSuccess) {
                listener.onRatingDone()
                dismiss()
            }
        })

        rateLastOrderPb.show()
        viewModel.getLastOrder(orderId)
    }

    private fun handleOrderDetails(order: Order) {
        rateLastOrderDishesRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RateLastOrderAdapter(context!!, order.orderItems, this)
        rateLastOrderDishesRecyclerView.adapter = adapter


        rateLastOrderUserImage.setImage(order.cook!!.thumbnail!!)
        rateLastOrderUserName.text = "Made by ${order.cook!!.getFullName()}"

        rateLastOrderNotes.setInputTitleViewListener(this)

    }

    override fun onInputTitleChange(str: String?){
//        if(str != null){
//            onRate()
//        }
    }

    override fun onRate() {
        if(allFieldsRated()){
            rateLastOrderDoneBtn.setBtnEnabled(true)
        }
    }

    private fun allFieldsRated(): Boolean {
        return accuracyRating != null && deliveryRating != null && adapter.isAllRated()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id){
            R.id.rateLastOrderAccuracyNegative ->
                accuracyRating = 0
            R.id.rateLastOrderAccuracyPositive ->
                accuracyRating = 1
            R.id.rateLastOrderDeliveryNegative ->
                deliveryRating = 0
            R.id.rateLastOrderDeliveryPositive ->
                deliveryRating = 1
        }
        onRate()
    }

    private fun onDoneClick() {
        rateLastOrderPb.show()
        val reviewRequest = ReviewRequest()
        var metricsArr = adapter.getRatedDishes()

        reviewRequest.accuracyRating = accuracyRating
        reviewRequest.deliveryRating = deliveryRating

        reviewRequest.dishMetrics = metricsArr
        reviewRequest.body = rateLastOrderNotes.getText()

        viewModel.postRating(orderId, reviewRequest)

    }
}