package com.bupp.wood_spoon_chef.presentation.features.splash.fragments

import android.os.Bundle
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.analytics.event.onboarding.OnboardingClickOnGetStartedEvent
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.databinding.FragmentWelcomeBinding
import com.bupp.wood_spoon_chef.presentation.features.splash.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WelcomeFragment : BaseFragment(R.layout.fragment_welcome) {

    var binding : FragmentWelcomeBinding? = null
    private val viewModel: SplashViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWelcomeBinding.bind(view)


        binding?.welcomeFragmentLogin?.setOnClickListener { onLoginClick() }
    }

    private fun onLoginClick() {
        viewModel.onWelcomeClick()
        viewModel.trackAnalyticsEvent(OnboardingClickOnGetStartedEvent())
    }

    override fun clearClassVariables() {
        binding = null
    }

}