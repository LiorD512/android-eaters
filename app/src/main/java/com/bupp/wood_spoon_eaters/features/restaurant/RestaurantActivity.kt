package com.bupp.wood_spoon_eaters.features.restaurant;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.ActivityRestaurantBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestaurantActivity : AppCompatActivity() {

    lateinit var binding: ActivityRestaurantBinding
    private val viewModel by viewModel<RestaurantMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        initUi()
        initObservers()
    }

    private fun initUi() {

    }

    private fun initObservers() {

    }

    companion object {
        private const val TAG = "RestaurantActivity"
    }
}