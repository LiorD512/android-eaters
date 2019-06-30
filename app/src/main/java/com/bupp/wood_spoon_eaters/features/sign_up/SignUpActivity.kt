package com.bupp.wood_spoon_eaters.features.sign_up

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.sign_up.create_account.CreateAccountFragment
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initUi()

        loadCreateAccount()

    }

    private fun initUi() {
        headerSignUpFragment.setHeaderViewListener(this)
    }

    fun loadCreateAccount() {
        headerSignUpFragment.setType(
            Constants.HEADER_VIEW_TYPE_TITLE_SKIP,
            getString(R.string.create_account_fragment_title)
        )
        loadFragment(CreateAccountFragment(), Constants.CREATE_ACCOUNT_TAG)
    }

    fun moveToMainActivity() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.signUpContainer, fragment, tag)
            .commit()
    }

    override fun onHeaderSkipClick() {
        moveToMainActivity()
    }

    fun showPb() {
        signupPb.show()
    }

    fun hidePb() {
        signupPb.hide()
    }
}
