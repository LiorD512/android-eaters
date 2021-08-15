package com.bupp.wood_spoon_eaters.features.restaurant;

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.databinding.ActivityRestaurantBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestaurantActivity : AppCompatActivity() {

    lateinit var binding: ActivityRestaurantBinding
    private val viewModel by viewModel<RestaurantMainViewModel>()

    //activityLauncher Results
    private val startCheckoutForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - startCheckoutForResult")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val isAfterPurchase = data?.getBooleanExtra("isAfterPurchase", false)
            if(isAfterPurchase!!){
                intent.putExtra("isAfterPurchase", isAfterPurchase)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        viewModel.initExtras(intent.extras?.getParcelable(Constants.ARG_RESTAURANT), intent.extras?.getParcelable(Constants.ARG_DISH))
        initUi()
        initObservers()
    }

    private fun initUi() {

    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, { navigationEvent ->
            handleNavigationEvent(navigationEvent)
        })
    }

    private fun handleNavigationEvent(navigationEvent: LiveEvent<RestaurantMainViewModel.NavigationEvent>) {
        MTLogger.c(body = "handleFragmentNavigationEvent called")
        val event = navigationEvent.getContentIfNotHandled()
        event?.let {
            when(it.navigationType){
                RestaurantMainViewModel.NavigationType.OPEN_DISH_PAGE -> {
                    it.navDirections?.let { it1 ->
                        findNavController(R.id.restaurantActContainer).navigate(it1)
                    }
                }
                RestaurantMainViewModel.NavigationType.START_ORDER_CHECKOUT_ACTIVITY -> {
                    startCheckoutForResult.launch(Intent(this, OrderCheckoutActivity::class.java))
                }
                RestaurantMainViewModel.NavigationType.FINISH_RESTAURANT_ACTIVITY -> {

                }
                else -> {}
            }
        }
    }

    companion object {
        private const val TAG = "wowRestaurantActivity"
    }
}