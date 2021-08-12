package com.bupp.wood_spoon_eaters.features.restaurant;

import android.os.Bundle
import android.provider.SyncStateContract
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.databinding.ActivityRestaurantBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.math.abs

class RestaurantActivity : AppCompatActivity() {

    lateinit var binding: ActivityRestaurantBinding
    private val viewModel by viewModel<RestaurantMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        viewModel.initExtras(intent.extras?.getParcelable(Constants.ARG_RESTAURANT))
        initUi()
        initObservers()
    }

    private fun initUi() {

    }

    private fun initObservers() {
        viewModel.fragmentNavigationEvent.observe(this, { navigationEvent ->
            handleFragmentNavigationEvent(navigationEvent)
        })
    }

    private fun handleFragmentNavigationEvent(navigationEvent: LiveEvent<NavDirections>?) {
        MTLogger.c(body = "handleFragmentNavigationEvent called")
        navigationEvent?.getContentIfNotHandled()?.let {
            try {
                findNavController(R.id.restaurantActContainer).navigate(it)
            } catch (ex: Exception) {
                MTLogger.d(body = "NavigationEvent Error ${ex.message}")
            }
        }
    }

    companion object {
        private const val TAG = "RestaurantActivity"
    }
}