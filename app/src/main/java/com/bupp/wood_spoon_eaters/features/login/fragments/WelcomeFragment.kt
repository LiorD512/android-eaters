package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.databinding.FragmentWelcomeBinding
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class   WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private var binding: FragmentWelcomeBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWelcomeBinding.bind(view)
        binding!!.welcomeFragmentLogin.setOnClickListener { onLoginClick() }

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_ON_BOARDING)

    }

    private fun onLoginClick() {
        viewModel.directToPhoneFrag()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}