package com.bupp.wood_spoon_eaters.features.login

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.ActivityLoginBinding
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.utils.hideKeyboard
import com.eatwoodspoon.auth.WoodSpoonAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()
    private val woodSpoonAuth: WoodSpoonAuth by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initObservers()
    }

    private fun initUi() {
        intent?.let {
            val status = it.getIntExtra(Constants.LOGIN_STATE, Constants.LOGIN_STATE_WELCOME)
            when (status) {
                Constants.LOGIN_STATE_WELCOME -> {
                    //do nothing this is default state
                }
                Constants.LOGIN_STATE_VERIFICATION -> {
                    redirectToPhoneVerification()
                }
                Constants.LOGIN_STATE_CREATE_ACCOUNT -> {
                    redirectToCreateAccountFromWelcome()
                }
            }
        }

    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    LoginViewModel.NavigationEventType.OPEN_PHONE_SCREEN -> {
                        redirectToPhoneVerification()
                    }
                    LoginViewModel.NavigationEventType.OPEN_CODE_SCREEN -> {
                        redirectToCodeVerification()
                    }
                    LoginViewModel.NavigationEventType.OPEN_SIGNUP_SCREEN -> {
                        redirectToCreateAccountFromVerification()
                    }
                    LoginViewModel.NavigationEventType.CODE_RESENT -> {
                        Toast.makeText(this, "Code sent!", Toast.LENGTH_SHORT).show()
                    }
                    LoginViewModel.NavigationEventType.OPEN_MAIN_ACT -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    LoginViewModel.NavigationEventType.OPEN_WEB_FLOW -> {
                        woodSpoonAuth.startWebLogin(
                            this,
                            PendingIntent.getActivity(
                                this,
                                1,
                                Intent(this, SplashActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                },
                                0
                            ),
                            "onboarding"
                        )
                        finish()
                    }
                }
            }
        }

        viewModel.errorEvents.observe(this) {
            when (it) {
                ErrorEventType.INVALID_PHONE -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_phone), null).show(
                        supportFragmentManager,
                        Constants.WS_ERROR_DIALOG
                    )
                }
                ErrorEventType.WRONG_PASSWORD -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_code), null).show(
                        supportFragmentManager,
                        Constants.WS_ERROR_DIALOG
                    )
                }
                ErrorEventType.SERVER_ERROR -> {
                    WSErrorDialog(getString(R.string.default_server_error), null).show(
                        supportFragmentManager,
                        Constants.WS_ERROR_DIALOG
                    )
                }
                ErrorEventType.SOMETHING_WENT_WRONG -> {
                    WSErrorDialog(getString(R.string.something_went_wrong_error), null).show(
                        supportFragmentManager,
                        Constants.WS_ERROR_DIALOG
                    )
                }
                else -> {
                }
            }
        }

        viewModel.progressData.observe(this) {
            handlePb(it)
        }
    }


    private fun redirectToCreateAccountFromWelcome() {
        if (findNavController(R.id.loginActContainer).currentDestination?.id == R.id.onboardingFragment) {
            findNavController(R.id.loginActContainer).navigate(R.id.action_onboardingFragment_to_createAccountFragment)
        }
    }

    private fun redirectToCreateAccountFromVerification() {
        if (findNavController(R.id.loginActContainer).currentDestination?.id == R.id.codeFragment) {
            findNavController(R.id.loginActContainer).navigate(R.id.action_codeFragment_to_createAccountFragment)
        }
    }

    private fun redirectToPhoneVerification() {
        if (findNavController(R.id.loginActContainer).currentDestination?.id == R.id.onboardingFragment) {
            findNavController(R.id.loginActContainer).navigate(R.id.action_onboardingFragment_to_phoneVerificationFragment)
        }
    }

    private fun redirectToCodeVerification() {
        if (findNavController(R.id.loginActContainer).currentDestination?.id == R.id.phoneVerificationFragment) {
            hideKeyboard()
            findNavController(R.id.loginActContainer).navigate(R.id.action_phoneVerificationFragment_to_codeFragment)
        }
    }

    private fun handlePb(shouldShow: Boolean) {
        if (shouldShow) {
            binding.loginActPb.show()
        } else {
            binding.loginActPb.hide()
        }
    }


}