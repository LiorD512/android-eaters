package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.app.DatePickerDialog
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
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.model.ShippingMethod
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.segment.analytics.Analytics
import com.stripe.android.model.PaymentMethod
import kotlinx.android.synthetic.main.checkout_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList



class CheckoutFragment(val listener: CheckoutDialogListener) : Fragment(),
    TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, DeliveryDetailsView.DeliveryDetailsViewListener,
    HeaderView.HeaderViewListener, OrderItemsViewAdapter.OrderItemsViewAdapterListener, OrderDateChooserDialog.OrderDateChooserDialogListener,
    StatusBottomBar.StatusBottomBarListener, ClearCartDialog.ClearCartDialogListener,
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener, OrderUpdateErrorDialog.updateErrorDialogListener,
    NationwideShippingChooserDialog.NationwideShippingChooserListener {

    private var hasPaymentMethod: Boolean = false
    lateinit var curOrder: Order

    interface CheckoutDialogListener{
        fun onCheckoutDone()
        fun onCheckoutCanceled()
    }

//    val viewModel by viewModel<CheckoutViewModel>()
    val ordersViewModel by sharedViewModel<NewOrderMainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.checkout_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Checkout page")

        initUi()
        initObservers()
    }

    private fun initObservers() {
//        ordersViewModel.checkoutOrderEvent.observe(this, Observer { checkoutEvent ->
//            if(checkoutEvent != null && checkoutEvent.isSuccess){
//                listener.onCheckoutDone()
//            }else{
//                Toast.makeText(context, "checkoutEvent failed", Toast.LENGTH_SHORT).show()
//            }
//         })
//
//
//        ordersViewModel.getStripeCustomerCards(requireContext()).observe(this, Observer { cardsEvent ->
//            Log.d("wowCheckoutFrag","getStripeCustomerCards()")
//            if(cardsEvent != null){
//                handleCustomerCards(cardsEvent)
//            }else {
//                setEmptyPaymentMethod()
//            }
//        })
//
//        ordersViewModel.orderData.observe(viewLifecycleOwner, Observer { orderData ->
//            handleOrderDetails(orderData)
//            checkoutFragTipPercntView.updateCurrentTip(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
//            updatePriceUi(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
//        })
//
//        ordersViewModel.tipInDollars.observe(viewLifecycleOwner, Observer { orderData ->
//            updatePriceUi(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
//        })
//
//        ordersViewModel.tipPercentage.observe(viewLifecycleOwner, Observer { orderData ->
//            updatePriceUi(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
//        })
//
//        ordersViewModel.progressData.observe(viewLifecycleOwner, Observer { shouldShow ->
//            if(shouldShow){
//                checkoutFragPb.show()
//            }else{
//                checkoutFragPb.hide()
//            }
//        })
//        ordersViewModel.editDeliveryTime.observe(this, Observer { editTimeEvent ->
//            editTimeEvent.endsAt?.let{
//                editTimeEvent.startAt?.let{
//                    onEditDateClick(editTimeEvent.startAt, editTimeEvent.endsAt)
//                }
//            }
//        })
//        ordersViewModel.emptyCartEvent.observe(this, Observer { emptyCartEvent ->
//            if(emptyCartEvent.shouldShow) {
//                ClearCartDialog(this).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
//            }
//        })
//
//        ordersViewModel.updateOrderError.observe(this, Observer { errorEvent ->
//            when(errorEvent){
//                400 -> {
//                    OrderUpdateErrorDialog(this).show(childFragmentManager, Constants.ORDER_UPDATE_ERROR_DIALOG)
//                }
//                else -> {}
//            }
//        })
//        ordersViewModel.errorEvent.observe(this, Observer{
//            it?.let{
//                var errorStr = ""
//                it.forEach {
//                    errorStr += "${it.msg} \n"
//                }
//                ErrorDialog.newInstance(errorStr).show(childFragmentManager, Constants.ERROR_DIALOG)
//            }
//        })
//        ordersViewModel.shippingMethodsEvent.observe(viewLifecycleOwner, Observer{
//            it?.let{
//                if(it?.size > 0){
//                    NationwideShippingChooserDialog.newInstance(it).show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_SELECT_DIALOG)
//                }else{
//                    Toast.makeText(requireContext(), "UPS Service is not available at the moment, please try again later", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
    }

    override fun onCancelUpdateOrderError() {
//        ordersViewModel.rollBackToPreviousAddress()
    }


    private fun initUi() {
        checkoutFragStatusBar.setStatusBottomBarListener(this)
        checkoutFragTipPercntView.setTipPercentViewListener(this)
        checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)
        checkoutFragHeaderView.setHeaderViewListener(this)
        checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)

        checkoutFragAddPromoCodeBtn.setOnClickListener {
            (activity as NewOrderActivity).loadPromoCode()
        }

        checkoutFragPromoCodeStr.setOnClickListener {
            (activity as NewOrderActivity).loadPromoCode()
        }

        checkoutFragChangePaymentChangeBtn.setOnClickListener {
            (activity as NewOrderActivity).startPaymentMethodActivity()
        }

//        checkoutFragUtensilsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
//            ordersViewModel.updateAddUtensils(isChecked)
//            when(isChecked){
//                true -> {
//                    checkoutFragUtensilsText.text = "These items will be added to your order"
//                }
//                false -> {
//                    checkoutFragUtensilsText.text = "These items won’t be included unless you ask"
//                }
//            }
//        }


        ordersViewModel.getLastOrderDetails()
//        ordersViewModel.resetTip()

    }

    private fun setEmptyPaymentMethod() {
        hasPaymentMethod = false
        checkoutFragChangePaymentTitle.text = "Insert payment method"
        checkoutFragChangePaymentChangeBtn.alpha = 1f
    }

    private fun handleCustomerCards(paymentMethods: List<PaymentMethod>?) {
        if(paymentMethods?.size!! > 0){
            val defaultPayment = paymentMethods[0]
            updateCustomerPaymentMethod(defaultPayment)
        }else{
            setEmptyPaymentMethod()
        }
    }

    fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        val card = paymentMethod.card
        if(card != null){
            hasPaymentMethod = true
            Log.d("wowCheckoutFrag","updateCustomerPaymentMethod: ${paymentMethod.id}")
            checkoutFragStatusBar.setEnabled(true)
            checkoutFragChangePaymentTitle.text = "Selected Card: (${card.brand} ${card.last4})"
            checkoutFragChangePaymentChangeBtn.alpha = 0.3f
//            ordersViewModel.updateUserCustomerCard(paymentMethod)
        }
    }

    private fun handleOrderDetails(order: Order?) {
        checkoutFragPb.hide()
        if (order != null) {
            this.curOrder = order

            if(order.orderItems?.size > 0){
                checkoutFragStatusBar.isEnabled = true
                var cook = order.cook

                if(order.deliveryAddress != null ){
                    checkoutFragDeliveryAddress.updateDeliveryFullDetails(order.deliveryAddress)
                }

                if(order.estDeliveryTime != null){
                    val time = DateUtils.parseDateToDayDateHour(order.estDeliveryTime)
                    if(time != null){
                        checkoutFragDeliveryTime.updateDeliveryDetails(time)
                    }
                }else if(order.estDeliveryTimeText != null){
                    checkoutFragDeliveryTime.updateDeliveryDetails(order.estDeliveryTimeText)
                }

                checkoutFragTitle.text = "Your Order From Cook ${cook.getFullName()}"
                checkoutFragOrderItemsView.setOrderItems(requireContext(), order.orderItems as ArrayList<OrderItem>, this)
            }

            order.cookingSlot.isNationwide?.let{
                if(it){
                    checkoutFragDeliveryTime.visibility = View.GONE
                    checkoutFragNationwideSelect.visibility = View.VISIBLE
                    checkoutFragNationwideSelect.setDeliveryDetailsViewListener(this)
//                    if(ordersViewModel.selectedShippingMethod == null){
//                        checkoutFragStatusBar.isEnabled = false
//                    }
                }else{
                    checkoutFragDeliveryTime.visibility = View.VISIBLE
                    checkoutFragNationwideSelect.visibility = View.GONE
                }
            }
            checkoutFragSmallOrderFee.isNationWide(order.cookingSlot.isNationwide)

        }
    }

    override fun onDishCountChange(orderItemsCount: Int, updatedOrderItem: OrderItem) {
//            ordersViewModel.updateOrder(updatedOrderItem)
    }

    private fun updatePriceUi(tipPercent: Int?, tipInDollars: Int?) {
        val tax: Double = curOrder.tax.value
        val serviceFee = curOrder.serviceFee.value
        val deliveryFee = curOrder.deliveryFee.value
        val discount = curOrder.discount.value
        curOrder.minPrice?.let{
            val minPrice = it.value
            if(minPrice > 0.0){
                checkoutFragMinPriceText.text = "$$minPrice"
                checkoutFragMinPriceLayout.visibility = View.VISIBLE
                checkoutFragMinPriceSep.visibility = View.VISIBLE
            }else{
                checkoutFragMinPriceSep.visibility = View.GONE
                checkoutFragMinPriceLayout.visibility = View.GONE
            }
        }

        val promo = curOrder.promoCode

        if(promo != null && promo.isNotEmpty()){
            checkoutFragPromoCodeLayout.visibility = View.VISIBLE
            checkoutFragPromoCodeText.text = "(${curOrder.discount.formatedValue.replace("-","")})"
            checkoutFragPromoCodeStr.visibility = View.VISIBLE
            checkoutFragPromoCodeStr.text = "Promo Code - ${curOrder.promoCode}"
            checkoutFragAddPromoCodeBtn.visibility = View.GONE
            checkoutFragPromoCodeSep.visibility = View.VISIBLE
        }else{
            checkoutFragPromoCodeStr.visibility = View.GONE
            checkoutFragPromoCodeSep.visibility = View.GONE
            checkoutFragPromoCodeLayout.visibility = View.GONE
            checkoutFragAddPromoCodeBtn.visibility = View.VISIBLE
        }

        checkoutFragTaxPriceText.text = "$$tax"
        if(serviceFee > 0.0){
            checkoutFragServiceFeePriceText.text = "$$serviceFee"
            checkoutFragServiceFeePriceText.visibility = View.VISIBLE
            checkoutFragServiceFeePriceFree.visibility = View.GONE
        }else{
            checkoutFragServiceFeePriceText.visibility = View.GONE
            checkoutFragServiceFeePriceFree.visibility = View.VISIBLE
        }
        if(deliveryFee > 0.0){
            checkoutFragDeliveryFeePriceText.text = "$$deliveryFee"
            checkoutFragDeliveryFeePriceText.visibility = View.VISIBLE
            checkoutFragDeliveryFeePriceFree.visibility = View.GONE
        }else{
            checkoutFragDeliveryFeePriceText.visibility = View.GONE
            checkoutFragDeliveryFeePriceFree.visibility = View.VISIBLE
        }

        val allDishSubTotal = checkoutFragOrderItemsView.getAllDishPriceValue()
        val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)

        var tipValue = 0.0
        tipPercent?.let{
            if(tipPercent != 0){
                tipValue = allDishSubTotal.times(tipPercent).div(100)
            }
        }
        tipInDollars?.let{
            if(tipInDollars > 0.0){
                tipValue = tipInDollars.toDouble()
            }
        }

//        val total: Double = (allDishSubTotal.plus(tax).plus(serviceFee).plus(deliveryFee).plus(discount))
//        val total: Double = curOrder.discount.value
        val total: Double = curOrder.totalBeforeTip.value
        val totalWithTip: Double = total.plus(tipValue)


        checkoutFragSubtotalPriceText.text = "$$allDishSubTotalStr"
        checkoutFragTotalPriceText.text = "${curOrder.totalBeforeTip.formatedValue}"

        checkoutFragStatusBar.updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_FINALIZE, price = totalWithTip)

    }

    override fun onShippingMethodChoose(choosenShippingMethod: ShippingMethod) {
//        checkoutFragStatusBar.isEnabled = true
//        checkoutFragNationwideSelect.updateNationwideShippingDetails("${choosenShippingMethod.name}")
//        ordersViewModel.updateShppingMethod(choosenShippingMethod)
    }

    override fun onClearCart() {
//        ordersViewModel.clearCart()
//        (activity as NewOrderActivity).finish()
    }

    override fun onStatusBarClicked(type: Int?) {
//        if(ordersViewModel.isNationwideOrder() && ordersViewModel.selectedShippingMethod == null){
//            ordersViewModel.onNationwideShippingSelectClick()
//            return
//        }
//        if(hasPaymentMethod){
//            checkoutFragPb.show()
//            ordersViewModel.checkoutOrder(curOrder.id)
//        }else{
//            //start ups and then payment
//            (activity as NewOrderActivity).startPaymentMethodActivity()
//        }
    }

    override fun onTipIconClick(tipSelection: Int) {
        if (tipSelection == Constants.TIP_CUSTOM_SELECTED) {
            TipCourierDialog(this).show(childFragmentManager, Constants.TIP_COURIER_DIALOG_TAG)
        } else {
//            ordersViewModel.updateTip(tipPercentage = tipSelection)
            }
    }

    override fun onTipDone(tipAmount: Int) {
        checkoutFragTipPercntView.setCustomTipValue(tipAmount)
//        ordersViewModel.updateTip(tipInCents = tipAmount)
    }

    override fun onChangeLocationClick() {
        (activity as NewOrderActivity).loadAddressesDialog()
    }

    override fun onChangeTimeClick() {
        openOrderTimeDialog()
    }

    override fun onNationwideShippingChange() {
//        ordersViewModel.onNationwideShippingSelectClick()
    }

    private fun openOrderTimeDialog() {
//        ordersViewModel.editDeliveryTime()
    }

    override fun onDateChoose(selectedMenuItem: MenuItem, newChosenDate: Date) {
        if (selectedMenuItem != null) {
            ordersViewModel.updateDeliveryTime(newChosenDate)
        }
    }


    override fun onHeaderBackClick() {
        listener.onCheckoutCanceled()
    }

    fun onAddressChooserSelected() {
        Log.d("wow","onAddressChooserSelected")
        ordersViewModel.updateAddress()
    }


    fun onEditDateClick(startsAt: Date, endsAt: Date) {
        val calStart = Calendar.getInstance()
        calStart.time = startsAt

        val calEnd = Calendar.getInstance()
        calEnd.time = endsAt

        if(DateUtils.isSameDay(calStart, calEnd)){
            openTimePicker(calStart, calEnd)
        }else{
            openDatePicker(calStart, calEnd)
        }

    }

    fun openDatePicker(calStart: Calendar, calEnd: Calendar){
        val c = Calendar.getInstance()
        val year = calStart.get(Calendar.YEAR)
        val month = calStart.get(Calendar.MONTH)
        val day = calStart.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            if(DateUtils.isSameDay(selectedDate, calStart)){
                selectedDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59)
                openTimePicker(calStart, selectedDate)
            }else{
                selectedDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                openTimePicker(selectedDate, calEnd)
            }
        }, year, month, day)

        dpd.datePicker.minDate = calStart.timeInMillis
        dpd.datePicker.maxDate = calEnd.timeInMillis

        dpd.show()
    }

    fun openTimePicker(calStart: Calendar, calEnd: Calendar) {
        val dpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this, calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), false)

        dpd.show(childFragmentManager, "Datepickerdialog")
        if(calStart.before(calEnd)){
            dpd.setMinTime(calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), 0)
            dpd.setMaxTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), 0)
        }
    }

    override fun onTimeSet(view: com.wdullaer.materialdatetimepicker.time.TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val newCal = Calendar.getInstance()
//        newCal.time = ordersViewModel.orderData.value?.cookingSlot?.startsAt
        newCal.time = ordersViewModel.orderData.value?.cookingSlot?.orderFrom
        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newCal.set(Calendar.MINUTE, minute)
        ordersViewModel.updateDeliveryTime(newCal.time)
    }




}
