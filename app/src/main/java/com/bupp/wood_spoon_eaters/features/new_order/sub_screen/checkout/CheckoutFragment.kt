package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

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
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.Constants.Companion.TIP_NOT_SELECTED
import com.bupp.wood_spoon_eaters.custom_views.order_item_view.OrderItemsView
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


class CheckoutFragment : Fragment(R.layout.checkout_fragment),
    TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, CustomDetailsView.CustomDetailsViewListener,
    HeaderView.HeaderViewListener, OrderDateChooserDialog.OrderDateChooserDialogListener,
    ClearCartDialog.ClearCartDialogListener,
    OrderUpdateErrorDialog.UpdateErrorDialogListener,
    NationwideShippingChooserDialog.NationwideShippingChooserListener, TimePickerBottomSheet.TimePickerListener, OrderItemsView.OrderItemsListener {

    private val binding: CheckoutFragmentBinding by viewBinding()

    private var hasPaymentMethod: Boolean = false


    val viewModel by viewModel<CheckoutViewModel>()
    val mainViewModel by sharedViewModel<NewOrderMainViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Checkout page")
        mainViewModel.proceedToCheckoutEvent()

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
        mainViewModel.orderData.observe(viewLifecycleOwner, { orderData ->
            handleOrderDetails(orderData)
            updateBottomBar(orderData.total)
        })
        viewModel.timeChangeEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                openOrderTimeBottomSheet(it)
            }
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
            if (emptyCartEvent) {
                ClearCartDialog(this@CheckoutFragment).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
            }
        })
        viewModel.shippingMethodsEvent.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    NationwideShippingChooserDialog.newInstance(ArrayList(it)).show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_SELECT_DIALOG)
                } else {
                    Toast.makeText(requireContext(), "UPS Service is not available at the moment, please try again later", Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainViewModel.validationError.observe(viewLifecycleOwner, {
            when (it) {
                NewOrderMainViewModel.OrderValidationErrorType.SHIPPING_METHOD_MISSING -> {
                    viewModel.onNationwideShippingSelectClick()
                }
//                NewOrderMainViewModel.OrderValidationErrorType.PAYMENT_METHOD_MISSING -> {
//                    mainViewModel.startStripeOrReInit()
//                }
            }
        })
    }

    private fun updateBottomBar(totalPrice: Price?) {
        mainViewModel.updateCartBottomBarByType(CartBottomBar.BottomBarTypes.PLACE_AN_ORDER, totalPrice?.value, null)
    }

    override fun onCancelUpdateOrderError() {
//        ordersViewModel.rollBackToPreviousAddress()
    }

    override fun onDishCountChange(updatedOrderItem: OrderItem, isCartEmpty: Boolean) {
        if (isCartEmpty) {
            mainViewModel.showClearCartDialog()
        } else {
            mainViewModel.updateOrderItem(updatedOrderItem)
        }
    }

    private fun initUi() {
        binding.checkoutFragTipPercentView.setTipPercentViewListener(this)
        binding.checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this)
        binding.checkoutFragHeaderView.setHeaderViewListener(this)
        binding.checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this)
        binding.checkoutFragChangePayment.setDeliveryDetailsViewListener(this)
        with(binding) {

            checkoutFragPromoCode.setOnClickListener {
                mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.PROMO_CODE)
            }

        }

        mainViewModel.getLastOrderDetails()

    }

    private fun openOrderTimeBottomSheet(menuItems: List<MenuItem>) {
        val timePickerBottomSheet = TimePickerBottomSheet(this)
        timePickerBottomSheet.setMenuItems(menuItems)
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onTimerPickerChange() {
        mainViewModel.onDeliveryTimeChange()
        mainViewModel.refreshFullDish()
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
                    var cook = it.cook

                    checkoutFragDeliveryAddress.updateDeliveryFullDetails(it.deliveryAddress)

                    if (it.estDeliveryTime != null) {
                        val time = DateUtils.parseDateToDayDateAndTime(it.estDeliveryTime)
                        checkoutFragDeliveryTime.updateDeliveryDetails(time)
                    } else if (it.estDeliveryTimeText != null) {
                        checkoutFragDeliveryTime.updateDeliveryDetails(it.estDeliveryTimeText)
                    }

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
        }

    }

    override fun onShippingMethodChoose(chosenShippingMethod: ShippingMethod) {
        viewModel.updateOrderShippingMethod(shippingService = chosenShippingMethod.code)
        binding.checkoutFragNationwideSelect.updateSubTitle(chosenShippingMethod.name)
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
            if (tipSelection == TIP_NOT_SELECTED) {
                viewModel.simpleUpdateOrder(
                    OrderRequest(tipPercentage = null, tip = 0),
                    Constants.EVENT_TIP
                ) //if server fix this issue (accept tip_percentage=null as no tip) you can delete this case
            } else {
                viewModel.simpleUpdateOrder(OrderRequest(tipPercentage = tipSelection?.toFloat()), Constants.EVENT_TIP)
            }
        }
    }

    override fun onTipDone(tipAmount: Int) {
        binding.checkoutFragTipPercentView.setCustomTipValue(tipAmount)
        viewModel.simpleUpdateOrder(OrderRequest(tipPercentage = null, tip = tipAmount * 100), Constants.EVENT_TIP)
    }


    override fun onCustomDetailsClick(type: Int) {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION -> {
                mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.LOCATION_AND_ADDRESS_ACTIVITY)
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
        mainViewModel.updateDeliveryTime(newChosenDate)
    }

    override fun onHeaderBackClick() {
        if (mainViewModel.isCheckout) {
            mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
        } else {
            activity?.onBackPressed()
        }
    }

    override fun onAddBtnClicked() {
        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.CHECKOUT_TO_ADD_MORE_DISH)
    }
}