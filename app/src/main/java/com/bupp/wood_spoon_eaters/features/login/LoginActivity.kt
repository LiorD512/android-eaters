package com.bupp.wood_spoon_eaters.features.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.features.login.code.CodeFragment
import com.bupp.wood_spoon_eaters.features.login.verification.PhoneVerificationFragment
import com.bupp.wood_spoon_eaters.features.login.welcome.WelcomeFragment
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.sign_up.SignUpActivity
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginActHeaderView.setHeaderViewListener(this)
        loadWelcomeFragment()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(tag)
            .replace(R.id.loginActContainer, fragment)
            .commit()
    }

    fun loadWelcomeFragment() {
        setTitleVisibility(View.GONE)
        loadFragment(WelcomeFragment(), Constants.WELCOME_TAG)
    }

    fun loadCodeFragment(phoneNumber: String) {
        setHeaderView(getString(R.string.code_fragment_title))
        setTitleVisibility(View.VISIBLE)
        loadFragment(CodeFragment(phoneNumber), Constants.CODE_TAG)
    }

    fun loadPhoneVerificationFragment() {
        setHeaderView(getString(R.string.phone_verification_fragment_title))
        setTitleVisibility(View.VISIBLE)
        loadFragment(PhoneVerificationFragment(), Constants.PHONE_VERIFICATION_TAG)
    }

    private fun loadSignUpActivity() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun setTitleVisibility(visibility: Int) {
        loginActHeaderView.visibility = visibility
    }

    private fun setHeaderView(title: String) {
        loginActHeaderView.setType(Constants.HEADER_VIEW_TYPE_SIGNUP, title)
        loginActHeaderView.isSkipable(false)
    }

    fun loadMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun handlePb(shouldShow: Boolean) {
        if (shouldShow) {
            loginActPb.show()
        } else {
            loginActPb.hide()
        }
    }

    fun onCodeSuccess() {
        handlePb(false)
        loadSignUpActivity()
    }

    fun onRegisteredUser() {
        //return here after code verification and current user is after login
        handlePb(false)
        loadMain()
    }

    override fun onHeaderBackClick() {
        onBackPressed()
    }


}