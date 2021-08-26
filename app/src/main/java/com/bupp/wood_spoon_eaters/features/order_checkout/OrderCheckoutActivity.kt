package com.bupp.wood_spoon_eaters.features.order_checkout

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.IconCompat
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.custom_views.CheckoutHeaderView
import com.bupp.wood_spoon_eaters.databinding.ActivityMainBinding
import com.bupp.wood_spoon_eaters.databinding.ActivityOrderCheckoutActivityBinding
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.stripe.android.view.PaymentMethodsActivityStarter
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderCheckoutActivity : BaseActivity() {

    //activityLauncher Results
    private val startAddressChooserForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - startAddressChooserForResult")
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onLocationChanged()
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
        viewModel.navigationEvent.observe(this, {
            handleNavigation(it)
        })
    }

    private fun handleNavigation(mainNavigationEvent: OrderCheckoutViewModel.NavigationEvent) {
        when (mainNavigationEvent) {
            OrderCheckoutViewModel.NavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY -> {
                startAddressChooserForResult.launch(Intent(this, LocationAndAddressActivity::class.java))
            }
            OrderCheckoutViewModel.NavigationEvent.START_PAYMENT_METHOD_ACTIVITY -> {
                PaymentMethodsActivityStarter(this).startForResult(PaymentMethodsActivityStarter.Args.Builder().build())
            }
            OrderCheckoutViewModel.NavigationEvent.FINISH_CHECKOUT_ACTIVITY -> {
                finish()
            }
            OrderCheckoutViewModel.NavigationEvent.INITIALIZE_STRIPE -> {
                viewModel.reInitStripe(this)
            }
            OrderCheckoutViewModel.NavigationEvent.FINISH_ACTIVITY_AFTER_PURCHASE -> {
                intent.putExtra("isAfterPurchase", true)
                setResult(RESULT_OK, intent)
                finish()
            }
            OrderCheckoutViewModel.NavigationEvent.OPEN_PROMO_CODE_FRAGMENT -> {
                findNavController(R.id.checkoutActContainer).navigate(R.id.action_checkoutFragment_to_promoCodeFragment)
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
                        viewModel.updatePaymentMethod(this, result.paymentMethod)
                    }
                }

            }
        }
    }

    fun updateMainHeader(title: String, subtitle: String, icon: Int){
        binding.checkoutActHeader.updateHeaderUi(title, subtitle,icon)
        binding.checkoutActHeader.setOnIconClickListener { onBackPressed() }
    }

    fun onEditOrderClick() {
        intent.putExtra("editOrderClick", true)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object{
        const val TAG = "wowOrderCheckoutAct"
    }

}