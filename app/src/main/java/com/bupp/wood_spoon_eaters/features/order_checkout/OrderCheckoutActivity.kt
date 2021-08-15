package com.bupp.wood_spoon_eaters.features.order_checkout

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.stripe.android.view.PaymentMethodsActivityStarter
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderCheckoutActivity : AppCompatActivity() {

    val viewModel by viewModel<OrderCheckoutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_checkout_activity)

        initUi()
        initObservers()
    }

    private fun initUi() {

    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, {
            handleNavigation(it)
        })
    }

    private fun handleNavigation(mainNavigationEvent: OrderCheckoutViewModel.NavigationEvent) {
        when (mainNavigationEvent) {
            OrderCheckoutViewModel.NavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY -> {
//                updateLocationOnResult.launch(Intent(this, LocationAndAddressActivity::class.java))
            }
            OrderCheckoutViewModel.NavigationEvent.START_PAYMENT_METHOD_ACTIVITY -> {
                PaymentMethodsActivityStarter(this).startForResult(PaymentMethodsActivityStarter.Args.Builder().build())
            }
            OrderCheckoutViewModel.NavigationEvent.INITIALIZE_STRIPE -> {
                viewModel.reInitStripe(this)
            }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PaymentMethodsActivityStarter.REQUEST_CODE -> {
                    MTLogger.c(MainActivity.TAG, "Stripe")
                    val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                    result?.let {
                        MTLogger.c(MainActivity.TAG, "payment method success")
                        viewModel.updatePaymentMethod(result.paymentMethod)
                    }
                }

            }
        }
    }

}