package com.bupp.wood_spoon_eaters.features.main.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.TipPercentView
import com.bupp.wood_spoon_eaters.dialogs.TipCourierDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.OrderItem2
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.checkout_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class CheckoutFragment : Fragment(), TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener,
    DeliveryDetailsView.DeliveryDetailsViewListener {

    val viewModel by viewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.checkout_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        checkoutFragTipPercntView.setTipPercentViewListener(this)
        checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)
        checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)

//        checkoutFragDeliveryAddress.updateDeliveryDetails(viewModel.getLastOrderAddress()!!)
        checkoutFragPromoCodeBtn.setOnClickListener {
            (activity as MainActivity).loadPromoCode()
        }

        checkoutFragChangePaymentBtn.setOnClickListener {
            Toast.makeText(context, "on change payment clicked", Toast.LENGTH_SHORT).show()
        }

        viewModel.getDumbCheckoutDetails()
        viewModel.checkOutDetails.observe(this, Observer { checkoutDetails -> handleCheckOutDetails(checkoutDetails) })

    }

    private fun handleCheckOutDetails(checkoutDetails: CheckoutViewModel.CheckoutDetails?) {
        if (checkoutDetails != null) {
            var order = checkoutDetails.order
            var cook = checkoutDetails.cook

            checkoutFragTitle.text = "Your Order From Cook ${cook.getFullName()}"


            checkoutFragSubtotalPriceText.text = "$" + order.subtotal.toString()
            checkoutFragTaxPriceText.text = "$" + order.tax.toString()
            checkoutFragServiceFeePriceText.text = "$" + order.serviceFee.toString()
            checkoutFragDeliveryFeePriceText.text = "$" + order.deliveryFee.toString()
            checkoutFragTotalPriceText.text = "$" + order.total


            checkoutFragOrderItemsView.setOrderItems(order.orderItems as ArrayList<OrderItem2>)
        }
    }

    override fun onTipIconClick(tipSelection: Int) {
        if (tipSelection == Constants.TIP_CUSTOM_SELECTED) {
            TipCourierDialog(this).show(fragmentManager, Constants.TIP_COURIER_DIALOG_TAG)
        } else {
            Toast.makeText(context, "Tip selected is $tipSelection", Toast.LENGTH_SHORT).show()
            //update order
            //handle tip
        }

    }

    override fun onTipDone(tipAmount: Int) {
        checkoutFragTipPercntView.setCustomTipValue(tipAmount)
    }

    override fun onChangeLocationClick() {
        (activity as MainActivity).loadAddressesDialog()
    }
}
