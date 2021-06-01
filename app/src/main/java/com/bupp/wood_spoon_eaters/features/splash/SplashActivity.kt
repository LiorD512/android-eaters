package com.bupp.wood_spoon_eaters.features.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.dialogs.update_required.UpdateRequiredDialog
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.ActivityNewOrderBinding
import com.bupp.wood_spoon_eaters.databinding.ActivitySplashBinding
import com.bupp.wood_spoon_eaters.utils.updateScreenUi
import org.koin.androidx.viewmodel.ext.android.viewModel
import io.branch.referral.Branch
import com.google.firebase.analytics.FirebaseAnalytics


class SplashActivity : AppCompatActivity(), UpdateRequiredDialog.UpdateRequiredDialogListener, WSErrorDialog.WSErrorListener {

    private lateinit var binding: ActivitySplashBinding
    private var cookId: String? = null
    private var menuItemId: String? = null
    val viewModel: SplashViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_splash)

        FirebaseAnalytics.getInstance(this)

        init()
        initObservers()
    }

    private fun init() {
        viewModel.initAppSplashData(this)
    }

    private fun initObservers() {
        viewModel.splashEvent.observe(this, Observer { splashEvent ->
            val event = splashEvent.getContentIfNotHandled()
            event?.let{
                when(it){
                    SplashViewModel.SplashEventType.SHOULD_UPDATE_VERSION -> {
                        openVersionUpdateDialog()
                    }
                    SplashViewModel.SplashEventType.GO_TO_WELCOME -> {
                        redirectToLogin(Constants.LOGIN_STATE_WELCOME)
                    }
                    SplashViewModel.SplashEventType.GO_TO_PHONE_VERIFICATION -> {
                        redirectToLogin(Constants.LOGIN_STATE_VERIFICATION)
                    }
                    SplashViewModel.SplashEventType.GO_TO_CREATE_ACCOUNT -> {
                        redirectToLogin(Constants.LOGIN_STATE_CREATE_ACCOUNT)
                    }
                    SplashViewModel.SplashEventType.GOT_TO_MAIN -> {
                        redirectToMain()
                    }
                }
            }
            
        })

        viewModel.errorEvent.observe(this, Observer{
            if(it){
                WSErrorDialog("Server error, please try again later", this).show(supportFragmentManager, Constants.WS_ERROR_DIALOG)
            }
        })
    }

    private fun redirectToLogin(loginScreenState: Int) {
        startActivity(Intent(this, LoginActivity::class.java).putExtra(Constants.LOGIN_STATE, loginScreenState))
        finish()
    }

    override fun onWSErrorDone() {
        finishAffinity()
    }

    override fun onUpdateApp(redirectUrl: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl)))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(redirectUrl)
                )
            )
        }
    }

    private fun openVersionUpdateDialog() {
        UpdateRequiredDialog().show(supportFragmentManager, Constants.UPDATE_REQUIRED_DIALOG)
    }

    private fun redirectToMain() {
        Log.d("wowSplash", "redirectToMain")
        viewModel.initFCMAndRefreshToken()
        val intent = Intent(this, MainActivity::class.java)
        cookId?.let {
            intent.putExtra("cook_id", it.toLong())
        }
        menuItemId?.let {
            intent.putExtra("menu_item_id", it.toLong())
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback(callback).withData(if (intent != null) intent.data else null).init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(callback).reInit()
    }

    private val callback = Branch.BranchReferralInitListener { linkProperties, _ ->
        linkProperties?.let {
            Log.d("wowSplash", "Branch.io intent $linkProperties")
            if (it.has("cook_id")) {
                cookId = it.get("cook_id") as String
            }
            if (it.has("menu_item_id")) {
                menuItemId = it.get("menu_item_id") as String
            }
            if(it.has("referal")){//token
                val token = it.get("token") as String
//                val cid = it.get("cid") as String

                Log.d("wowSplash", "token: $token")
                viewModel.setUserCampaignParam(token)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateScreenUi()
    }

}
