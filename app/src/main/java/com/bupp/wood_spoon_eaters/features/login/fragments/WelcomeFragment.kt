package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentWelcomeBinding
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class   WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val binding: FragmentWelcomeBinding by viewBinding()
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomeFragmentLogin.setOnClickListener { onLoginClick() }

        Analytics.with(requireContext()).screen("onboarding")
    }

    private fun onLoginClick() {
        viewModel.directToPhoneFrag()
    }


}