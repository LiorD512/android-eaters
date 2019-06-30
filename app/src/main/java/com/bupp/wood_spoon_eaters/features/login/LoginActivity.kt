package com.bupp.wood_spoon_eaters.features.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.code.CodeFragment
import com.bupp.wood_spoon_eaters.features.login.verification.PhoneVerificationFragment
import com.bupp.wood_spoon_eaters.features.login.welcome.WelcomeFragment
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.sign_up.SignUpActivity
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadWelcomeFragment()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.loginContainer, fragment)
            .commit()
    }

    fun loadWelcomeFragment() {
        setTitleVisibility(View.GONE)
        loadFragment(WelcomeFragment())
    }

    fun loadCodeFragment(phoneNumber: String) {
        setTitleText(getString(R.string.code_fragment_title))
        setTitleVisibility(View.VISIBLE)
        loadFragment(CodeFragment(phoneNumber))
    }

    fun loadPhoneVerificationFragment() {
        setTitleText(getString(R.string.phone_verification_fragment_title))
        setTitleVisibility(View.VISIBLE)
        loadFragment(PhoneVerificationFragment())
    }

    private fun loadSignUpActivity() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun setTitleVisibility(visibility: Int) {
        loginTitle.visibility = visibility
    }

    private fun setTitleText(title: String) {
        loginTitle.setType(Constants.HEADER_VIEW_TYPE_TITLE, title)
    }

    fun loadMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun handlePb(shouldShow: Boolean) {
        if (shouldShow) {
            loginPb.show()
        } else {
            loginPb.hide()
        }
    }

    fun onCodeSuccess() {
        handlePb(false)
        loadSignUpActivity()
        //TODO == need to check how to display welcome fragment when we go back from signUpActivity
        //TODO == maybe override onBackPress?    (eyal)
        //loadWelcomeFragment()

//        if(type == Constants.BUSINESS_CODE){
//            loadPhoneVerificationFragment()
//        }else if(type == Constants.VERIFY_CODE){
//            loadMain()
//        }
    }


}