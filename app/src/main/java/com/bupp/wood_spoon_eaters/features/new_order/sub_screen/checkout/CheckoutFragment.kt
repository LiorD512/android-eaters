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
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
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
                finalizeOrder(curOrder)
            }else{
                checkoutFragPb.hide()
                Toast.makeText(context, "checkoutEvent failed", Toast.LENGTH_SHORT).show()
            }
         })

        viewModel.finalizeOrderEvent.observe(this, Observer { finalizeEvent ->
            if(finalizeEvent != null && finalizeEvent.isSuccess){
                onOrderFinalized()
            }else{
                checkoutFragPb.hide()
                Toast.makeText(context, "finalizeEvent failed", Toast.LENGTH_SHORT).show()
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
            (activity as MainActivity).loadPromoCode()
        }

        checkoutFragChangePaymentBtn.setOnClickListener {
            Toast.makeText(context, "on change payment clicked", Toast.LENGTH_SHORT).show()
        }
//        viewModel.getDeliveryDetails()
//        viewModel.getDeliveryDetailsEvent.observe(this, Observer { deliveryDetails -> handleDeliveryDetails(deliveryDetails.address, deliveryDetails.time) })
        viewModel.getOrderDetails()
        viewModel.getOrderDetailsEvent.observe(this, Observer { orderDetails -> handleOrderDetails(orderDetails.order) })

    }

//    private fun handleDeliveryDetails(address: Address?, time: String?) {
//        if (address != null) {
//            checkoutFragDeliveryAddress.updateDeliveryDetails(address.streetLine1)
//        }
//        if(time != null){
//            checkoutFragDeliveryTime.updateDeliveryDetails(time)
//        }
//    }

    private fun handleOrderDetails(order: Order?) {
        if (order != null) {
            this.curOrder = order
            var cook = order.cook

            val address = order.deliveryAddress.streetLine1
            val time = Utils.parseDateToDayDateHour(order.estDeliveryTime)
            if (address != null) {
                checkoutFragDeliveryAddress.updateDeliveryDetails(address)
            }
            if(time != null){
                checkoutFragDeliveryTime.updateDeliveryDetails(time)
            }

            checkoutFragTitle.text = "Your Order From Cook ${cook.getFullName()}"
            checkoutFragOrderItemsView.setOrderItems(context!!, order.orderItems as ArrayList<OrderItem>, this)
            calcPrice()
        }
    }

    override fun onDishCountChange() {
        calcPrice()
    }

    private fun calcPrice() {
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
        checkoutFragTotalPriceText.text = "$$total"

        checkoutFragStatusBar.updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT, price = total)

        if(allDishSubTotal == 0.0){
            checkoutFragStatusBar.isEnabled = false
            Log.d("wowCheckoutFrag","no dish no more !")
            showEmptyCartDialog()
        }else{
            checkoutFragStatusBar.isEnabled = true
        }
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

    private fun finalizeOrder(curOrder: Order) {
        viewModel.finalizeOrder(curOrder.id)
    }

    private fun onOrderFinalized() {
        checkoutFragPb.hide()
        listener?.onCheckoutDone()
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
        listener?.onCheckoutCanceled()
    }

    fun onAddressChooserSelected() {
        Log.d("wow","onAddressChooserSelected")
        viewModel.getDeliveryDetails()
    }


}
