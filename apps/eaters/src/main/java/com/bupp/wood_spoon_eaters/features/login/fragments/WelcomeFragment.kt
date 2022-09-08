package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.databinding.FragmentWelcomeBinding
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


@Deprecated("Use new OnboardingFragment.kt")
class  WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private var binding: FragmentWelcomeBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWelcomeBinding.bind(view)
        binding!!.welcomeFragmentLogin.setOnClickListener { onLoginClick() }

        viewModel.trackPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_ON_BOARDING)

    }

    private fun onLoginClick() {
        viewModel.onStartLoginClicked()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}