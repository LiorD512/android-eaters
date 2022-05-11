package com.bupp.wood_spoon_chef.presentation.features.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.databinding.ActivitySplashBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.NoNetworkDialog
import com.bupp.wood_spoon_chef.presentation.dialogs.WSErrorDialog
import com.bupp.wood_spoon_chef.presentation.dialogs.update_required.UpdateRequiredDialog
import com.bupp.wood_spoon_chef.presentation.features.base.BaseActivity
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountActivity
import com.bupp.wood_spoon_chef.presentation.features.onboarding.login.LoginActivity
import com.bupp.wood_spoon_chef.utils.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import io.branch.referral.Branch
import io.branch.referral.Branch.BranchReferralInitListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity(), UpdateRequiredDialog.UpdateRequiredDialogListener,
    WSErrorDialog.WSErrorListener, NoNetworkDialog.NoNetworkListener {


    val viewModel: SplashViewModel by viewModel()
    var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        startApp()
    }

    private fun startApp() {
        if (Utils.isNetworkAvailable(this)) {
            FirebaseAnalytics.getInstance(this)

            viewModel.initAppSplashData()

            initObservers()
        } else {
            NoNetworkDialog(this).show(supportFragmentManager, Constants.NO_NETWORK_DIALOG)
        }
    }

    override fun onRetryConnectionClick() {
        startApp()
    }

    private fun initObservers() {
        viewModel.splashEvent.observe(this) { splashEvent ->
            val event = splashEvent.getContentIfNotHandled()
            event?.let {
                when (it) {
                    SplashViewModel.SplashEventType.SHOULD_UPDATE_VERSION -> {
                        openVersionUpdateDialog()
                    }
                    SplashViewModel.SplashEventType.GO_TO_WELCOME -> {
                        redirectToWelcomeScreen()
                    }
                    SplashViewModel.SplashEventType.GOT_TO_MAIN -> {
                        redirectToMain()
                    }
                    SplashViewModel.SplashEventType.START_LOGIN_ACTIVITY -> {
                        redirectToLogin()
                    }
                    SplashViewModel.SplashEventType.START_CREATE_ACCOUNT_ACTIVITY -> {
                        redirectToCreateAccount()
                    }
                }
            }

        }

        viewModel.errorEvent.observe(this) {
            handleErrorEvent(it, binding?.root)
        }
    }



    private fun redirectToWelcomeScreen() {
        try {
            findNavController(R.id.splashActContainer).navigate(R.id.action_initFragment_to_welcomeFragment)
        } catch (ex: Exception) {
            MTLogger.e("navError, ${ex.localizedMessage}")
        }
    }

    private fun redirectToLogin() {
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )
    }

    private fun redirectToCreateAccount() {
        startActivity(
            Intent(
                this,
                CreateAccountActivity::class.java
            )
        )
    }

    override fun onWSErrorDone() {
        finishAffinity()
    }

    override fun onUpdateApp(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (ex: android.content.ActivityNotFoundException) {
            WSErrorDialog("Ops! Couldn't open GooglePlay", this).show(
                supportFragmentManager,
                Constants.WS_ERROR_DIALOG
            )
        }
    }

    private fun openVersionUpdateDialog() {
        UpdateRequiredDialog().show(supportFragmentManager, Constants.UPDATE_REQUIRED_DIALOG)
    }

    private fun redirectToMain() {
        Log.d("wowSplash", "redirectToMain")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener)
            .withData(if (intent != null) intent.data else null).init()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit()
    }

    private val branchReferralInitListener: BranchReferralInitListener =
        BranchReferralInitListener { _, _ ->
            // do stuff with deep link data (nav to page, display content, etc)
        }

}
