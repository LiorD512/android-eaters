package com.bupp.wood_spoon_eaters.features.order_checkout.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.custom_views.CustomDetailsView
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.TipPercentView
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.bottom_sheets.nationwide_shipping_bottom_sheet.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.promo_code.PromoCodeBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.Constants.Companion.TIP_NOT_SELECTED
import com.bupp.wood_spoon_eaters.custom_views.order_item_view2.OrderItemsView2
import com.bupp.wood_spoon_eaters.databinding.CheckoutFragmentBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutViewModel
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.WSTitleValueView
import com.segment.analytics.Analytics
import com.stripe.android.model.PaymentMethod
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class CheckoutFragment : Fragment(R.layout.checkout_fragment),
    TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, CustomDetailsView.CustomDetailsViewListener,
    OrderDateChooserDialog.OrderDateChooserDialogListener,
    NationwideShippingChooserDialog.NationwideShippingChooserListener, TimePickerBottomSheet.TimePickerListener, OrderItemsView2.OrderItemsListener,
    WSTitleValueView.WSTitleValueListener, HeaderView.HeaderViewListener, WSErrorDialog.WSErrorListener,
    SingleColumnTimePickerBottomSheet.TimePickerListener {

    private val binding: CheckoutFragmentBinding by viewBinding()

    private var hasPaymentMethod: Boolean = false


    val viewModel by viewModel<CheckoutViewModel>()
    val mainViewModel by sharedViewModel<OrderCheckoutViewModel>()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Checkout page")
//        mainViewModel.proceedToCheckoutEvent()

        initUi()
        initObservers()

    }
    
    private fun initObservers() {
        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding.checkoutFragmentPb.show()
            } else {
                binding.checkoutFragmentPb.hide()
            }
        })
        viewModel.onCheckoutDone.observe(viewLifecycleOwner, {
            mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEvent.FINISH_ACTIVITY_AFTER_PURCHASE)
        })
        viewModel.orderLiveData.observe(viewLifecycleOwner, { orderData ->
            handleOrderDetails(orderData)
        })
        viewModel.deliveryDatesLiveData.observe(viewLifecycleOwner, {
            handleOrderDeliveryDates(it)
        })
        viewModel.timeChangeEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                openOrderTimeBottomSheet(it)
            }
        })
        viewModel.getStripeCustomerCards.observe(viewLifecycleOwner, { cardsEvent ->
            Log.d("wowCheckoutFrag", "getStripeCustomerCards()")
            if (cardsEvent != null) {
                handleCustomerCards(cardsEvent)
            } else {
                setEmptyPaymentMethod()
            }
        })
        viewModel.feeAndTaxDialogData.observe(viewLifecycleOwner, {
            FeesAndTaxBottomSheet.newInstance(it.fee, it.tax, it.minOrderFee).show(childFragmentManager, Constants.FEES_AND_tAX_BOTTOM_SHEET)
        })
//        mainViewModel.clearCartEvent.observe(viewLifecycleOwner, { emptyCartEvent ->
//            if (emptyCartEvent) {
//                ClearCartDialog(this@CheckoutFragment).show(childFragmentManager, Constants.CLEAR_CART_RESTAURANT_DIALOG_TAG)
//            }
//        })
        viewModel.shippingMethodsEvent.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    NationwideShippingChooserDialog.newInstance(ArrayList(it)).show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_SELECT_DIALOG)
                } else {
                    Toast.makeText(requireContext(), "UPS Service is not available at the moment, please try again later", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.validationError.observe(viewLifecycleOwner, {
            when (it) {
                CheckoutViewModel.OrderValidationErrorType.SHIPPING_METHOD_MISSING -> {
                    viewModel.onNationwideShippingSelectClick()
                }
                CheckoutViewModel.OrderValidationErrorType.PAYMENT_METHOD_MISSING -> {
                    mainViewModel.startStripeOrReInit()
                }
            }
        })
        viewModel.wsErrorEvent.observe(viewLifecycleOwner, {
            handleWSError(it.getContentIfNotHandled())
        })
        viewModel.deliveryDatesUi.observe(viewLifecycleOwner, {
            binding.checkoutFragDeliveryTime.updateDeliveryTimeUi(it)
        })
    }

    private fun handleOrderDeliveryDates(deliveryDates: List<DeliveryDates>) {
        if(deliveryDates.isEmpty()){
            binding.checkoutFragDeliveryTime.setChangeable(false)
        }
    }

//    private fun updateBottomBar(totalPrice: Price?) {
//        mainViewModel.updateCartBottomBarByType(CartBottomBar.BottomBarTypes.PLACE_AN_ORDER, totalPrice?.value, null)
//    }

//    override fun onCancelUpdateOrderError() {
////        ordersViewModel.rollBackToPreviousAddress()
//    }

    override fun onDishCountChange(updatedOrderItem: OrderItem, isCartEmpty: Boolean) {
        if (isCartEmpty) {
            viewModel.updateOrderParams(OrderRequest(orderItemRequests = listOf(updatedOrderItem.toOrderItemRequest())))
            mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEvent.FINISH_CHECKOUT_ACTIVITY)
        } else {
            viewModel.updateOrderParams(OrderRequest(orderItemRequests = listOf(updatedOrderItem.toOrderItemRequest())))
        }
    }

    private fun handleWSError(errorEvent: String?) {
        errorEvent?.let {
            WSErrorDialog(it, this).show(childFragmentManager, Constants.ERROR_DIALOG)
        }
    }

    private fun initUi() {
        binding.checkoutFragTipPercentView.setTipPercentViewListener(this)
        binding.checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)
        binding.checkoutFragHeaderView.setHeaderViewListener(this)
        binding.checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)
        binding.checkoutFragChangePayment.setDeliveryDetailsViewListener(this)
        binding.checkoutFragFees.setWSTitleValueListener(this)
        with(binding) {
            checkoutFragPromoCode.setOnClickListener {
                val promoCodeBottomSheet = PromoCodeBottomSheet()
                promoCodeBottomSheet.show(childFragmentManager, Constants.PROMO_CODE_TAG)
            }
            checkoutFragPlaceOrderBtn.setOnClickListener {
                viewModel.onPlaceOrderClick()
            }
        }

    }

    private fun openOrderTimeBottomSheet(deliveryDates: List<DeliveryDates>) {
        val timePickerBottomSheet = SingleColumnTimePickerBottomSheet(this)
        timePickerBottomSheet.setDeliveryDates(deliveryDates)
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onTimerPickerChange() {
        viewModel.onDeliveryTimeChanged()
//        mainViewModel.refreshFullDish()
    }


    private fun setEmptyPaymentMethod() {
        with(binding) {
            hasPaymentMethod = false
            checkoutFragChangePayment.updateSubTitle("Insert payment method")
        }
    }

    private fun handleCustomerCards(paymentMethods: PaymentMethod?) {
        if (paymentMethods != null) {
            updateCustomerPaymentMethod(paymentMethods)
        } else {
            setEmptyPaymentMethod()
        }
    }

    private fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        with(binding) {
            val card = paymentMethod.card
            card?.let {
                hasPaymentMethod = true
                Log.d("wowCheckoutFrag", "updateCustomerPaymentMethod: ${paymentMethod.id}")
                checkoutFragChangePayment.updateSubTitle("${card.brand} ●●●● ${card.last4}")
            }
        }
    }

    private fun handleOrderDetails(order: Order?) {
        order?.let {
            with(binding) {

                if (!it.orderItems.isNullOrEmpty()) {
                    var cook = it.restaurant

                    checkoutFragDeliveryAddress.updateDeliveryAddressFullDetails(it.deliveryAddress)

//                    if (it.estDeliveryTime != null) {
//                        val time = DateUtils.parseDateToDayDateAndTime(it.estDeliveryTime)
//                        checkoutFragDeliveryTime.updateDeliveryDetails(time)
//                    } else if (it.estDeliveryTimeText != null) {
//                        checkoutFragDeliveryTime.updateDeliveryDetails(it.estDeliveryTimeText)
//                    }

                    checkoutFragDetailsHeader.text = "Your Order From home chef ${cook?.firstName}"
                    checkoutFragOrderItemsView.setOrderItems(requireContext(), it.orderItems.toList(), this@CheckoutFragment)
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
//                checkoutFragSmallOrderFee.isNationWide(it.cookingSlot?.isNationwide) //fix this
                it.tipPercentage?.let { tip ->
                    if (tip != 0) {
                        checkoutFragTipPercentView.selectDefaultTip(tip)
                    }
                }
            }
            updatePriceUi(it)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePriceUi(curOrder: Order) {
        with(binding) {

            val deliveryFee = curOrder.deliveryFee?.value

            val promo = curOrder.promoCode

            if (!promo.isNullOrEmpty()) {
                checkoutFragPromoCode2.visibility = View.VISIBLE
                checkoutFragPromoCode2.setTitle("Promo code $promo")
                checkoutFragPromoCode2.setValue("(${curOrder.discount?.formatedValue?.replace("-", "")})")
                checkoutFragPromoCode.updateSubTitle("$promo")
            }

            var feeAndTax = 0.0
            curOrder.serviceFee?.value?.let {
                feeAndTax += it
            }
            curOrder.tax?.value?.let {
                feeAndTax += it
            }
            val feeAndTaxStr = DecimalFormat("##.##").format(feeAndTax)
            checkoutFragFees.setValue("$$feeAndTaxStr")



            checkoutFragDeliveryFee.setValue("$$deliveryFee")


            val allDishSubTotal = curOrder.subtotal?.value
            val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)

            checkoutFragSubtotal.setValue("$$allDishSubTotalStr")
            checkoutFragTotalBeforeTip.setValue(curOrder.totalBeforeTip?.formatedValue ?: "")

            curOrder.total?.formatedValue?.let {
                checkoutFragPlaceOrderBtn.updateFloatingBtnPrice(it)
            }
        }

    }

    override fun onShippingMethodChoose(chosenShippingMethod: ShippingMethod) {
        viewModel.updateOrderShippingMethod(shippingService = chosenShippingMethod.code)
        binding.checkoutFragNationwideSelect.updateSubTitle(chosenShippingMethod.name)
    }

//    override fun onClearCart() {
////        mainViewModel.clearCart()
////        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
//    }
//
//    override fun onCancelClearCart() {
//        //refresh orderItem counter (set to 0 by user and canceled)
//        viewModel.refreshUi()
//    }

    override fun onTipIconClick(tipSelection: Int?) {
        if (tipSelection == Constants.TIP_CUSTOM_SELECTED) {
            TipCourierDialog(this).show(childFragmentManager, Constants.TIP_COURIER_DIALOG_TAG)
        } else {
            if (tipSelection == TIP_NOT_SELECTED) {
                viewModel.updateOrderParams(
                    OrderRequest(tipPercentage = null, tip = 0),
                    Constants.EVENT_TIP
                ) //if server fix this issue (accept tip_percentage=null as no tip) you can delete this case
            } else {
                viewModel.updateOrderParams(OrderRequest(tipPercentage = tipSelection?.toFloat()), Constants.EVENT_TIP)
            }
        }
    }

    override fun onTipDone(tipAmount: Int) {
        binding.checkoutFragTipPercentView.setCustomTipValue(tipAmount)
        viewModel.updateOrderParams(OrderRequest(tipPercentage = null, tip = tipAmount * 100), Constants.EVENT_TIP)
    }


    override fun onCustomDetailsClick(type: Int) {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION -> {
                mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
            }
            Constants.DELIVERY_DETAILS_TIME -> {
                viewModel.onTimeChangeClick()
            }
            Constants.DELIVERY_DETAILS_PAYMENT -> {
                mainViewModel.startStripeOrReInit()
            }
            Constants.DELIVERY_DETAILS_NATIONWIDE_SHIPPING -> {
                viewModel.onNationwideShippingSelectClick()
            }
        }
    }

    override fun onDateChoose(selectedMenuItem: MenuItem, newChosenDate: Date) {
//        mainViewModel.updateDeliveryTime(newChosenDate)
    }

    override fun onHeaderBackClick() {
        activity?.onBackPressed()
//        if (mainViewModel.isCheckout) {
//            mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
//        } else {
//            activity?.onBackPressed()
//        }
    }

    override fun onEditOrderBtnClicked() {
        activity?.finish()
    }

    override fun onCustomToolTipClick() {
        viewModel.onFeesAndTaxInfoClick()
    }

    override fun onWSErrorDone() {
        viewModel.refreshCheckoutPage()
    }
}