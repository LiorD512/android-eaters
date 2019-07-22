package com.bupp.wood_spoon_eaters.features.login.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class WelcomeFragment : Fragment() {

    private val viewModel: WelcomeViewModel by viewModel<WelcomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomeFragmentLogin.setOnClickListener { onLoginClick() }
        welcomeFragmentSignup.setOnClickListener { onLoginClick() }
    }

    private fun onLoginClick() {
        (activity as LoginActivity).loadPhoneVerificationFragment()
    }

    override fun onPause() {
        welcomeFragSliderImages.stopAnimation()
        super.onPause()
    }


}