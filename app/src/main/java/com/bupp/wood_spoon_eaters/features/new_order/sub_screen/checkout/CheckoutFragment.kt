package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.bupp.wood_spoon_eaters.dialogs.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.dialogs.TipCourierDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderSharedViewModel
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import com.stripe.android.model.PaymentMethod
import kotlinx.android.synthetic.main.checkout_fragment.*
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList



class CheckoutFragment(val listener: CheckoutDialogListener) : Fragment(),
    TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, DeliveryDetailsView.DeliveryDetailsViewListener,
    HeaderView.HeaderViewListener, OrderItemsViewAdapter.OrderItemsViewAdapterListener,
    StatusBottomBar.StatusBottomBarListener, ClearCartDialog.ClearCartDialogListener, OrderDateChooserDialog.OrderDateChooserDialogListener,
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    private var hasPaymentMethod: Boolean = false
    lateinit var curOrder: Order
//    var tipPercent: Int = 0
//    var tipInDollars: Int = 0

    interface CheckoutDialogListener{
        fun onCheckoutDone()
        fun onCheckoutCanceled()
    }

    val viewModel by viewModel<CheckoutViewModel>()
    val ordersViewModel by sharedViewModel<NewOrderSharedViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.checkout_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {
        ordersViewModel.checkoutOrderEvent.observe(this, Observer { checkoutEvent ->
            if(checkoutEvent != null && checkoutEvent.isSuccess){
                listener.onCheckoutDone()
            }else{
                Toast.makeText(context, "checkoutEvent failed", Toast.LENGTH_SHORT).show()
            }
         })


        viewModel.getStripeCustomerCards.observe(this, Observer { cardsEvent ->
            if(cardsEvent.isSuccess){
                handleCustomerCards(cardsEvent.paymentMethods)
            }else {
                setEmptyPaymentMethod()
            }
        })

//        ordersViewModel.getOrderDetailsEvent.observe(this, Observer { orderDetails -> handleOrderDetails(orderDetails.order) })
        ordersViewModel.orderData.observe(this, Observer { orderData ->
            handleOrderDetails(orderData)
            checkoutFragTipPercntView.updateCurrentTip(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
            updatePriceUi(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
        })

        ordersViewModel.tipInDollars.observe(this, Observer { orderData ->
//            checkoutFragTipPercntView.updateCurrentTip(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
            updatePriceUi(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
        })

        ordersViewModel.tipPercentage.observe(this, Observer { orderData ->
//            checkoutFragTipPercntView.updateCurrentTip(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
            updatePriceUi(ordersViewModel.tipPercentage.value, ordersViewModel.tipInDollars.value)
        })

        ordersViewModel.progressData.observe(this, Observer { shouldShow ->
            if(shouldShow){
                checkoutFragPb.show()
            }else{
                checkoutFragPb.hide()
            }
        })
        ordersViewModel.editDeliveryTime.observe(this, Observer { editTimeEvent ->
            editTimeEvent.endsAt?.let{
                editTimeEvent.startAt?.let{
                    onEditDateClick(editTimeEvent.startAt, editTimeEvent.endsAt)
                }
            }
        })


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

        checkoutFragUtensilsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            ordersViewModel.updateAddUtensils(isChecked)
            when(isChecked){
                true -> {
                    checkoutFragUtensilsText.text = "These items will be added to your order"
                }
                false -> {
                    checkoutFragUtensilsText.text = "These items won’t be included unless you ask"
                }
            }
        }

        checkoutFragRecurringSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            ordersViewModel.updateRecurringOrder(isChecked)
        }

        ordersViewModel.getLastOrderDetails()
        viewModel.getStripeCustomerCards()
        viewModel.getCurrentCustomer()
        ordersViewModel.resetTip()
    }

    private fun setEmptyPaymentMethod() {
        hasPaymentMethod = false
        checkoutFragChangePaymentTitle.text = "Insert payment method"
        checkoutFragChangePaymentChangeBtn.alpha = 1f
//        checkoutFragStatusBar.setEnabled(false)

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
            viewModel.updateUserCustomerCard(paymentMethod)
        }
    }

    private fun handleOrderDetails(order: Order?) {
        checkoutFragPb.hide()
        if (order != null) {
            this.curOrder = order
            var cook = order.cook

//            var address: String? = null
            if(order.deliveryAddress != null ){
                checkoutFragDeliveryAddress.updateDeliveryFullDetails(order.deliveryAddress)
//                address = order.deliveryAddress.streetLine1
            }
//            if (address != null) {
//            }
            val time = Utils.parseDateToDayDateHour(order.estDeliveryTime)
            if(time != null){
                checkoutFragDeliveryTime.updateDeliveryDetails(time)
            }

            checkoutFragTitle.text = "Your Order From Cook ${cook.getFullName()}"
            checkoutFragOrderItemsView.setOrderItems(context!!, order.orderItems as ArrayList<OrderItem>, this)

        }
    }

    override fun onDishCountChange(orderItemsCount: Int, updatedOrderItem: OrderItem) {
        if(orderItemsCount <= 0){
//            checkoutFragStatusBar.isEnabled = false
            showEmptyCartDialog()
        }else{
            checkoutFragPb.show()
            checkoutFragStatusBar.isEnabled = true
            ordersViewModel.updateOrder(updatedOrderItem)
        }

    }

    private fun updatePriceUi(tipPercent: Int?, tipInDollars: Int?) {
        val tax: Double = curOrder.tax.value
        val serviceFee = curOrder.serviceFee.value
        val deliveryFee = curOrder.deliveryFee.value
        val discount = curOrder.discount.value

//        val tipPercent = ordersViewModel.getTempTipPercent()
//        val tipInDollars = ordersViewModel.getTempTipInDollars()
//        Log.d("wowCheckout","tipinDollars $tipInDollars")

        val promo = curOrder.promoCode

        if(promo != null && promo.isNotEmpty()){
            checkoutFragPromoCodeLayout.visibility = View.VISIBLE
            checkoutFragPromoCodeText.text = "(${curOrder.discount.formatedValue.replace("-","")})"
            checkoutFragPromoCodeStr.visibility = View.VISIBLE
            checkoutFragPromoCodeStr.text = "Promo Code - ${curOrder.promoCode}"
            checkoutFragAddPromoCodeBtn.visibility = View.GONE
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

        val total: Double = (allDishSubTotal.plus(tax).plus(serviceFee).plus(deliveryFee).plus(discount))
        val totalWithTip: Double = total.plus(tipValue)


        checkoutFragSubtotalPriceText.text = "$$allDishSubTotalStr"
        checkoutFragTotalPriceText.text = "$${DecimalFormat("##.##").format(total)}"

        checkoutFragStatusBar.updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_FINALIZE, price = totalWithTip)

    }

    private fun showEmptyCartDialog() {
        ClearCartDialog(this).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
    }

    override fun onClearCart() {
        ordersViewModel.clearCart()
        (activity as NewOrderActivity).finish()
    }

    override fun onStatusBarClicked(type: Int?) {
        if(hasPaymentMethod){
            checkoutFragPb.show()
            ordersViewModel.checkoutOrder(curOrder.id)
        }else{
            (activity as NewOrderActivity).startPaymentMethodActivity()
        }
    }

    override fun onTipIconClick(tipSelection: Int) {
        if (tipSelection == Constants.TIP_CUSTOM_SELECTED) {
            TipCourierDialog(this).show(childFragmentManager, Constants.TIP_COURIER_DIALOG_TAG)
        } else {
            ordersViewModel.updateTipInDollars(0)
            ordersViewModel.updateTipPercentage(tipSelection)
//            ordersViewModel.getOrderDetails()
//            updatePriceUi()
            Toast.makeText(context, "Tip selected is $tipSelection", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onTipDone(tipAmount: Int) {
        checkoutFragTipPercntView.setCustomTipValue(tipAmount)
        ordersViewModel.updateTipPercentage(0)
        ordersViewModel.updateTipInDollars(tipAmount)
//        ordersViewModel.getOrderDetails()
//        updatePriceUi()
    }

    override fun onChangeLocationClick() {
        (activity as NewOrderActivity).loadAddressesDialog()
    }

    override fun onChangeTimeClick() {
//        openDatePicker()
        openOrderTimeDialog()
    }

    private fun openOrderTimeDialog() {
        ordersViewModel.editDeliveryTime()
//        viewModel.getCurrentDish()?.let {
//            val currentDateSelected = it.menuItem
//            val availableMenuItems = it.availableMenuItems
//            OrderDateChooserDialog(currentDateSelected, availableMenuItems, this).show(childFragmentManager, Constants.ORDER_DATE_CHOOSER_DIALOG_TAG)
//        }
    }

    override fun onDateChoose(selectedMenuItem: MenuItem, newChosenDate: Date) {
        if (selectedMenuItem != null) {
            //update order manager
//            currentDish.menuItem = selectedMenuItem // update menuItem to update ui in the next visit in openOrderTimeDialog()
//            viewModel.updateChosenDeliveryDate(selectedMenuItem, newChosenDate)
            ordersViewModel.updateDeliveryTime(newChosenDate)
//            singleDishInfoDate.text = "${Utils.parseDateToDayDateHour(newChosenDate)}"
//            singleDishInfoDate.text = "${currentDish.menuItem?.eta}"
        }
    }


//    fun openDatePicker(){
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//        val day = c.get(Calendar.DAY_OF_MONTH)
//
//        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//            openTimePicker(year, monthOfYear, dayOfMonth)
//        }, year, month, day)
//
//        dpd.datePicker.minDate = Date().time
//
//        dpd.show()
//    }
//
//    fun openTimePicker(year: Int, monthOfYear: Int, dayOfMonth: Int) {
//        val cal = Calendar.getInstance()
//        cal.set(year, monthOfYear, dayOfMonth)
//        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
//            cal.set(Calendar.HOUR_OF_DAY, hour)
//            cal.set(Calendar.MINUTE, minute)
//
//            checkoutFragDeliveryTime.updateDeliveryDetails(Utils.parseDateToDayDateHour(cal.time))
//
//            ordersViewModel.updateDeliveryTime(cal.time)
//
//        }
//        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
//    }



    override fun onHeaderBackClick() {
        listener.onCheckoutCanceled()
    }

    fun onAddressChooserSelected() {
        Log.d("wow","onAddressChooserSelected")
        ordersViewModel.updateAddress()
//        ordersViewModel.getLastOrderDetails()
//        viewModel.getDeliveryDetails()
    }


    fun onEditDateClick(startsAt: Date, endsAt: Date) {
//        this.newSelectedMenuItem = selected

//        val startDate: Date = selected.cookingSlot.startsAt
//        val endDate: Date = selected.cookingSlot.endsAt

        val calStart = Calendar.getInstance()
        calStart.time = startsAt

        val calEnd = Calendar.getInstance()
        calEnd.time = endsAt

        if(Utils.isSameDay(calStart, calEnd)){
            openTimePicker(calStart, calEnd)
//            dismiss()
        }else{
            openDatePicker(calStart, calEnd)
        }

    }

    fun openDatePicker(calStart: Calendar, calEnd: Calendar){
        val c = Calendar.getInstance()
        val year = calStart.get(Calendar.YEAR)
        val month = calStart.get(Calendar.MONTH)
        val day = calStart.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            if(Utils.isSameDay(selectedDate, calStart)){
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
        dpd.setMinTime(calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), 0)
        dpd.setMaxTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), 0)
    }

    override fun onTimeSet(view: com.wdullaer.materialdatetimepicker.time.TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val newCal = Calendar.getInstance()
        newCal.time = ordersViewModel.orderData.value?.cookingSlot?.startsAt
        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newCal.set(Calendar.MINUTE, minute)
        ordersViewModel.updateDeliveryTime(newCal.time)

//        listener.onDateChoose(newSelectedMenuItem!!, newChosenDate)
    }




}
