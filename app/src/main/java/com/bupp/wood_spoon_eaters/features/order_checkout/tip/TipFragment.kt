package com.bupp.wood_spoon_eaters.features.order_checkout.tip

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.custom_tip.CustomTipBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.tool_tip_bottom_sheet.ToolTipBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.TipLineView
import com.bupp.wood_spoon_eaters.databinding.FragmentTipBinding
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderRequest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TipFragment : Fragment(R.layout.fragment_tip), CustomTipBottomSheet.CustomTipListener, WSErrorDialog.WSErrorListener {

    val viewModel by sharedViewModel<CheckoutViewModel>()
    val mainViewModel by sharedViewModel<OrderCheckoutViewModel>()
    private var binding: FragmentTipBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTipBinding.bind(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!) {
            viewModel.orderLiveData.value?.tipPercentage?.let {
                selectDefaultTip(it)
            }
            tipFragExit.setOnClickListener{
                activity?.onBackPressed()
            }
            tipFragToolTip.setOnClickListener {
                onToolTipClick()
            }
            tipFrag12Percent.setOnClickListener {
                onTipClick(tipFrag12Percent, Constants.TIP_12_PERCENT_SELECTED)
            }
            tipFrag15Percent.setOnClickListener {
                onTipClick(tipFrag15Percent, Constants.TIP_15_PERCENT_SELECTED)
            }
            tipFrag18Percent.setOnClickListener {
                onTipClick(tipFrag18Percent, Constants.TIP_18_PERCENT_SELECTED)
            }
            tipFrag20Percent.setOnClickListener {
                onTipClick(tipFrag20Percent, Constants.TIP_20_PERCENT_SELECTED)
            }
            tipFragCustomSelect.setOnClickListener {
                CustomTipBottomSheet().show(childFragmentManager, Constants.CUSTOM_TIP_BOTTOM_SHEET)
            }
            tipFragPlaceOrder.setOnClickListener {
                viewModel.onPlaceOrderClick()
            }
        }
    }

    private fun selectDefaultTip(tipPercentage: Int) {
        with(binding!!) {
            when (tipPercentage) {
                Constants.TIP_12_PERCENT_SELECTED -> {
                    tipFrag12Percent.select()
                }
                Constants.TIP_15_PERCENT_SELECTED -> {
                    tipFrag15Percent.select()
                }
                Constants.TIP_18_PERCENT_SELECTED -> {
                    tipFrag18Percent.select()
                }
                Constants.TIP_20_PERCENT_SELECTED -> {
                    tipFrag20Percent.select()
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.orderLiveData.observe(viewLifecycleOwner, { orderData ->
            handleOrderDetails(orderData)
        })
        viewModel.onCheckoutDone.observe(viewLifecycleOwner, {
            mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.FINISH_ACTIVITY_AFTER_PURCHASE)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handlePb(it ?: false)
        })
        viewModel.wsErrorEvent.observe(viewLifecycleOwner, {
            handleWSError(it.getContentIfNotHandled())
        })
    }

    private fun handlePb(shouldShow: Boolean) {
        if(shouldShow){
            binding!!.tipFragPb.show()
        }else{
            binding!!.tipFragPb.hide()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleOrderDetails(order: Order?) {
        order?.tip?.let {
            binding!!.tipFragTotalTip.text = "Your tip:  ${it.formatedValue}"
        }
    }

    private fun onCustomTipSelected(tipAmount: Double) {
        clearAll()
        binding!!.tipFragCustomSelect.setCustomValue(tipAmount)
        onTipSelected(Constants.TIP_CUSTOM_SELECTED, tipAmount = (tipAmount*100).toInt())
    }

    private fun onTipClick(tipView: TipLineView, tipSelection: Int?) {
        if (tipView.isSelected) {
            tipView.unselect()
            onTipSelected(Constants.TIP_NOT_SELECTED)
        } else {
            clearAll()
            tipView.select()
            onTipSelected(tipSelection)
        }
    }

    private fun handleWSError(errorEvent: String?) {
        errorEvent?.let {
            WSErrorDialog(it, this).show(childFragmentManager, Constants.ERROR_DIALOG)
        }
    }

    private fun clearAll() {
        with(binding!!) {
            tipFrag12Percent.unselect()
            tipFrag15Percent.unselect()
            tipFrag18Percent.unselect()
            tipFrag20Percent.unselect()
            binding!!.tipFragCustomSelect.setCustomValue(0.0)
            tipFragCustomSelect.unselect()
        }
    }

    private fun onTipSelected(tipSelection: Int?, tipAmount: Int? = null) {
        if (tipSelection == Constants.TIP_CUSTOM_SELECTED) {
            viewModel.updateOrderParams(OrderRequest(tipPercentage = null, tip = tipAmount), Constants.EVENT_CLICK_TIP)
        } else {
            if (tipSelection == Constants.TIP_NOT_SELECTED) {
                viewModel.updateOrderParams(
                    OrderRequest(tipPercentage = 0f, tip = 0),
                    Constants.EVENT_CLICK_TIP
                ) //if server fix this issue (accept tip_percentage=null as no tip) you can delete this case
            } else {
                viewModel.updateOrderParams(OrderRequest(tipPercentage = tipSelection?.toFloat()), Constants.EVENT_CLICK_TIP)
            }
        }
    }

    private fun onToolTipClick() {
        val titleText = resources.getString(R.string.tool_tip_courier_title)
        val bodyText = resources.getString(R.string.tool_tip_courier_body)
        ToolTipBottomSheet.newInstance(titleText, bodyText).show(childFragmentManager, Constants.FREE_TEXT_BOTTOM_SHEET)
    }

    override fun onCustomTipDone(tip: Double) {
        onCustomTipSelected(tip)
    }

    override fun onWSErrorDone() {
        viewModel.refreshCheckoutPage()
    }

}

