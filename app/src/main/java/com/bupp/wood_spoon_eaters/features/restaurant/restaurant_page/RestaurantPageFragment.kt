package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page) {

    private val binding: FragmentRestaurantPageBinding by viewBinding()
    private val viewModel by viewModel<RestaurantPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initUi() {

    }

    private fun initObservers() {

    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }
}