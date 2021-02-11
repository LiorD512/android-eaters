package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentWelcomeBinding
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class   WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private var binding: FragmentWelcomeBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWelcomeBinding.bind(view)
        binding!!.welcomeFragmentLogin.setOnClickListener { onLoginClick() }
    }

    private fun onLoginClick() {
        viewModel.directToPhoneFrag()
    }

    override fun onResume() {
        super.onResume()
        (activity as LoginActivity).setTitleVisibility(View.GONE)
        Log.d("wowWelcomeFrag","onResume")
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }


}