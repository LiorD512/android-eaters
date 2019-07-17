package com.bupp.wood_spoon_eaters.features.main.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.StatusBottomBar
import com.bupp.wood_spoon_eaters.custom_views.TipPercentView
import com.bupp.wood_spoon_eaters.dialogs.TipCourierDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.checkout_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class CheckoutFragmentDialog(val listener: CheckoutDialogListener) : DialogFragment(), TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, DeliveryDetailsView.DeliveryDetailsViewListener,
    HeaderView.HeaderViewListener, StatusBottomBar.StatusBottomBarListener {

    interface CheckoutDialogListener{
        fun onCheckoutDone()
    }

    val viewModel by viewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.checkout_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        checkoutFragTipPercntView.setTipPercentViewListener(this)
        checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)
        checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)
        checkoutFragHeaderView.setHeaderViewListener(this)
        checkoutStatusBar.setStatusBottomBarListener(this)

        checkoutFragPromoCodeBtn.setOnClickListener {
            (activity as MainActivity).loadPromoCode()
        }

        checkoutFragChangePaymentBtn.setOnClickListener {
            Toast.makeText(context, "on change payment clicked", Toast.LENGTH_SHORT).show()
        }

        viewModel.getOrderDetails()
        viewModel.getOrderDetailsEvent.observe(this, Observer { orderDetails -> handleCheckOutDetails(orderDetails.order) })

    }

    private fun handleCheckOutDetails(order: Order?) {
        if (order != null) {
            var cook = order.cook

            checkoutFragTitle.text = "Your Order From Cook ${cook.getFullName()}"

            if(order.deliveryAddress != null){
                checkoutFragDeliveryAddress.updateDeliveryDetails(order.deliveryAddress.streetLine1)
            }
            if(order.deliverAt != null){
                checkoutFragDeliveryTime.updateDeliveryDetails(Utils.parseDateToDayDateHour(order.deliverAt))
            }

            checkoutFragOrderItemsView.setOrderItems(order.orderItems as ArrayList<OrderItem>)

            checkoutFragSubtotalPriceText.text = "${order.subtotal.formatedValue}"
            checkoutFragTaxPriceText.text = "${order.tax.formatedValue}"
            checkoutFragServiceFeePriceText.text = "${order.serviceFee.formatedValue}"
            checkoutFragDeliveryFeePriceText.text = "${order.deliveryFee.formatedValue}"
            checkoutFragTotalPriceText.text = "${order.total.formatedValue}"

            checkoutStatusBar.updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT, price = order.total.formatedValue)
        }
    }

    override fun onStatusBarClicked(type: Int?) {
        listener?.onCheckoutDone()
        dismiss()
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

    override fun onHeaderBackClick() {
        dismiss()
    }


}
