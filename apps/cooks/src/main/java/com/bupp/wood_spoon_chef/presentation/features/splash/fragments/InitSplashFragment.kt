package com.bupp.wood_spoon_chef.presentation.features.splash.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.splash.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InitSplashFragment : Fragment(R.layout.fragment_init_splash) {

    val viewModel by sharedViewModel<SplashViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initUi() {

    }

    private fun initObservers() {

    }
}