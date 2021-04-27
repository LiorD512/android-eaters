package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.additional_dishes.AdditionalDishesDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.promo_code.PromoCodeBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.dialogs.AddressMissingDialog
import com.bupp.wood_spoon_eaters.dialogs.StartNewCartDialog
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.NewOrderMainFragmentDirections
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutFragment
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.utils.navigateSafe
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import com.stripe.android.view.PaymentMethodsActivityStarter
import kotlinx.android.synthetic.main.activity_new_order.*
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
        }
    }

    val viewModel by viewModel<NewOrderMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)

        initUi()
        initObservers()

        viewModel.initNewOrderActivity(intent)
    }

    private fun initUi() {
        singleDishStatusBar.setCartBottomBarListener(this)
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
                RatingsDialog(it).show(supportFragmentManager, Constants.RATINGS_DIALOG_TAG)
            }
        })
        viewModel.progressData.observe(this, {
            Log.d(TAG, "progressData observer: $it")
            if(it){
                newOrderActPb.show()
            }else{
                newOrderActPb.hide()
            }
        })
    }

    private fun showNewCartDialog(newCartEvent: NewOrderMainViewModel.NewCartDialog?) {
        newCartEvent?.let{
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
        if (singleDishStatusBar.visibility != View.VISIBLE) {
            singleDishStatusBar.visibility = View.VISIBLE
        }
        singleDishStatusBar.updateStatusBottomBarByType(
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //change this to ActivityResultStarterCallback when stripe enables.
        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {
//            val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
            Log.d(TAG, "Stripe on activity result")
            viewModel.refreshPaymentsMethod(this)
            }
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
//        val navBuilder = NavOptions.Builder()
//        navBuilder.setEnterAnim(R.anim.slide_right_enter).setExitAnim(R.anim.slide_right_exit).setPopEnterAnim(R.anim.slide_left_enter).setPopExitAnim(R.anim.slide_left_exit)
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
        }
    }

    private fun handleBackPressed(force: Boolean = false) {
        if (force) {
            onBackPressed()
        } else {
            viewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.BACK_PRESS)
        }
    }

//    override fun onResume() {
//        super.onResume()
////        updateUI()
//    }


//    private fun updateUI() {
//        val decorView = window.decorView
//        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
//            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
//                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//            }
//        }
//    }


}
