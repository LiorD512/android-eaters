package com.bupp.wood_spoon_eaters.features.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.login.fragments.WelcomeFragmentDirections
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.utils.hideKeyboard
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
//                    redirectToMainLoginScreen(1)
                    redirectToCodeVerification()
                }
                Constants.LOGIN_STATE_CREATE_ACCOUNT -> {
                    redirectToCreateAccountFromWelcome()
//                    redirectToMainLoginScreen(2)
                }
            }
        }

    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer {
            it?.let{
                when(it){
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

        viewModel.errorEvents.observe(this, {
            when(it){
                ErrorEventType.INVALID_PHONE -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_phone), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.WRONG_PASSWORD -> {
                    WSErrorDialog(getString(R.string.login_error_wrong_code), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.SERVER_ERROR -> {
                    WSErrorDialog(getString(R.string.default_server_error), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
                ErrorEventType.SOMETHING_WENT_WRONG -> {
                    WSErrorDialog(getString(R.string.something_went_wrong_error), null).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
                }
            }
        })

        viewModel.progressData.observe(this, Observer{
            handlePb(it)
        })
    }

    private fun redirectToMainLoginScreen(startWith: Int = 0) {
//        val bundle = Bundle()
//        bundle.putInt("START_WITH", startWith)
//        val action = WelcomeFragmentDirections.actionWelcomeFragmentToLoginMainFragment()
//        action.startWithArgs = startWith
//        findNavController(R.id.loginActContainer).navigate(action)
    }

    private fun redirectToCreateAccountFromWelcome() {
//        setCloseBtnVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_welcomeFragment_to_createAccountFragment)
    }

    private fun redirectToCreateAccountFromVerification() {
//        setCloseBtnVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_codeFragment_to_createAccountFragment)
    }

    private fun redirectToPhoneVerification() {
//        setCloseBtnVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_welcomeFragment_to_phoneVerificationFragment)
    }

    private fun redirectToCodeVerification() {
        hideKeyboard()
//        setCloseBtnVisibility(View.VISIBLE)
        findNavController(R.id.loginActContainer).navigate(R.id.action_phoneVerificationFragment_to_codeFragment)
    }

//    private fun redirectToLocationPermission(isFromCodeScreen: Boolean) {
//        setHeaderView(getString(R.string.location_permission_title))
//        setTitleVisibility(View.VISIBLE)
//        if(isFromCodeScreen){
//            findNavController(R.id.loginActContainer).navigate(R.id.action_codeFragment_to_locationPermissionFragment)
//        }else{
//            findNavController(R.id.loginActContainer).navigate(R.id.action_createAccountFragment_to_locationPermissionFragment)
//        }
//    }
//
//    fun setCloseBtnVisibility(visibility: Int) {
//        loginActCloseBtn.visibility = visibility
//    }

    private fun handlePb(shouldShow: Boolean) {
        if (shouldShow) {
            loginActPb.show()
        } else {
            loginActPb.hide()
        }
    }



}