package com.bupp.wood_spoon_eaters.features.order_checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.databinding.ActivityOrderCheckoutActivityBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.create_profile.EditProfileActivity
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.CheckoutFragmentDirections
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import com.stripe.android.view.PaymentMethodsActivityStarter
import com.uxcam.UXCam
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderCheckoutActivity : BaseActivity() {

    //activityLauncher Results
    private val startAddressChooserForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d(TAG, "Activity For Result - startAddressChooserForResult")
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onLocationChanged()
            }
        }

    private val startUserDetailsForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d(TAG, "Activity For Result - startUserDetailsForResult")
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onUserDetailsUpdated()
            }
        }

    val viewModel by viewModel<OrderCheckoutViewModel>()
    lateinit var binding: ActivityOrderCheckoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderCheckoutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initObservers()
    }

    private fun initUi() {

    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this) {
            handleNavigation(it)
        }
        viewModel.deliveryAtChangeEvent.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                showErrorToast(message, binding.root, Toast.LENGTH_LONG)
            }
        }
    }

    private fun handleNavigation(mainNavigationEvent: LiveEvent<OrderCheckoutViewModel.NavigationEvent>) {
        mainNavigationEvent.getContentIfNotHandled()?.let { event ->
            when (event) {
                OrderCheckoutViewModel.NavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY -> {
                    startAddressChooserForResult.launch(
                        Intent(
                            this,
                            LocationAndAddressActivity::class.java
                        )
                    )
                }
                is OrderCheckoutViewModel.NavigationEvent.START_USER_DETAILS_ACTIVITY ->  {
                    startUserDetailsForResult.launch(
                        EditProfileActivity.createIntent(this, event.alternativeReasonDescription)
                    )
                }
                OrderCheckoutViewModel.NavigationEvent.START_PAYMENT_METHOD_ACTIVITY -> {
                    UXCam.occludeSensitiveScreen(true)
                    PaymentMethodsActivityStarter(this).startForResult(
                        PaymentMethodsActivityStarter.Args.Builder().build()
                    )
                }
                OrderCheckoutViewModel.NavigationEvent.FINISH_CHECKOUT_ACTIVITY -> {
                    finish()
                }
                OrderCheckoutViewModel.NavigationEvent.FINISH_ACTIVITY_AFTER_PURCHASE -> {
                    intent.putExtra("isAfterPurchase", true)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                OrderCheckoutViewModel.NavigationEvent.OPEN_PROMO_CODE_FRAGMENT -> {
                    findNavController(R.id.checkoutActContainer).navigate(R.id.action_checkoutFragment_to_promoCodeFragment)
                }
                OrderCheckoutViewModel.NavigationEvent.OPEN_GIFT_FRAGMENT -> {
                    findNavController(R.id.checkoutActContainer).navigate(R.id.action_checkoutFragment_to_giftActionsDialogFragment)
                }
                is OrderCheckoutViewModel.NavigationEvent.OPEN_DISH_PAGE -> {
                    val action = CheckoutFragmentDirections.actionCheckoutFragmentToDishPageFragment(event.dishInitParams)
                    findNavController(R.id.checkoutActContainer).navigate(action)
                }
                OrderCheckoutViewModel.NavigationEvent.OPEN_TIP_FRAGMENT -> {
                    findNavController(R.id.checkoutActContainer).navigate(R.id.action_checkoutFragment_to_tipFragment)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PaymentMethodsActivityStarter.REQUEST_CODE -> {
                MTLogger.c(MainActivity.TAG, "Stripe")
                UXCam.occludeSensitiveScreen(false)
                val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                result?.let {
                    MTLogger.c(MainActivity.TAG, "payment method success")
                    viewModel.updatePaymentMethod(this, result.paymentMethod)
                }
            }

        }
    }

    fun handleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }

    fun onEditOrderClick() {
        intent.putExtra("editOrderClick", true)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val TAG = "wowOrderCheckoutAct"
    }

}