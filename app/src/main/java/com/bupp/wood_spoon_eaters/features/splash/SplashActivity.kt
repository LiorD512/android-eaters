package com.bupp.wood_spoon_eaters.features.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.sign_up.SignUpActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule



class SplashActivity : AppCompatActivity() {

    val viewModel: SplashViewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initObservers()
        Timer("SettingUp", false).schedule(1000) {
            init()
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer { event ->
            if (event != null) {
                if (event.isSuccess) {
                    // all data received
                    if (event.isRegistered) {
                        redirectToMain()
                    } else {
                        redirectToCreateAccount()
                    }
                } else {
                    redirectToWelcome()
                }
            } else {
                redirectToWelcome()
                //server fail
            }
        })
    }


    private fun redirectToCreateAccount() {
        Log.d("wowSplash","redirectToCreateAccount")
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun redirectToMain() {
        Log.d("wowSplash","redirectToMain")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun redirectToWelcome() {
        Log.d("wowSplash","redirectToWelcome")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun init() {
        viewModel.initServerCall() //init all data
    }

}
