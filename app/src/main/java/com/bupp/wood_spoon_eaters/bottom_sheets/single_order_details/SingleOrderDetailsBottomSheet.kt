package com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.nationwide_shipping_bottom_sheet.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.SingleOrderDetailsBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.SupportCenterBottomSheetBinding
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat

class SingleOrderDetailsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: SingleOrderDetailsBottomSheetBinding
    private val viewModel: SingleOrderDetailsViewModel by viewModel()

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
            viewModel.initSingleOrder(it.getLong(SINGLE_ORDER_ARGS))
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

        binding = SingleOrderDetailsBottomSheetBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()
        initObservers()
    }

    private fun initUI() {
        with(binding) {

        }
    }

    private fun initObservers() {
        viewModel.singleOrderLiveData.observe(viewLifecycleOwner, {
            handleOrder(it)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handlePb(it)
        })
    }

    private fun handleOrder(order: Order) {
        with(binding) {
            order.apply {
                cook?.apply {
                    singleOrderDetailsHeader.setTitle("Home chef $firstName")
                }
                deliveryAddress?.apply{
                    singleOrderDetailsLocation.updateDeliveryFullDetails(this)
                }
                singleOrderDetailsStatus.updateDeliveryDetails(status ?: "N/A")
                singleOrderDetailsTotal.updateSubTitle(total?.formatedValue ?: "N/A")
                orderItems?.let{
                    singleOrderDetailsOrderItemsView.setOrderItems(requireContext(), it)
                }



                if (!promoCode.isNullOrEmpty()) {
                    singleOrderDetailsPromoCode.visibility = View.VISIBLE
                    singleOrderDetailsPromoCode.setValue("(${discount?.formatedValue?.replace("-", "")})")
                }

//            singleOrderDetailsTaxPriceText.text = "$$tax"
//            serviceFee?.let {
//                if (serviceFee > 0.0) {
//                    singleOrderDetailsServiceFeePriceText.text = "$$serviceFee"
//                    singleOrderDetailsServiceFeePriceText.visibility = View.VISIBLE
//                    singleOrderDetailsServiceFeePriceFree.visibility = View.GONE
//                } else {
//                    singleOrderDetailsServiceFeePriceText.visibility = View.GONE
//                    singleOrderDetailsServiceFeePriceFree.visibility = View.VISIBLE
//                }
//            }
                deliveryFee?.value?.let {
                    if (it > 0.0) {
                        singleOrderDetailsDeliveryFee.setValue("${deliveryFee.formatedValue}")
//                        singleOrderDetailsDeliveryFeePriceText.visibility = View.VISIBLE
//                        singleOrderDetailsDeliveryFeePriceFree.visibility = View.GONE
                    } else {
//                        singleOrderDetailsDeliveryFeePriceText.visibility = View.GONE
//                        singleOrderDetailsDeliveryFeePriceFree.visibility = View.VISIBLE
                    }
                }

                val allDishSubTotal = subtotal?.value
                val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)

                singleOrderDetailsSubtotal.setValue("$$allDishSubTotalStr")
                singleOrderDetailsTip.setValue(totalBeforeTip?.formatedValue ?: "")
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


}