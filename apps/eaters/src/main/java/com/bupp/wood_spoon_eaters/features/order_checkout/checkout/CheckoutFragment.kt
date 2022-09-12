package com.bupp.wood_spoon_eaters.features.order_checkout.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartCheckoutBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet.FeesAndTaxBottomSheetDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.nationwide_shipping_bottom_sheet.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.tool_tip_bottom_sheet.ToolTipBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.CheckoutHeaderView
import com.bupp.wood_spoon_eaters.custom_views.CustomDetailsView
import com.bupp.wood_spoon_eaters.databinding.CheckoutFragmentBinding
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentParams
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryState
import com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map.AddressVerificationMapFragment
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutActivity
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.models.CheckoutAdapterItem
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.order_items_view.CheckoutOrderItemsAdapter
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.order_items_view.CheckoutOrderItemsView
import com.bupp.wood_spoon_eaters.features.order_checkout.gift.GiftConfig
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.WSTitleValueView
import com.stripe.android.model.PaymentMethod
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import kotlin.collections.ArrayList


class CheckoutFragment : Fragment(R.layout.checkout_fragment),
    CustomDetailsView.CustomDetailsViewListener,
    NationwideShippingChooserDialog.NationwideShippingChooserListener,
    WSTitleValueView.WSTitleValueListener, WSErrorDialog.WSErrorListener,
    SingleColumnTimePickerBottomSheet.TimePickerListener,
    ClearCartCheckoutBottomSheet.ClearCartListener, CheckoutHeaderView.CheckoutHeaderListener {

    private var binding: CheckoutFragmentBinding? = null

    private var hasPaymentMethod: Boolean = false

    val viewModel by viewModel<CheckoutViewModel>()
    val mainViewModel by sharedViewModel<OrderCheckoutViewModel>()

    var isEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(isEnabled) {
                override fun handleOnBackPressed() {
                    mainViewModel.logEvent(Constants.EVENT_CLICK_BACK_FROM_CHECKOUT)
                    isEnabled = false
                    activity?.onBackPressed()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CheckoutFragmentBinding.bind(view)

        mainViewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_CHECKOUT)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!) {
            checkoutFragDeliveryTime.setDeliveryDetailsViewListener(this@CheckoutFragment)
            checkoutFragDeliveryAddress.setDeliveryDetailsViewListener(this@CheckoutFragment)
            checkoutFragDeliveryContactDetails.setDeliveryDetailsViewListener(this@CheckoutFragment)
            checkoutFragDeliveryContactDetails.isVisible = viewModel.showContactDetailsSection
            checkoutFragChangePayment.setDeliveryDetailsViewListener(this@CheckoutFragment)
            checkoutFragDeliveryFee.setWSTitleValueListener(this@CheckoutFragment)
            checkoutFragPromoCode.setDeliveryDetailsViewListener(this@CheckoutFragment)
            checkoutFragGift.setDeliveryDetailsViewListener(this@CheckoutFragment)
            checkoutFragFees.setWSTitleValueListener(this@CheckoutFragment)
            checkoutFragFreeDeliveryView.setAddItemsClickListener { (activity as OrderCheckoutActivity).onEditOrderClick() }

            checkoutFragPromoCode.setOnClickListener {
                mainViewModel.logEvent(Constants.EVENT_CLICK_ON_PROMO_CODE)
                mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.OPEN_PROMO_CODE_FRAGMENT)
            }
            checkoutFragPlaceOrderBtn.setOnClickListener {
                if (viewModel.validateOrderData()) {
                    mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.OPEN_TIP_FRAGMENT)
                }
            }
            checkoutFragHeader.setCheckoutHeaderListener(this@CheckoutFragment)
            initOrderItemsView()
        }
    }


    private fun initOrderItemsView() {
        with(binding!!) {
            //view listener
            val viewListener = object : CheckoutOrderItemsView.CheckoutOrderItemsViewListener {
                override fun onEditOrderBtnClicked() {
                    mainViewModel.logEvent(Constants.EVENT_CLICK_EDIT_ORDER)
                    (activity as OrderCheckoutActivity).onEditOrderClick()
                }
            }

            //adapter listener
            val adapterListener =
                object : CheckoutOrderItemsAdapter.CheckoutOrderItemsAdapterListener {
                    override fun onDishSwipedAdd(item: CheckoutAdapterItem) {
                        val dishId = item.customOrderItem.orderItem.dish.id
                        val note = item.customOrderItem.orderItem.notes
                        val orderId = item.customOrderItem.orderItem.id
                        val currentQuantity = item.customOrderItem.orderItem.quantity
                        viewModel.updateDishInCart(currentQuantity + 1, dishId, note, orderId)
//                    viewModel.logSwipeDishInCart(Constants.EVENT_SWIPE_ADD_DISH_IN_CART, item.customOrderItem)
                    }

                    override fun onDishSwipedRemove(item: CheckoutAdapterItem) {
                        val orderItemId = item.customOrderItem.orderItem.id
                        val showDialog = viewModel.removeSingleOrderItemId(orderItemId)
                        if (showDialog) {
                            ClearCartCheckoutBottomSheet.newInstance(this@CheckoutFragment)
                                .show(
                                    childFragmentManager,
                                    Constants.CLEAR_CART_RESTAURANT_DIALOG_TAG
                                )
                        }
//                    viewModel.logSwipeDishInCart(Constants.EVENT_SWIPE_REMOVE_DISH_IN_CART, item.customOrderItem)
                    }

                    override fun onDishClicked(customOrderItem: CustomOrderItem) {
                        mainViewModel.openDishPageWithOrderItem(customOrderItem)
                    }

                }
            //init view
            checkoutFragOrderItemsView.initView(
                adapterListener = adapterListener,
                viewListener = viewListener
            )
        }
    }

    private fun initObservers() {
        viewModel.progressData.observe(viewLifecycleOwner) {
            if (it) {
                binding!!.checkoutFragmentPb.show()
            } else {
                binding!!.checkoutFragmentPb.hide()
            }
        }
        viewModel.orderLiveData.observe(viewLifecycleOwner) { orderData ->
            handleOrderDetails(orderData)
            initMap(orderData)
        }
        viewModel.deliveryDatesLiveData.observe(viewLifecycleOwner) {
            handleOrderDeliveryDates(it)
        }
        viewModel.timeChangeEvent.observe(
            viewLifecycleOwner
        ) {
            it.getContentIfNotHandled()?.let {
                openOrderTimeBottomSheet(it)
            }

        }
        viewModel.getStripeCustomerCards.observe(viewLifecycleOwner) { cardsEvent ->
            Log.d("wowCheckoutFrag", "getStripeCustomerCards()")
            if (cardsEvent != null) {
                handleCustomerCards(cardsEvent)
            } else {
                setEmptyPaymentMethod()
            }
        }
        viewModel.feeAndTaxDialogData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { data ->
                FeesAndTaxBottomSheetDialog.newInstance(data.fee, data.tax, data.minOrderFee)
                    .show(childFragmentManager, Constants.FEES_AND_tAX_BOTTOM_SHEET)
            }
        }
        viewModel.shippingMethodsEvent.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    NationwideShippingChooserDialog.newInstance(ArrayList(it))
                        .show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_SELECT_DIALOG)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "UPS Service is not available at the moment, please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        viewModel.validationError.observe(viewLifecycleOwner) {
            when (it) {
                CheckoutViewModel.OrderValidationErrorType.SHIPPING_METHOD_MISSING -> {
                    viewModel.onNationwideShippingSelectClick()
                }
                CheckoutViewModel.OrderValidationErrorType.PAYMENT_METHOD_MISSING -> {
                    mainViewModel.startStripeOrReInit()
                }
                CheckoutViewModel.OrderValidationErrorType.SHIPPING_ADDRESS_MISSING -> {
                    mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.START_LOCATION_AND_ADDRESS_ACTIVITY)
                }
                CheckoutViewModel.OrderValidationErrorType.USER_DETAILS_MISSING -> {
                    mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.START_USER_DETAILS_ACTIVITY)
                }
            }
        }
        viewModel.wsErrorEvent.observe(viewLifecycleOwner) {
            handleWSError(it.getContentIfNotHandled())
        }
        viewModel.deliveryDatesUi.observe(viewLifecycleOwner) {
            binding!!.checkoutFragDeliveryTime.updateDeliveryTimeUi(it)
        }
        viewModel.orderItemsData.observe(viewLifecycleOwner) {
            handleOrderItemsData(it)
        }
        viewModel.pricingExperimentData.observe(viewLifecycleOwner) {
            handlePricingExperiment(it)
        }
        viewModel.giftConfigData.observe(viewLifecycleOwner) {
            handleGiftConfig(it)
        }
        viewModel.userData.observe(viewLifecycleOwner) {
            handleUserDetail(it)
        }
        viewModel.freeDeliveryData.observe(viewLifecycleOwner){
            setFreeDeliveryViewState(it)
        }
    }

    private fun setFreeDeliveryViewState(freeDeliveryState: FreeDeliveryState?){
        binding?.apply {
            checkoutFragFreeDeliveryView.setFreeDeliveryState(freeDeliveryState)
        }
    }

    private fun initMap(orderData: Order?) {
        val bundle = Bundle()
        bundle.putFloat("zoom_level", 17f)
        bundle.putBoolean("show_btns", false)
        bundle.putBoolean("isCheckout", true)
        bundle.putParcelable("order", orderData)
        childFragmentManager.beginTransaction()
            .add(R.id.checkoutFragMap, AddressVerificationMapFragment::class.java, bundle)
            .commit()
    }

    private fun handleOrderItemsData(list: List<CheckoutAdapterItem>) {
        with(binding!!) {
            checkoutFragOrderItemsView.submitList(list)
        }
    }

    private fun handlePricingExperiment(pricingParams: PricingExperimentParams) {
        with(binding!!) {
            checkoutFragFees.setTitle(pricingParams.feeAndTaxTitle)
        }
    }

    private fun handleGiftConfig(giftConfig: GiftConfig) {
        with(binding!!) {
            checkoutFragGift.isVisible = giftConfig.isGiftEnabled
        }
    }


    private fun handleOrderDeliveryDates(deliveryDates: List<DeliveryDates>) {
        if (deliveryDates.isEmpty()) {
            binding!!.checkoutFragDeliveryTime.setChangeable(false)
        }
    }

    private fun handleWSError(errorEvent: String?) {
        errorEvent?.let {
            WSErrorDialog(it, this).show(childFragmentManager, Constants.ERROR_DIALOG)
        }
    }

    private fun openOrderTimeBottomSheet(deliveryDatesData: CheckoutViewModel.TimePickerData) {
        val timePickerBottomSheet = SingleColumnTimePickerBottomSheet(this)
        timePickerBottomSheet.setDeliveryDates(
            deliveryDatesData.selectedDate,
            deliveryDatesData.deliveryDates
        )
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onTimerPickerChange(deliveryTimeParam: SingleColumnTimePickerBottomSheet.DeliveryTimeParam?) {
        viewModel.updateDeliveryAt(deliveryTimeParam?.date)
        mainViewModel.logChangeTime(deliveryTimeParam?.date)
    }

    private fun setEmptyPaymentMethod() {
        with(binding!!) {
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
        with(binding!!) {
            val card = paymentMethod.card
            card?.let {
                hasPaymentMethod = true
                Log.d("wowCheckoutFrag", "updateCustomerPaymentMethod: ${paymentMethod.id}")
                checkoutFragChangePayment.updateSubTitle("${card.brand} ●●●● ${card.last4}")
            }
        }
    }

    private fun handleOrderDetails(order: Order?) {
        if (order != null) {
            with(binding!!) {
                viewModel.fetchOrderDeliveryTimes()
                viewModel.handleOrderItems(order)

                checkoutFragHeader.setSubtitle(order.restaurant?.restaurantName ?: "")

                if (!order.orderItems.isNullOrEmpty()) {
                    var cook = order.restaurant

                    checkoutFragDeliveryAddress.updateDeliveryAddressFullDetails(order.deliveryAddress)
//                    checkoutFragOrderItemsView.setOrderItems(requireContext(), it.orderItems.toList(), this@CheckoutFragment)
                }

                checkoutFragGift.updateGiftingFullDetails(order)

                order.cookingSlot?.isNationwide?.let {
                    if (it) {
                        checkoutFragDeliveryTime.visibility = View.GONE
                        checkoutFragNationwideSelect.visibility = View.VISIBLE
                        checkoutFragNationwideSelect.setDeliveryDetailsViewListener(this@CheckoutFragment)
                    } else {
                        checkoutFragDeliveryTime.visibility = View.VISIBLE
                        checkoutFragNationwideSelect.visibility = View.GONE
                    }
                }
            }
            updatePriceUi(order)
        } else {
            mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.FINISH_CHECKOUT_ACTIVITY)
        }
    }

    private fun handleUserDetail(user: Eater?) {
        binding?.checkoutFragDeliveryContactDetails?.updateUserDetails(user)
    }

    @SuppressLint("SetTextI18n")
    private fun updatePriceUi(curOrder: Order) {
        with(binding!!) {

            val deliveryFee = curOrder.deliveryFee?.value

            val promo = curOrder.promoCode

            if (!promo.isNullOrEmpty()) {
                checkoutFragPromoCode2.visibility = View.VISIBLE
                checkoutFragPromoCode2.setTitle("Promo code $promo")
                checkoutFragPromoCode2.setValue("${curOrder.discount?.formatedValue}")
                checkoutFragPromoCode.updateSubTitle("$promo")
            }

            var feeAndTax = 0.0
            curOrder.serviceFee?.value?.let {
                feeAndTax += it
            }
            curOrder.tax?.value?.let {
                feeAndTax += it
            }
            curOrder.minOrderFee?.value?.let {
                feeAndTax += it
            }

            val feeAndTaxStr = DecimalFormat("##.##").format(feeAndTax)
            checkoutFragFees.setValue("$$feeAndTaxStr")

            checkoutFragDeliveryFee.setValue("$$deliveryFee")

            val allDishSubTotal = curOrder.subtotal?.value
            val allDishSubTotalStr = DecimalFormat("##.##").format(allDishSubTotal)

            checkoutFragSubtotal.setValue("$$allDishSubTotalStr")
            checkoutFragTotalBeforeTip.setValue(curOrder.totalBeforeTip?.formatedValue ?: "")

            curOrder.totalBeforeTip?.formatedValue?.let {
                checkoutFragPlaceOrderBtn.updateFloatingBtnPrice(it)
            }
        }
    }

    override fun onShippingMethodChoose(chosenShippingMethod: ShippingMethod) {
        viewModel.updateOrderShippingMethod(shippingService = chosenShippingMethod.code)
        binding!!.checkoutFragNationwideSelect.updateSubTitle(chosenShippingMethod.name)
    }

    override fun onCustomDetailsClick(type: Int) {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION -> {
                mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.START_LOCATION_AND_ADDRESS_ACTIVITY)
            }
            Constants.DELIVERY_DETAILS_CONTACT_DETAILS -> {
                mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.START_USER_DETAILS_ACTIVITY)
            }
            Constants.DELIVERY_DETAILS_TIME -> {
                viewModel.onTimeChangeClick()
            }
            Constants.DELIVERY_DETAILS_PAYMENT -> {
                mainViewModel.logEvent(Constants.EVENT_CLICK_PAYMENT)
                mainViewModel.startStripeOrReInit()
            }
            Constants.DELIVERY_DETAILS_NATIONWIDE_SHIPPING -> {
                viewModel.onNationwideShippingSelectClick()
            }
            Constants.DELIVERY_DETAILS_PROMO_CODE -> {
                mainViewModel.logEvent(Constants.EVENT_CLICK_ON_PROMO_CODE)
                mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.OPEN_PROMO_CODE_FRAGMENT)
            }
            Constants.DELIVERY_DETAILS_GIFT -> {
                mainViewModel.logEventGiftClicked()
                mainViewModel.handleMainNavigation(OrderCheckoutViewModel.NavigationEventType.OPEN_GIFT_FRAGMENT)
            }
        }
    }

    override fun onToolTipClick(type: Int) {
        var titleText = ""
        var bodyText = ""
        if (type == Constants.FEES_AND_ESTIMATED_TAX) {
            viewModel.onFeesAndTaxInfoClick()
        } else {
            when (type) {
                Constants.TOOL_TIP_SERVICE_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_service_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                }
                Constants.TOOL_TIP_CHECKOUT_SERVICE_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_service_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                }
                Constants.TOOL_TIP_CHECKOUT_DELIVERY_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_delivery_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_delivery_fee_body)
                }
                Constants.TOOL_TIP_COURIER_TIP -> {
                    titleText = resources.getString(R.string.tool_tip_courier_title)
                    bodyText = resources.getString(R.string.tool_tip_courier_body)
                }
            }
            ToolTipBottomSheet.newInstance(titleText, bodyText)
                .show(childFragmentManager, Constants.FREE_TEXT_BOTTOM_SHEET)
        }
    }

    override fun onWSErrorDone() {
        viewModel.refreshCheckoutPage()
    }

    override fun onPerformClearCart() {
        viewModel.clearCart()
    }

    override fun onClearCartCanceled() {
        //do nothing
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCloseBtnClick() {
        activity?.finish()
    }
}