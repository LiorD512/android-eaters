package com.bupp.wood_spoon_eaters.features.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    private val viewModel: LoginViewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginActHeaderView.setHeaderViewListener(this)

        initUi()
        initObservers()
    }

    private fun initUi() {
        intent?.let{
            val status = it.getIntExtra(Constants.LOGIN_STATE, Constants.LOGIN_STATE_WELCOME)
            when(status){
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
        viewModel.navigationEvent.observe(this, Observer {
            it?.let{
                when(it.type){
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
                }
            }
        })

        viewModel.errorEvents.observe(this, Observer{
            when(it){
                LoginViewModel.ErrorEventType.INVALID_PHONE -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_phone), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                LoginViewModel.ErrorEventType.WRONG_PASSWORD -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_code), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                LoginViewModel.ErrorEventType.SERVER_ERROR -> {
                    WSErrorDialog(getString(R.string.default_server_error), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                LoginViewModel.ErrorEventType.SOMETHING_WENT_WRONG -> {
                    WSErrorDialog(getString(R.string.something_went_wrong_error), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
            }
        })
        viewModel.progressData.observe(this, Observer{
            handlePb(it)
        })
    }

    private fun redirectToCreateAccountFromWelcome() {
        setHeaderView(getString(R.string.create_account_fragment_title))
        setTitleVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_welcomeFragment_to_createAccountFragment)
    }

    private fun redirectToCreateAccountFromVerification() {
        setHeaderView(getString(R.string.create_account_fragment_title))
        setTitleVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_codeFragment_to_createAccountFragment)
    }

    private fun redirectToPhoneVerification() {
        setHeaderView(getString(R.string.phone_verification_fragment_title))
        setTitleVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_welcomeFragment_to_phoneVerificationFragment)
    }

    private fun redirectToCodeVerification() {
        Utils.hideKeyBoard(this)
        setHeaderView(getString(R.string.code_fragment_title))
        setTitleVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_phoneVerificationFragment_to_codeFragment)
    }

    fun setTitleVisibility(visibility: Int) {
        loginActHeaderView.visibility = visibility
    }

    private fun setHeaderView(title: String) {
        loginActHeaderView.setType(Constants.HEADER_VIEW_TYPE_SIGNUP, title)
        loginActHeaderView.isSkipable(false)
    }

    private fun handlePb(shouldShow: Boolean) {
        if (shouldShow) {
            loginActPb.show()
        } else {
            loginActPb.hide()
        }
    }

    override fun onHeaderBackClick() {
        onBackPressed()
    }


}