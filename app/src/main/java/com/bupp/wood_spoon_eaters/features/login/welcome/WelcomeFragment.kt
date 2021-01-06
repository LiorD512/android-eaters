package com.bupp.wood_spoon_eaters.features.login.welcome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomeFragmentLogin.setOnClickListener { onLoginClick() }
        welcomeFragmentSignup.setOnClickListener { onLoginClick() }
    }

    private fun onLoginClick() {
        welcomeFragSliderImages.stopAnimation()
        viewModel.directToPhoneFrag()
    }

    override fun onPause() {
        welcomeFragSliderImages.stopAnimation()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        (activity as LoginActivity).setTitleVisibility(View.GONE)
        Log.d("wowWelcomeFrag","onResume")
    }


}