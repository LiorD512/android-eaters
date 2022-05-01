package com.bupp.wood_spoon_chef.presentation.features.onboarding.login

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.ActivityLoginBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseActivity
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountActivity
import com.bupp.wood_spoon_chef.utils.Utils
import com.bupp.wood_spoon_chef.utils.extensions.clearStack
import com.google.android.libraries.places.api.Places
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initObservers()
    }

    private fun initUi() {
        initializePlaces()
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, {
            it?.let {
                when (it) {
                    LoginViewModel.NavigationEventType.OPEN_CODE_SCREEN -> {
                        redirectToCodeVerification()
                    }
                    LoginViewModel.NavigationEventType.START_CREATE_ACCOUNT_ACTIVITY -> {
                        redirectToCreateAccount()
                    }
                    LoginViewModel.NavigationEventType.CODE_RESENT -> {
//                        Toast.makeText(this, "Code sent!", Toast.LENGTH_SHORT).show()
                    }
                    LoginViewModel.NavigationEventType.OPEN_MAIN_ACT -> {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.clearStack()
                        startActivity(intent)
                    }
                    else -> {}
                }
            }
        })

        viewModel.errorEvent.observe(this, {
            handleErrorEvent(it, binding.root)
        })

        viewModel.progressData.observe(this, {
            handleProgressBar(it)
        })
    }

    private fun initializePlaces() {
        Places.initialize(this, BuildConfig.GOOGLE_API_KEY)
    }

    private fun redirectToCreateAccount() {
        startActivity(Intent(this, CreateAccountActivity::class.java))
        finish()
    }

    private fun redirectToCodeVerification() {
        Utils.hideKeyBoard(this)
        findNavController(R.id.loginActContainer).navigate(R.id.action_phoneVerificationFragment_to_codeFragment)
    }


    companion object {
        const val TAG = "wowLoginAct"
    }


}