package com.bupp.wood_spoon_eaters.dialogs.rate_last_order

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.custom_views.adapters.RateLastOrderAdapter
import com.bupp.wood_spoon_eaters.custom_views.metrics_view.MetricsViewAdapter
import com.bupp.wood_spoon_eaters.databinding.RateLastOrderDialogBinding
import com.bupp.wood_spoon_eaters.dialogs.title_body_dialog.TitleBodyDialog
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ReviewRequest
import org.koin.androidx.viewmodel.ext.android.viewModel


class RateLastOrderDialog(val orderId: Long) : DialogFragment(),
    RateLastOrderAdapter.RateOrderAdapterListener,
    MetricsViewAdapter.MetricsViewAdapterListener, CompoundButton.OnCheckedChangeListener, TitleBodyDialog.TitleBodyDialogListener {

//    interface RateDialogListener {
//        fun onRatingDone()
//    }

    private var accuracyRating: Int? = null
    private var deliveryRating: Int? = null

    private lateinit var adapter: RateLastOrderAdapter
    val viewModel by viewModel<RateLastOrderViewModel>()

    lateinit var binding: RateLastOrderDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.rate_last_order_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = RateLastOrderDialogBinding.bind(view)
        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding) {
            rateLastOrderCloseBtn.setOnClickListener { dismiss() }
            rateLastOrderDoneBtn.setBtnEnabled(false)

            rateLastOrderDoneBtn.setOnClickListener {
                onDoneClick()
            }

            rateLastOrderAccuracyNegative.setOnCheckedChangeListener(this@RateLastOrderDialog)
            rateLastOrderAccuracyPositive.setOnCheckedChangeListener(this@RateLastOrderDialog)
            rateLastOrderDeliveryNegative.setOnCheckedChangeListener(this@RateLastOrderDialog)
            rateLastOrderDeliveryPositive.setOnCheckedChangeListener(this@RateLastOrderDialog)

            viewModel.getLastOrder(orderId)
        }
    }

    private fun initObservers() {
        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding.rateLastOrderPb.show()
            } else {
                binding.rateLastOrderPb.hide()
            }
        })
        viewModel.getLastOrder.observe(viewLifecycleOwner, { order ->
            handleOrderDetails(order)
        })

        viewModel.postRating.observe(viewLifecycleOwner, {
            TitleBodyDialog.newInstance(getString(R.string.thank_you), getString(R.string.rate_order_done_body)).show(childFragmentManager, Constants.TITLE_BODY_DIALOG)
        })
    }

    private fun handleOrderDetails(order: Order) {
        with(binding) {
            rateLastOrderDishesRecyclerView.layoutManager = LinearLayoutManager(context)
            adapter = RateLastOrderAdapter(requireContext(), order.orderItems, this@RateLastOrderDialog)
            rateLastOrderDishesRecyclerView.adapter = adapter


            rateLastOrderUserImage.setImage(order.cook!!.thumbnail)
            rateLastOrderUserName.text = "Made by ${order.cook!!.getFullName()}"

        }

    }


    override fun onRate() {
        if (allFieldsRated()) {
            binding.rateLastOrderDoneBtn.setBtnEnabled(true)
        }
    }

    private fun allFieldsRated(): Boolean {
        return accuracyRating != null && deliveryRating != null && adapter.isAllRated()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
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
        binding.rateLastOrderPb.show()
        val reviewRequest = ReviewRequest()
        var metricsArr = adapter.getRatedDishes()

        reviewRequest.accuracyRating = accuracyRating
        reviewRequest.deliveryRating = deliveryRating

        reviewRequest.dishMetrics = metricsArr
        reviewRequest.body = binding.rateLastOrderNotes.getText()

        viewModel.postRating(orderId, reviewRequest)

    }

    override fun onTitleBodyDialogDismiss() {
        dismiss()
    }

}