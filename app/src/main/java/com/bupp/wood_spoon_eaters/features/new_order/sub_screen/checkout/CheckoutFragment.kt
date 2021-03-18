package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.TipPercentView
import com.bupp.wood_spoon_eaters.custom_views.order_item_view.OrderItemsViewAdapter
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.bottom_sheets.nationwide_shipping_bottom_sheet.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.OrderDateChooserDialog
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


class CheckoutFragment : Fragment(R.layout.checkout_fragment),
    TipPercentView.TipPercentViewListener, TipCourierDialog.TipCourierDialogListener, DeliveryDetailsView.DeliveryDetailsViewListener,
    HeaderView.HeaderViewListener, OrderItemsViewAdapter.OrderItemsViewAdapterListener, OrderDateChooserDialog.OrderDateChooserDialogListener,
    ClearCartDialog.ClearCartDialogListener,
    OrderUpdateErrorDialog.updateErrorDialogListener,
    NationwideShippingChooserDialog.NationwideShippingChooserListener, TimePickerBottomSheet.TimePickerListener {

    private var binding: CheckoutFragmentBinding? = null

    private var hasPaymentMethod: Boolean = false


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
        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding!!.checkoutFragmentPb.show()
            } else {
                binding!!.checkoutFragmentPb.hide()
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
//        viewModel.getDeliveryTimeLiveData().observe(viewLifecycleOwner, {
//
//        })
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
                NewOrderMainViewModel.OrderValidationErrorType.PAYMENT_METHOD_MISSING -> {
                    binding!!.checkoutFragChangePaymentLayout.performClick()
                }
            }
        })
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

            checkoutFragChangePaymentLayout.setOnClickListener {
                mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.START_PAYMENT_METHOD_ACTIVITY)
            }

            checkoutFragUtensilsSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.simpleUpdateOrder(OrderRequest(addUtensils = isChecked))
                if (isChecked) {
                    checkoutFragUtensilsText.text = getString(R.string.checkout_utensils_on)
                } else {
                    checkoutFragUtensilsText.text = getString(R.string.checkout_utensils_off)
                }
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

    private fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        with(binding!!) {
            val card = paymentMethod.card
            card?.let {
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
                        val time = DateUtils.parseDateToDayDateAndTime(it.estDeliveryTime)
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
        if (isCartEmpty) {
            mainViewModel.showClearCartDialog()
        } else {
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
            serviceFee?.let {
                if (serviceFee > 0.0) {
                    checkoutFragServiceFeePriceText.text = "$$serviceFee"
                    checkoutFragServiceFeePriceText.visibility = View.VISIBLE
                    checkoutFragServiceFeePriceFree.visibility = View.GONE
                } else {
                    checkoutFragServiceFeePriceText.visibility = View.GONE
                    checkoutFragServiceFeePriceFree.visibility = View.VISIBLE
                }
            }
            deliveryFee?.let {
                if (deliveryFee > 0.0) {
                    checkoutFragDeliveryFeePriceText.text = "$$deliveryFee"
                    checkoutFragDeliveryFeePriceText.visibility = View.VISIBLE
                    checkoutFragDeliveryFeePriceFree.visibility = View.GONE
                } else {
                    checkoutFragDeliveryFeePriceText.visibility = View.GONE
                    checkoutFragDeliveryFeePriceFree.visibility = View.VISIBLE
                }
            }

            val allDishSubTotal = curOrder.subtotal?.value
            val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)

            checkoutFragSubtotalPriceText.text = "$$allDishSubTotalStr"
            checkoutFragTotalPriceText.text = curOrder.totalBeforeTip?.formatedValue ?: ""
        }

    }

    override fun onShippingMethodChoose(chosenShippingMethod: ShippingMethod) {
        viewModel.updateOrderShippingMethod(shippingService = chosenShippingMethod.code)
        binding!!.checkoutFragNationwideSelect.updateNationwideShippingDetails(chosenShippingMethod.name)
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
            viewModel.simpleUpdateOrder(OrderRequest(tipPercentage = tipSelection?.toFloat()), Constants.EVENT_TIP)
        }
    }

    override fun onTipDone(tipAmount: Int) {
        binding!!.checkoutFragTipPercentView.setCustomTipValue(tipAmount)
        viewModel.simpleUpdateOrder(OrderRequest(tipAmount = tipAmount.toString()), Constants.EVENT_TIP)
    }

    override fun onChangeLocationClick() {
        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.LOCATION_AND_ADDRESS_ACTIVITY)
//        (activity as NewOrderActivity).loadAddressesDialog()
    }

    override fun onChangeTimeClick() {
        viewModel.onTimeChangeClick()
    }

    override fun onNationwideShippingChange() {
        viewModel.onNationwideShippingSelectClick()
    }

    private fun openOrderTimeDialog() {
//        ordersViewModel.editDeliveryTime()
    }

    override fun onDateChoose(selectedMenuItem: MenuItem, newChosenDate: Date) {
        mainViewModel.updateDeliveryTime(newChosenDate)
    }


    override fun onHeaderBackClick() {
        if(mainViewModel.isCheckout){
            mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
        }else{
            activity?.onBackPressed()
        }

    }




}
