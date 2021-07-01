package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.additional_dishes.AdditionalDishesDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.promo_code.PromoCodeBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.dialogs.AddressMissingDialog
import com.bupp.wood_spoon_eaters.dialogs.StartNewCartDialog
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.rating_dialog.RatingsBottomSheet
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.databinding.ActivityNewOrderBinding
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.utils.navigateSafe
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.PaymentMethodsActivity
import com.stripe.android.view.PaymentMethodsActivityStarter
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewOrderActivity : BaseActivity(),
    StartNewCartDialog.StartNewCartDialogListener, CartBottomBar.OrderBottomBatListener,
    AddressMissingDialog.AddressMissingDialogListener {

    companion object {
        const val TAG = "wowNewOrderAct"
    }

    //activityLauncher Results
    private val startAddressChooserForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - startAddressChooserForResult")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            viewModel.onLocationChanged()
            viewModel.handleAddToCartClick()
        }
    }

    lateinit var binding: ActivityNewOrderBinding
    val viewModel by viewModel<NewOrderMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_new_order)

        initUi()
        initObservers()

        viewModel.initNewOrderActivity(intent)
    }

    private fun initUi() {
        binding.singleDishStatusBar.setCartBottomBarListener(this)
    }

    private fun initObservers() {
//        viewModel.deliveryTimeLiveData.observe(this, {
//            viewModel.initNewOrderActivity(intent)
//        })
        viewModel.navigationEvent.observe(this, {
            handleNavigationEvent(it)
        })
        viewModel.ephemeralKeyProvider.observe(this, { event ->
            if (!event.isSuccess) {
                Toast.makeText(this, "Error while loading payments method", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.startNewCartEvent.observe(this, { newCartEvent ->
            showNewCartDialog(newCartEvent)
        })
        viewModel.cartBottomBarTypeEvent.observe(this, {
            updateStatusBottomBar(it)
        })
        viewModel.mainActionEvent.observe(this, {
            when (it) {
                NewOrderMainViewModel.NewOrderActionEvent.SHOW_ADDITIONAL_DISH_DIALOG -> {
                    AdditionalDishesDialog().show(supportFragmentManager, Constants.ADDITIONAL_DISHES_DIALOG)
                }
                NewOrderMainViewModel.NewOrderActionEvent.INITIALIZE_STRIPE -> {
                    viewModel.reInitStripe(this)
                }
                else -> {
                }
            }
        })
        viewModel.wsErrorEvent.observe(this, {
            if (!it.isNullOrEmpty()) {
                it[0].let { wsError ->
                    WSErrorDialog(wsError.msg, null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
            }
        })

        viewModel.getReviewsEvent.observe(this, Observer {
            it?.let {
                RatingsBottomSheet(it).show(supportFragmentManager, Constants.RATINGS_DIALOG_TAG)
            }
        })
        viewModel.progressData.observe(this, {
            Log.d(TAG, "progressData observer: $it")
            if (it) {
                binding.newOrderActPb.show()
            } else {
                binding.newOrderActPb.hide()
            }
        })
        viewModel.stripeInitializationEvent.observe(this, {
            Log.d(TAG, "stripeInitializationEvent status: $it")
            when (it) {
                PaymentManager.StripeInitializationStatus.START -> {
                    binding.newOrderActPb.show()
                }
                PaymentManager.StripeInitializationStatus.SUCCESS -> {
                    binding.newOrderActPb.hide()
                }
                PaymentManager.StripeInitializationStatus.FAIL -> {
                    binding.newOrderActPb.hide()
                }
            }
        })
    }

    private fun showNewCartDialog(newCartEvent: NewOrderMainViewModel.NewCartDialog?) {
        newCartEvent?.let {
            val bundle = Bundle()
            bundle.putString(Constants.START_NEW_CART_IN_CART_COOK_NAME_ARG, it.inCartCookName)
            bundle.putString(Constants.START_NEW_CART_CURRENT_COOK_NAME_ARG, it.currentShowingCookName)
            val dialog = StartNewCartDialog(this)
            dialog.arguments = bundle
            dialog.show(supportFragmentManager, Constants.START_NEW_CART_DIALOG_TAG)
        }
    }

    private fun updateStatusBottomBar(bottomBarEvent: NewOrderMainViewModel.CartBottomBarTypeEvent) {
        Log.d(TAG, "updateStatusBottomBar type: ${bottomBarEvent.type}")
        if (binding.singleDishStatusBar.visibility != View.VISIBLE) {
            binding.singleDishStatusBar.visibility = View.VISIBLE
        }
        binding.singleDishStatusBar.updateStatusBottomBarByType(
            type = bottomBarEvent.type,
            price = bottomBarEvent.price,
            itemCount = bottomBarEvent.itemCount,
            totalPrice = bottomBarEvent.totalPrice
        )
    }

    override fun onCartBottomBarOrdersClick(type: CartBottomBar.BottomBarTypes) {
        viewModel.onCartBottomBarClick(type)
    }

    override fun onBottomBarCheckoutClick() {
        redirectToCheckout()
    }

    private fun redirectToCheckout() {
        viewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.CHECKOUT)
        viewModel.updateCartBottomBarByType(CartBottomBar.BottomBarTypes.PLACE_AN_ORDER, null, null)
    }

    override fun onNewCartClick() {
        viewModel.clearCart()
    }

    override fun onCancelClick() {
        //this method called when user open newdish from a diffrent cooking slot
        //and he chooses cancel in the clear cart dialog. redirect to checkout with old (still current) order.
        redirectToCheckout()
    }


    private fun finishNewOrder() {
        viewModel.onNewOrderFinish()
        finish()
    }

    //Address Missing Dialog interface
    override fun openUpdateAddress() {
        startAddressChooserForResult.launch(Intent(this, LocationAndAddressActivity::class.java))
    }

    private fun handleNavigationEvent(event: NewOrderMainViewModel.NewOrderNavigationEvent?) {
        when (event) {
            NewOrderMainViewModel.NewOrderNavigationEvent.SHOW_ADDRESS_MISSING_DIALOG -> {
//                AddressMissingDialog(this).show(supportFragmentManager, Constants.ADDRESS_MISSING_DIALOG)
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.MAIN_TO_CHECKOUT -> {
//                val action = NewOrderMainFragmentDirections.actionNewOrderMainFragmentToCheckoutFragment()
                findNavController(R.id.newOrderContainer).navigateSafe(R.id.action_newOrderMainFragment_to_checkoutFragment)
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.REDIRECT_TO_SELECT_PROMO_CODE -> {
                val promoCodeBottomSheet = PromoCodeBottomSheet()
                promoCodeBottomSheet.show(supportFragmentManager, Constants.COUNTRY_CODE_BOTTOM_SHEET)
//                val action = CheckoutFragmentDirections.actionCheckoutFragmentToPromoCodeFragment()
//                findNavController(R.id.newOrderContainer).navigate(action)
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.BACK_TO_PREVIOUS -> {
                handleBackPressed(true)
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY -> {
                openUpdateAddress()
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.START_PAYMENT_METHOD_ACTIVITY -> {
                PaymentMethodsActivityStarter(this).startForResult(PaymentMethodsActivityStarter.Args.Builder().build())
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.FINISH_ACTIVITY -> {
                finishNewOrder()
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.FINISH_ACTIVITY_AFTER_PURCHASE -> {
                intent.putExtra("isAfterPurchase", true)
                setResult(Activity.RESULT_OK, intent)
                finishNewOrder()
            }
            NewOrderMainViewModel.NewOrderNavigationEvent.CHECKOUT_TO_ADD_MORE_DISH -> {
                val args = Bundle().apply {
                    putInt("startScreen", 2)
                }
                findNavController(R.id.newOrderContainer).navigateSafe(R.id.action_checkoutFragment_to_newOrderMainFragment)
                viewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.LOCK_SINGLE_DISH_COOK)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PaymentMethodsActivityStarter.REQUEST_CODE -> {
                    MTLogger.d(MainActivity.TAG, "Stripe")
                    val result = PaymentMethodsActivityStarter.Result.fromIntent(data)

                    result?.let {
                        MTLogger.d(MainActivity.TAG, "payment method success")
                        viewModel.updatePaymentMethod(result.paymentMethod)
                    }
                }

            }
        }
    }

    private fun handleBackPressed(force: Boolean = false) {
        if (force) {
            onBackPressed()
        } else {
            viewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.BACK_PRESS)
        }
    }

    override fun onBackPressed() {
        if (viewModel.isCheckout) {
            viewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
        } else {
            super.onBackPressed()
        }
    }




}
