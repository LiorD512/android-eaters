package com.bupp.wood_spoon_eaters.features.restaurant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.databinding.ActivityRestaurantBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutActivity
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.model.DishInitParams
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestaurantActivity : BaseActivity() {

    lateinit var binding: ActivityRestaurantBinding
    private val viewModel by viewModel<RestaurantMainViewModel>()

    private lateinit var navController: NavController

    //activityLauncher Results
    private val startCheckoutForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - startCheckoutForResult")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val isAfterPurchase = data?.getBooleanExtra("isAfterPurchase", false)
            if (isAfterPurchase!!) {
                intent.putExtra("isAfterPurchase", isAfterPurchase)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            val editOrderClicked = data.getBooleanExtra("editOrderClick", false)
            if (editOrderClicked) {
                viewModel.reOpenCart()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        viewModel.initExtras(intent.extras?.getParcelable(Constants.ARG_RESTAURANT), intent.extras?.getParcelable(Constants.ARG_DISH))
        checkStartDestination(intent.extras?.getParcelable(Constants.ARG_RESTAURANT), intent.extras?.getParcelable(Constants.ARG_DISH))
        initUi()
        initObservers()
    }

    /** Determine the starting fragment on activity launch -
     * can be one of (SearchFragment, HubPageFragment, MembershipPageFragment and SubCategoriesFragment)
     */
    private fun checkStartDestination(restaurantInitParams: RestaurantInitParams?, customOrderItem: CustomOrderItem?) {
        val dishInitParams = DishInitParams(cookingSlot = customOrderItem?.cookingSlot, orderItem = customOrderItem?.orderItem)
        val bundle = Bundle()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.restaurantActContainer) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.restaurant_nav)
        if (restaurantInitParams != null) {
            graph.startDestination = R.id.restaurantPageFragment
            bundle.putParcelable("extras", restaurantInitParams)
        } else {
            graph.startDestination = R.id.dishPageFragment
            bundle.putParcelable("extras", dishInitParams)
        }

        navController = navHostFragment.navController
        navController.setGraph(graph, bundle)
    }

    private fun initUi() {

    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, { navigationEvent ->
            handleNavigationEvent(navigationEvent)
        })
        viewModel.deliveryAtChangeEvent.observe(this, {
            it.getContentIfNotHandled()?.let { message ->
                showErrorToast(message, binding.root, Toast.LENGTH_LONG)
            }
        })
    }

    private fun handleNavigationEvent(navigationEvent: LiveEvent<RestaurantMainViewModel.NavigationEvent>) {
        MTLogger.c(body = "handleFragmentNavigationEvent called")
        val event = navigationEvent.getContentIfNotHandled()
        event?.let {
            when (it.navigationType) {
                RestaurantMainViewModel.NavigationType.OPEN_DISH_PAGE -> {
                    it.navDirections?.let { it1 ->
                        findNavController(R.id.restaurantActContainer).navigate(it1)
                    }
                }
                RestaurantMainViewModel.NavigationType.START_ORDER_CHECKOUT_ACTIVITY -> {
                    startCheckoutForResult.launch(Intent(this, OrderCheckoutActivity::class.java))
                }
                RestaurantMainViewModel.NavigationType.OPEN_DISH_SEARCH -> {
                    it.navDirections?.let { it1 ->
                        findNavController(R.id.restaurantActContainer).navigate(it1)
                    }
                }
                else -> {
                }
            }
        }
    }

    fun handleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.restaurantActProgressBar.show()
        } else {
            binding.restaurantActProgressBar.hide()
        }
    }

    override fun finish() {
        val shouldForceFeedRefresh = viewModel.shouldForceFeedRefresh()
        intent.putExtra("refreshFeed", shouldForceFeedRefresh)
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }

    companion object {
        private const val TAG = "wowRestaurantActivity"
    }
}