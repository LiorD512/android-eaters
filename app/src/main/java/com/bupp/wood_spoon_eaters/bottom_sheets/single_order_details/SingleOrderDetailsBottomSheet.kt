package com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.report_issue.ReportIssueBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.SingleOrderDetailsBottomSheetBinding
import com.bupp.wood_spoon_eaters.dialogs.rate_last_order.RateLastOrderDialog
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.views.WSTitleValueView
import com.daasuu.bl.ArrowDirection
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat

class SingleOrderDetailsBottomSheet : BottomSheetDialogFragment(), HeaderView.HeaderViewListener, RateLastOrderDialog.RateDialogListener,
    WSTitleValueView.WSTitleValueListener {

    private val binding: SingleOrderDetailsBottomSheetBinding by viewBinding()
    private val viewModel: SingleOrderDetailsViewModel by viewModel()
    private var curOrderId: Long = -1

//    var listener: SingleOrderDetailsListener? = null
//    interface SingleOrderDetailsListener{
//        fun onOrderAgainClick(orderId: Long){}
//        fun onRateOrderClick(orderId: Long){}
//        fun onReportOrderClick(orderId: Long){}
//    }

    companion object {
        private const val SINGLE_ORDER_ARGS = "single_order_args"
        fun newInstance(orderId: Long): SingleOrderDetailsBottomSheet {
            return SingleOrderDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putLong(SINGLE_ORDER_ARGS, orderId)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_order_details_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
        arguments?.let {
            curOrderId = it.getLong(SINGLE_ORDER_ARGS)
            viewModel.initSingleOrder(curOrderId)
        }
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = true
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()
        initObservers()
    }

    private fun initUI() {
        with(binding) {
            singleOrderDetailsOrderAgain.setOnClickListener {
                Toast.makeText(requireContext(), "Coming soon..", Toast.LENGTH_SHORT).show()
            }
            singleOrderDetailsRate.setOnClickListener {
                RateLastOrderDialog(curOrderId, this@SingleOrderDetailsBottomSheet).show(childFragmentManager, Constants.RATE_LAST_ORDER_DIALOG_TAG)
            }
            singleOrderDetailsReport.setOnClickListener {
                ReportIssueBottomSheet.newInstance(curOrderId).show(childFragmentManager, Constants.REPORT_ISSUE_BOTTOM_SHEET)
            }
            singleOrderDetailsHeader.setHeaderViewListener(this@SingleOrderDetailsBottomSheet)

            binding.singleOrderDetailsFees.setWSTitleValueListener(this@SingleOrderDetailsBottomSheet)
            binding.singleOrderDetailsDeliveryFee.setWSTitleValueListener(this@SingleOrderDetailsBottomSheet)
        }
    }

    private fun initObservers() {
        viewModel.singleOrderLiveData.observe(viewLifecycleOwner, {
            handleOrder(it)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handlePb(it)
        })
        viewModel.feeAndTaxDialogData.observe(viewLifecycleOwner, {
            FeesAndTaxBottomSheet.newInstance(it.fee, it.tax, it.minFee).show(childFragmentManager, Constants.FEES_AND_tAX_BOTTOM_SHEET)
        })
    }

    private fun handleOrder(order: Order) {
        with(binding) {
            order.apply {
                restaurant?.apply {
                    singleOrderDetailsHeader.setTitle("Home chef $firstName")
                }
                deliveryAddress?.apply{
                    singleOrderDetailsLocation.updateDeliveryAddressFullDetails(this)
                }
                singleOrderDetailsStatus.updateSubTitle(status?.uppercase() ?: "N/A")
                singleOrderDetailsTotal.updateSubTitle(total?.formatedValue ?: "N/A")
                orderItems?.let{
                    singleOrderDetailsOrderItemsView.setOrderItems(requireContext(), it)
                }

                singleOrderDetailsTip.setValue(tip?.formatedValue ?: "N/A")

                if (!promoCode.isNullOrEmpty()) {
                    singleOrderDetailsPromoCode.visibility = View.VISIBLE
                    singleOrderDetailsPromoCode.setTitle("Promo code $promoCode")
                    singleOrderDetailsPromoCode.setValue("${discount?.formatedValue}")
                }

                if(status == "cancelled"){
                    singleOrderDetailsRate.isEnabled = false
                    singleOrderDetailsRate.alpha = 0.5f
                }

                var feeAndTax = 0.0
                serviceFee?.value?.let{
                    feeAndTax += it
                }
                tax?.value?.let{
                    feeAndTax += it
                }
                val feeAndTaxStr = DecimalFormat("##.##").format(feeAndTax)
                singleOrderDetailsFees.setValue("$$feeAndTaxStr")

                deliveryFee?.value?.let {
                    if (it > 0.0) {
                        singleOrderDetailsDeliveryFee.setValue("${deliveryFee.formatedValue}")
                    }
                }

                val allDishSubTotal = subtotal?.value
                val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)

                singleOrderDetailsSubtotal.setValue("$$allDishSubTotalStr")
                singleOrderDetailsTotal2.setValue(total?.formatedValue ?: "N/A")

                if(wasRated == true){
                    singleOrderDetailsRate.setBtnEnabled(false)
                    singleOrderDetailsRate.setOnClickListener(null)

                }
            }
        }
    }

    private fun handlePb(showPb: Boolean) {
        with(binding){
            if(showPb){
                singleOrderDetailsPb.show()
            }else{
                singleOrderDetailsPb.hide()
            }
        }
    }

    override fun onHeaderCloseClick() {
        dismiss()
    }

    override fun onRatingDone(isSuccess: Boolean) {
        viewModel.initSingleOrder(curOrderId)
    }

    override fun onToolTipClick(type: Int) {
        var titleText = ""
        var bodyText = ""
        if (type == Constants.FEES_AND_ESTIMATED_TAX) {
            viewModel.onFeesAndTaxInfoClick()
        } else {
            when (type) {
                Constants.TOOL_TIP_SERVICE_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_service_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                }
                Constants.TOOL_TIP_CHECKOUT_SERVICE_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_service_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                }
                Constants.TOOL_TIP_CHECKOUT_DELIVERY_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_delivery_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_delivery_fee_body)
                }
                Constants.TOOL_TIP_COURIER_TIP -> {
                    titleText = resources.getString(R.string.tool_tip_courier_title)
                    bodyText = resources.getString(R.string.tool_tip_courier_body)
                }
            }
            ToolTipBottomSheet.newInstance(titleText, bodyText).show(childFragmentManager, Constants.FREE_TEXT_BOTTOM_SHEET)
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is SingleOrderDetailsListener) {
//            listener = context
//        }
//        else if (parentFragment is SingleOrderDetailsListener){
//            this.listener = parentFragment as SingleOrderDetailsListener
//        }
//        else {
//            throw RuntimeException("$context must implement SingleOrderDetailsListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }
//
//    override fun onRatingDone() {
//        TitleBodyDialog.newInstance(getString(R.string.thank_you), getString(R.string.rate_order_done_body)).show(childFragmentManager, Constants.TITLE_BODY_DIALOG)
//    }


}