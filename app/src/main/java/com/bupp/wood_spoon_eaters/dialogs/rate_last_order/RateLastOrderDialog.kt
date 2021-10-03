package com.bupp.wood_spoon_eaters.dialogs.rate_last_order

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewRequest
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.adapters.RateLastOrderAdapter
import com.bupp.wood_spoon_eaters.databinding.RateLastOrderDialogBinding
import com.bupp.wood_spoon_eaters.dialogs.title_body_dialog.TitleBodyDialog
import com.bupp.wood_spoon_eaters.model.Order
import org.koin.androidx.viewmodel.ext.android.viewModel


class RateLastOrderDialog(val orderId: Long, val listener: RateDialogListener? = null) : DialogFragment(),
    RateLastOrderAdapter.RateOrderAdapterListener,
    CompoundButton.OnCheckedChangeListener, TitleBodyDialog.TitleBodyDialogListener {


    interface RateDialogListener {
        fun onRatingDone(isSuccess: Boolean)
    }

    private var accuracyRating: Int? = null
    private var deliveryRating: Int? = null

    private var adapter: RateLastOrderAdapter? = null
    val viewModel by viewModel<RateLastOrderViewModel>()

    val binding: RateLastOrderDialogBinding by viewBinding()

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


            rateLastOrderUserImage.setImage(order.restaurant!!.thumbnail?.url)
            rateLastOrderUserName.text = "Made by ${order.restaurant.getFullName()}"

        }
    }

    override fun onRate() {
        if (allFieldsRated()) {
            binding.rateLastOrderDoneBtn.setBtnEnabled(true)
        }
    }

    private fun allFieldsRated(): Boolean {
        return accuracyRating != null && deliveryRating != null && adapter?.isAllRated()?:false
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
        var metricsArr = adapter?.getRatedDishes()

        reviewRequest.accuracyRating = accuracyRating
        reviewRequest.deliveryRating = deliveryRating

        reviewRequest.dishMetrics = metricsArr
        reviewRequest.body = binding.rateLastOrderNotes.getText()

        viewModel.postRating(orderId, reviewRequest)

    }

    override fun onTitleBodyDialogDismiss() {
        listener?.onRatingDone(true)
        dismiss()
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }

}