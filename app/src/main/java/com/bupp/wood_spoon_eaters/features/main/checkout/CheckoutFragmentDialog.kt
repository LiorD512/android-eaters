//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.DialogFragment
//import androidx.lifecycle.Observer
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
//import com.bupp.wood_spoon_eaters.custom_views.HeaderView
//import com.bupp.wood_spoon_eaters.custom_views.StatusBottomBar
//import com.bupp.wood_spoon_eaters.custom_views.TipPercentView
//import com.bupp.wood_spoon_eaters.custom_views.order_item_view.OrderItemsViewAdapter
//import com.bupp.wood_spoon_eaters.dialogs.TipCourierDialog
//import com.bupp.wood_spoon_eaters.features.main.MainActivity
//import com.bupp.wood_spoon_eaters.model.Address
//import com.bupp.wood_spoon_eaters.model.Order
//import com.bupp.wood_spoon_eaters.model.OrderItem
//import com.bupp.wood_spoon_eaters.utils.Constants
//import com.bupp.wood_spoon_eaters.utils.Utils
//import kotlinx.android.synthetic.main.checkout_fragment.*
//import org.koin.androidx.viewmodel.ext.android.viewModel
//import java.text.DecimalFormat
//import java.util.*
//import kotlin.collections.ArrayList
//
//class CheckoutFragment(val listener: CheckoutDialogListener) :
//    DialogFragment(), TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, DeliveryDetailsView.DeliveryDetailsViewListener,
//    HeaderView.HeaderViewListener, StatusBottomBar.StatusBottomBarListener,
//    OrderItemsViewAdapter.OrderItemsViewAdapterListener {
//
//    lateinit var curOrder: Order
//
//    interface CheckoutDialogListener{
//        fun onCheckoutDone()
//    }
//
//    val viewModel by viewModel<CheckoutViewModel>()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.checkout_fragment, container, false)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        initUi()
//    }
//
//    private fun initUi() {
//        checkoutFragTipPercntView.setTipPercentViewListener(this)
//        checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)
//        checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)
//        checkoutFragHeaderView.setHeaderViewListener(this)
//        checkoutStatusBar.setStatusBottomBarListener(this)
//
//        checkoutFragPromoCodeBtn.setOnClickListener {
//            (activity as MainActivity).loadPromoCode()
//        }
//
//        checkoutFragChangePaymentBtn.setOnClickListener {
//            Toast.makeText(context, "on change payment clicked", Toast.LENGTH_SHORT).show()
//        }
//        viewModel.getDeliveryDetails()
//        viewModel.getDeliveryDetailsEvent.observe(this, Observer { deliveryDetails -> handleDeliveryDetails(deliveryDetails.address, deliveryDetails.time) })
//        viewModel.getOrderDetails()
//        viewModel.getOrderDetailsEvent.observe(this, Observer { orderDetails -> handleOrderDetails(orderDetails.order) })
//
//    }
//
//    private fun handleDeliveryDetails(address: Address?, time: String?) {
//        if (address != null) {
//            checkoutFragDeliveryAddress.updateDeliveryDetails(address.streetLine1)
//        }
//        if(time != null){
//            checkoutFragDeliveryTime.updateDeliveryDetails(time)
//        }
//    }
//
//    private fun handleOrderDetails(order: Order?) {
//        if (order != null) {
//            this.curOrder = order
//            var cook = order.cook
//
//            checkoutFragTitle.text = "Your Order From Cook ${cook.getFullName()}"
//            checkoutFragOrderItemsView.setOrderItems(context!!, order.orderItems as ArrayList<OrderItem>, this)
//            calcPrice()
//        }
//    }
//
//    override fun onDishCountChange() {
//        calcPrice()
//    }
//
//    private fun calcPrice() {
//        val tax: Double = curOrder.tax.value
//        val serviceFee = curOrder.serviceFee.value
//        val deliveryFee = curOrder.deliveryFee.value
//
//        checkoutFragTaxPriceText.text = "$$tax"
//        checkoutFragServiceFeePriceText.text = "$$serviceFee"
//        checkoutFragDeliveryFeePriceText.text = "$$deliveryFee"
//
//        val allDishSubTotal = checkoutFragOrderItemsView.getAllDishPriceValue()
//        val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)
//        val total: Double = (allDishSubTotal.plus(tax).plus(serviceFee).plus(deliveryFee))
//        checkoutFragSubtotalPriceText.text = "$$allDishSubTotalStr"
//        checkoutFragTotalPriceText.text = "$$total"
//
//        val totalStr = DecimalFormat("##.##").format(total)
//        checkoutStatusBar.updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT, price = "$$totalStr")
//
//        if(allDishSubTotal == 0.0){
//            checkoutStatusBar.isEnabled = false
//        }else{
//            checkoutStatusBar.isEnabled = true
//        }
//    }
//
//    override fun onStatusBarClicked(type: Int?) {
//        listener?.onCheckoutDone()
//        dismiss()
//    }
//
//    override fun onTipIconClick(tipSelection: Int) {
//        if (tipSelection == Constants.TIP_CUSTOM_SELECTED) {
//            TipCourierDialog(this).show(fragmentManager, Constants.TIP_COURIER_DIALOG_TAG)
//        } else {
//            Toast.makeText(context, "Tip selected is $tipSelection", Toast.LENGTH_SHORT).show()
//            //update order
//            //handle tip
//        }
//
//    }
//
//    override fun onTipDone(tipAmount: Int) {
//        checkoutFragTipPercntView.setCustomTipValue(tipAmount)
//    }
//
//    override fun onChangeLocationClick() {
//        (activity as MainActivity).openAddressChooser()
//    }
//
//    override fun onHeaderBackClick() {
//        dismiss()
//    }
//
//    fun onAddressChooserSelected() {
//        Log.d("wow","onAddressChooserSelected")
//        viewModel.getDeliveryDetails()
//    }
//
//
//}
