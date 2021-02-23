package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.TipPercentView
import com.bupp.wood_spoon_eaters.custom_views.order_item_view.OrderItemsViewAdapter
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.CheckoutFragmentBinding
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import com.segment.analytics.Analytics
import com.stripe.android.model.PaymentMethod
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class CheckoutFragment() : Fragment(R.layout.checkout_fragment),
    TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, DeliveryDetailsView.DeliveryDetailsViewListener,
    HeaderView.HeaderViewListener, OrderItemsViewAdapter.OrderItemsViewAdapterListener, OrderDateChooserDialog.OrderDateChooserDialogListener,
    ClearCartDialog.ClearCartDialogListener,
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener, OrderUpdateErrorDialog.updateErrorDialogListener,
    NationwideShippingChooserDialog.NationwideShippingChooserListener {

    private var binding: CheckoutFragmentBinding? = null

    private var hasPaymentMethod: Boolean = false
    lateinit var curOrder: Order

    interface CheckoutDialogListener {
        fun onCheckoutDone()
        fun onCheckoutCanceled()
    }


    val viewModel by viewModel<CheckoutViewModel>()
    val mainViewModel by sharedViewModel<NewOrderMainViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = CheckoutFragmentBinding.bind(view)

        Analytics.with(requireContext()).screen("Checkout page")

        initUi()
        initObservers()

    }

    private fun initObservers() {
        mainViewModel.orderData.observe(viewLifecycleOwner, { orderData ->
            handleOrderDetails(orderData)
            updateBottomBar(orderData.total)
        })
        viewModel.timeChangeEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{
                openOrderTimeBottomSheet(it)
            }
        })
        viewModel.getDeliveryTimeLiveData().observe(viewLifecycleOwner, {
            mainViewModel.onDeliveryTimeChange()
        })
        viewModel.getStripeCustomerCards.observe(viewLifecycleOwner) { cardsEvent ->
            Log.d("wowCheckoutFrag", "getStripeCustomerCards()")
            if (cardsEvent != null) {
                handleCustomerCards(cardsEvent)
            } else {
                setEmptyPaymentMethod()
            }
        }
        mainViewModel.clearCartEvent.observe(viewLifecycleOwner, { emptyCartEvent ->
            if(emptyCartEvent) {
                ClearCartDialog(this@CheckoutFragment).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
            }
        })
//        mainViewModel.checkoutOrderEvent.observe(this, Observer { checkoutEvent ->
//            if(checkoutEvent != null && checkoutEvent.isSuccess){
//                listener.onCheckoutDone()
//            }else{
//                Toast.makeText(context, "checkoutEvent failed", Toast.LENGTH_SHORT).show()
//            }
//         })
//
//
//
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

    private fun updateBottomBar(totalPrice: Price?) {
        mainViewModel.updateCartBottomBarByType(CartBottomBar.BottomBarTypes.PLACE_AN_ORDER, totalPrice?.value, null)
    }

    override fun onCancelUpdateOrderError() {
//        ordersViewModel.rollBackToPreviousAddress()
    }


    private fun initUi() {
        binding!!.checkoutFragTipPercentView.setTipPercentViewListener(this)
        binding!!.checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)
        binding!!.checkoutFragHeaderView.setHeaderViewListener(this)
        binding!!.checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)
        with(binding!!) {

            checkoutFragAddPromoCodeBtn.setOnClickListener {
                mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.PROMO_CODE)
            }

            checkoutFragPromoCodeStr.setOnClickListener {
                mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.PROMO_CODE)
            }

            checkoutFragChangePaymentChangeBtn.setOnClickListener {
                mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.START_PAYMENT_METHOD_ACTIVITY)
            }
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


        mainViewModel.getLastOrderDetails()
//        ordersViewModel.resetTip()

    }

    private fun openOrderTimeBottomSheet(menuItems: List<MenuItem>) {
        val timePickerBottomSheet = TimePickerBottomSheet()
        timePickerBottomSheet.setMenuItems(menuItems)
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    private fun setEmptyPaymentMethod() {
        with(binding!!) {
            hasPaymentMethod = false
            checkoutFragChangePaymentTitle.text = "Insert payment method"
            checkoutFragChangePaymentChangeBtn.alpha = 1f
        }
    }

    private fun handleCustomerCards(paymentMethods: List<PaymentMethod>?) {
        if (paymentMethods?.size!! > 0) {
            val defaultPayment = paymentMethods[0]
            updateCustomerPaymentMethod(defaultPayment)
        } else {
            setEmptyPaymentMethod()
        }
    }

    fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        with(binding!!) {
            val card = paymentMethod.card
            card?.let{
                hasPaymentMethod = true
                Log.d("wowCheckoutFrag", "updateCustomerPaymentMethod: ${paymentMethod.id}")
                checkoutFragChangePaymentTitle.text = "Selected Card: (${card.brand} ${card.last4})"
                checkoutFragChangePaymentChangeBtn.alpha = 0.3f
            }
        }
    }

    private fun handleOrderDetails(order: Order?) {
        order?.let {
            with(binding!!) {

                if (!it.orderItems.isNullOrEmpty()) {
                    var cook = it.cook

                    checkoutFragDeliveryAddress.updateDeliveryFullDetails(it.deliveryAddress)

                    if (it.estDeliveryTime != null) {
                        val time = DateUtils.parseDateToDayDateHour(it.estDeliveryTime)
                        checkoutFragDeliveryTime.updateDeliveryDetails(time)
                    } else if (it.estDeliveryTimeText != null) {
                        checkoutFragDeliveryTime.updateDeliveryDetails(it.estDeliveryTimeText)
                    }

                    checkoutFragTitle.text = "Your Order From Cook ${cook?.getFullName()}"
                    checkoutFragOrderItemsView.setOrderItems(requireContext(), it.orderItems?.toList(), this@CheckoutFragment)
                }

                it.cookingSlot?.isNationwide?.let {
                    if (it) {
                        checkoutFragDeliveryTime.visibility = View.GONE
                        checkoutFragNationwideSelect.visibility = View.VISIBLE
                        checkoutFragNationwideSelect.setDeliveryDetailsViewListener(this@CheckoutFragment)
                    } else {
                        checkoutFragDeliveryTime.visibility = View.VISIBLE
                        checkoutFragNationwideSelect.visibility = View.GONE
                    }
                }
                checkoutFragSmallOrderFee.isNationWide(it.cookingSlot?.isNationwide)
            }
            updatePriceUi(it)

        }
    }

    override fun onDishCountChange(updatedOrderItem: OrderItem, isCartEmpty: Boolean) {
        if(isCartEmpty){
            mainViewModel.showClearCartDialog()
        }else{
            mainViewModel.updateOrderItem(updatedOrderItem)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePriceUi(curOrder: Order) {
        with(binding!!) {

            val tax: Double? = curOrder.tax?.value
            val serviceFee = curOrder.serviceFee?.value
            val deliveryFee = curOrder.deliveryFee?.value
            val discount = curOrder.discount?.value
            curOrder.minPrice?.let {
                val minPrice = it.value
                if (minPrice > 0.0) {
                    checkoutFragMinPriceText.text = "$$minPrice"
                    checkoutFragMinPriceLayout.visibility = View.VISIBLE
                    checkoutFragMinPriceSep.visibility = View.VISIBLE
                } else {
                    checkoutFragMinPriceSep.visibility = View.GONE
                    checkoutFragMinPriceLayout.visibility = View.GONE
                }
            }

            val promo = curOrder.promoCode

            if (!promo.isNullOrEmpty()) {
                checkoutFragPromoCodeLayout.visibility = View.VISIBLE
                checkoutFragPromoCodeText.text = "(${curOrder.discount?.formatedValue?.replace("-", "")})"
                checkoutFragPromoCodeStr.visibility = View.VISIBLE
                checkoutFragPromoCodeStr.text = "Promo Code - ${curOrder.promoCode}"
                checkoutFragAddPromoCodeBtn.visibility = View.GONE
                checkoutFragPromoCodeSep.visibility = View.VISIBLE
            } else {
                checkoutFragPromoCodeStr.visibility = View.GONE
                checkoutFragPromoCodeSep.visibility = View.GONE
                checkoutFragPromoCodeLayout.visibility = View.GONE
                checkoutFragAddPromoCodeBtn.visibility = View.VISIBLE
            }

            checkoutFragTaxPriceText.text = "$$tax"
            serviceFee?.let{
                if (serviceFee > 0.0) {
                    checkoutFragServiceFeePriceText.text = "$$serviceFee"
                    checkoutFragServiceFeePriceText.visibility = View.VISIBLE
                    checkoutFragServiceFeePriceFree.visibility = View.GONE
                } else {
                    checkoutFragServiceFeePriceText.visibility = View.GONE
                    checkoutFragServiceFeePriceFree.visibility = View.VISIBLE
                }
            }
            deliveryFee?.let{
                if (deliveryFee > 0.0) {
                    checkoutFragDeliveryFeePriceText.text = "$$deliveryFee"
                    checkoutFragDeliveryFeePriceText.visibility = View.VISIBLE
                    checkoutFragDeliveryFeePriceFree.visibility = View.GONE
                } else {
                    checkoutFragDeliveryFeePriceText.visibility = View.GONE
                    checkoutFragDeliveryFeePriceFree.visibility = View.VISIBLE
                }
            }

            val allDishSubTotal = curOrder.subtotal?.value//checkoutFragOrderItemsView.getAllDishPriceValue() todo: check if this s ok
            val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)

//            var tipValue = 0.0
//            tipPercent?.let{
//                if(tipPercent != 0){
//                    tipValue = allDishSubTotal.times(tipPercent).div(100)
//                }
//            }
//            tipInDollars?.let{
//                if(tipInDollars > 0.0){
//                    tipValue = tipInDollars.toDouble()
//                }
//            }

//        val total: Double = (allDishSubTotal.plus(tax).plus(serviceFee).plus(deliveryFee).plus(discount))
//        val total: Double = curOrder.discount.value
//            val total: Double = curOrder.totalBeforeTip.value
//            val totalWithTip: Double = total.plus(tipValue)


            checkoutFragSubtotalPriceText.text = "$$allDishSubTotalStr"
            checkoutFragTotalPriceText.text = curOrder.totalBeforeTip?.formatedValue ?: ""

//        checkoutFragStatusBar.updateStatusBottomBar(type = Constants.CART_BOTTOM_BAR_TYPE_FINALIZE, price = totalWithTip)
        }

    }

    override fun onShippingMethodChoose(choosenShippingMethod: ShippingMethod) {
//        checkoutFragStatusBar.isEnabled = true
//        checkoutFragNationwideSelect.updateNationwideShippingDetails("${choosenShippingMethod.name}")
//        ordersViewModel.updateShppingMethod(choosenShippingMethod)
    }

    override fun onClearCart() {
        mainViewModel.clearCart()
        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
    }

    override fun onCancelClearCart() {
        //refresh orderItem counter (set to 0 by user and canceled)
        viewModel.refreshUi()
    }


    override fun onTipIconClick(tipSelection: Int?) {
        if (tipSelection == Constants.TIP_CUSTOM_SELECTED) {
            TipCourierDialog(this).show(childFragmentManager, Constants.TIP_COURIER_DIALOG_TAG)
        } else {
            viewModel.updateOrder(OrderRequest(tipPercentage = tipSelection?.toFloat()))
        }
    }

    override fun onTipDone(tipAmount: Int) {
        binding!!.checkoutFragTipPercentView.setCustomTipValue(tipAmount)
        viewModel.updateOrder(OrderRequest(tipAmount = tipAmount.toString()))
    }

    override fun onChangeLocationClick() {
        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.LOCATION_AND_ADDRESS_ACTIVITY)
//        (activity as NewOrderActivity).loadAddressesDialog()
    }

    override fun onChangeTimeClick() {
        viewModel.onTimeChangeClick()
    }

    override fun onNationwideShippingChange() {
//        ordersViewModel.onNationwideShippingSelectClick()
    }

    private fun openOrderTimeDialog() {
//        ordersViewModel.editDeliveryTime()
    }

    override fun onDateChoose(selectedMenuItem: MenuItem, newChosenDate: Date) {
        if (selectedMenuItem != null) {
            mainViewModel.updateDeliveryTime(newChosenDate)
        }
    }


    override fun onHeaderBackClick() {
//        listener.onCheckoutCanceled()
    }

    fun onAddressChooserSelected() {
        Log.d("wow", "onAddressChooserSelected")
        mainViewModel.updateAddress()
    }


    fun onEditDateClick(startsAt: Date, endsAt: Date) {
        val calStart = Calendar.getInstance()
        calStart.time = startsAt

        val calEnd = Calendar.getInstance()
        calEnd.time = endsAt

        if (DateUtils.isSameDay(calStart, calEnd)) {
            openTimePicker(calStart, calEnd)
        } else {
            openDatePicker(calStart, calEnd)
        }

    }

    fun openDatePicker(calStart: Calendar, calEnd: Calendar) {
        val c = Calendar.getInstance()
        val year = calStart.get(Calendar.YEAR)
        val month = calStart.get(Calendar.MONTH)
        val day = calStart.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            if (DateUtils.isSameDay(selectedDate, calStart)) {
                selectedDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59)
                openTimePicker(calStart, selectedDate)
            } else {
                selectedDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                openTimePicker(selectedDate, calEnd)
            }
        }, year, month, day)

        dpd.datePicker.minDate = calStart.timeInMillis
        dpd.datePicker.maxDate = calEnd.timeInMillis

        dpd.show()
    }

    fun openTimePicker(calStart: Calendar, calEnd: Calendar) {
        val dpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
            this,
            calStart.get(Calendar.HOUR_OF_DAY),
            calStart.get(Calendar.MINUTE),
            false
        )

        dpd.show(childFragmentManager, "Datepickerdialog")
        if (calStart.before(calEnd)) {
            dpd.setMinTime(calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), 0)
            dpd.setMaxTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), 0)
        }
    }

    override fun onTimeSet(view: com.wdullaer.materialdatetimepicker.time.TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val newCal = Calendar.getInstance()
//        newCal.time = ordersViewModel.orderData.value?.cookingSlot?.startsAt
        newCal.time = mainViewModel.orderData.value?.cookingSlot?.orderFrom
        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newCal.set(Calendar.MINUTE, minute)
        mainViewModel.updateDeliveryTime(newCal.time)
    }


}
