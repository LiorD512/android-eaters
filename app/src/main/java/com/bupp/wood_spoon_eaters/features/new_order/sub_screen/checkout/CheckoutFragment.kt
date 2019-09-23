package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.StatusBottomBar
import com.bupp.wood_spoon_eaters.custom_views.TipPercentView
import com.bupp.wood_spoon_eaters.custom_views.order_item_view.OrderItemsViewAdapter
import com.bupp.wood_spoon_eaters.dialogs.ClearCartDialog
import com.bupp.wood_spoon_eaters.dialogs.TipCourierDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import com.stripe.android.model.PaymentMethod
import kotlinx.android.synthetic.main.checkout_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import kotlin.collections.ArrayList



class CheckoutFragment(val listener: CheckoutDialogListener) :
    Fragment(), TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, DeliveryDetailsView.DeliveryDetailsViewListener,
    HeaderView.HeaderViewListener, OrderItemsViewAdapter.OrderItemsViewAdapterListener,
    StatusBottomBar.StatusBottomBarListener, ClearCartDialog.ClearCartDialogListener {

    lateinit var curOrder: Order

    interface CheckoutDialogListener{
        fun onCheckoutDone()
        fun onCheckoutCanceled()
    }

    val viewModel by viewModel<CheckoutViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.checkout_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.checkoutOrderEvent.observe(this, Observer { checkoutEvent ->
            if(checkoutEvent != null && checkoutEvent.isSuccess){
                checkoutFragPb.hide()
                listener.onCheckoutDone()
            }else{
                checkoutFragPb.hide()
                Toast.makeText(context, "checkoutEvent failed", Toast.LENGTH_SHORT).show()
            }
         })

//        viewModel.finalizeOrderEvent.observe(this, Observer { finalizeEvent ->
//            if(finalizeEvent != null && finalizeEvent.isSuccess){
//                onOrderFinalized()
//            }else{
//                checkoutFragPb.hide()
//                Toast.makeText(context, "finalizeEvent failed", Toast.LENGTH_SHORT).show()
//            }
//         })

        viewModel.getOrderDetailsEvent.observe(this, Observer { orderDetails -> handleOrderDetails(orderDetails.order) })

        viewModel.getStripeCustomerCards.observe(this, Observer { cardsEvent ->
            if(cardsEvent.isSuccess){
                handleCustomerCards(cardsEvent.paymentMethods)
            }else {
                setEmptyPaymentMethod()
            }
        })
    }


    private fun initUi() {
        checkoutFragStatusBar.setStatusBottomBarListener(this)
        checkoutFragTipPercntView.setTipPercentViewListener(this)
        checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)
        checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)
        checkoutFragHeaderView.setHeaderViewListener(this)

        checkoutFragPromoCodeBtn.setOnClickListener {
            (activity as NewOrderActivity).loadPromoCode()
        }

        checkoutFragChangePaymentChangeBtn.setOnClickListener {
//            PaymentMethodsDialog().show(childFragmentManager, Constants.PAYMENT_METHOD_SUCCESS_DIALOG)
            (activity as NewOrderActivity).startPaymentMethodActivity()
//            Toast.makeText(context, "on change payment clicked", Toast.LENGTH_SHORT).show()
//            checkoutFragPb.show()
        }

        viewModel.getOrderDetails()
        viewModel.getStripeCustomerCards()
        viewModel.getCurrentCustomer()

    }



    private fun setEmptyPaymentMethod() {
        checkoutFragChangePaymentTitle.text = "Insert payment method"
        checkoutFragChangePaymentChangeBtn.alpha = 1f
        checkoutFragStatusBar.setEnabled(false)

    }

    private fun handleCustomerCards(paymentMethods: List<PaymentMethod>?) {
        if(paymentMethods?.size!! > 0){
            val defaultPayment = paymentMethods[0]
            updateCustomerPaymentMethod(defaultPayment)
//            viewModel.attachCardToCustomer(defaultPayment.id!!)
        }else{
            setEmptyPaymentMethod()
        }
    }

    fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        val card = paymentMethod.card
        if(card != null){
            Log.d("wowCheckoutFrag","updateCustomerPaymentMethod: ${paymentMethod.id}")
            checkoutFragStatusBar.setEnabled(true)
            checkoutFragChangePaymentTitle.text = "Selected Card: (${card.brand} ${card.last4})"
            checkoutFragChangePaymentChangeBtn.alpha = 0.3f
            viewModel.updateUserCustomerCard(paymentMethod)
        }
    }

    private fun handleOrderDetails(order: Order?) {
        if (order != null) {
            this.curOrder = order
            var cook = order.cook

            var address: String? = null
            if(order.deliveryAddress != null ){
                address = order.deliveryAddress.streetLine1
            }
            val time = Utils.parseDateToDayDateHour(order.estDeliveryTime)
            if (address != null) {
                checkoutFragDeliveryAddress.updateDeliveryDetails(address)
            }
            if(time != null){
                checkoutFragDeliveryTime.updateDeliveryDetails(time)
            }

            checkoutFragTitle.text = "Your Order From Cook ${cook.getFullName()}"
            checkoutFragOrderItemsView.setOrderItems(context!!, order.orderItems as ArrayList<OrderItem>, this)
            updatePriceUi()
        }
    }

    override fun onDishCountChange() {
        if(checkoutFragOrderItemsView.getOrderItemsQuantity() <= 0){
            checkoutFragStatusBar.isEnabled = false
            showEmptyCartDialog()
        }else{
            checkoutFragStatusBar.isEnabled = true

        }
        updatePriceUi()

    }

    private fun updatePriceUi() {
        val tax: Double = curOrder.tax.value
        val serviceFee = curOrder.serviceFee.value
        val deliveryFee = curOrder.deliveryFee.value

        checkoutFragTaxPriceText.text = "$$tax"
        checkoutFragServiceFeePriceText.text = "$$serviceFee"
        checkoutFragDeliveryFeePriceText.text = "$$deliveryFee"

        val allDishSubTotal = checkoutFragOrderItemsView.getAllDishPriceValue()
        val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)
        val total: Double = (allDishSubTotal.plus(tax).plus(serviceFee).plus(deliveryFee))
        checkoutFragSubtotalPriceText.text = "$$allDishSubTotalStr"
        checkoutFragTotalPriceText.text = "$${DecimalFormat("##.##").format(total)}"

        checkoutFragStatusBar.updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT, price = total)

    }

    private fun showEmptyCartDialog() {
        ClearCartDialog(this).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
    }

    override fun onClearCart() {
        viewModel.clearCart()
        (activity as NewOrderActivity).finish()
    }

    override fun onStatusBarClicked(type: Int?) {
        checkoutFragPb.show()
        viewModel.checkoutOrder(curOrder.id)
    }

//    private fun finalizeOrder(curOrder: Order) {
//        viewModel.finalizeOrder(curOrder.id)
//    }

    private fun onOrderFinalized() {

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
        (activity as NewOrderActivity).loadAddressesDialog()
    }

    override fun onHeaderBackClick() {
        listener?.onCheckoutCanceled()
    }

    fun onAddressChooserSelected() {
        Log.d("wow","onAddressChooserSelected")
        viewModel.getDeliveryDetails()
    }




}
