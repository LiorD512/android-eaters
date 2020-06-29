package com.bupp.wood_spoon_eaters.features.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.UpdateRequiredDialog
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.sign_up.SignUpActivity
import com.bupp.wood_spoon_eaters.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule
import io.branch.referral.BranchError
import org.json.JSONObject
import io.branch.referral.Branch
import com.google.firebase.analytics.FirebaseAnalytics
import com.uxcam.UXCam
import java.lang.Exception


class SplashActivity : AppCompatActivity(), UpdateRequiredDialog.UpdateRequiredDialogListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var cookId: String? = null
    private var menuItemId: String? = null
    val viewModel: SplashViewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

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
                    if (event.shouldUpdateVersion) {
                        openVersionUpdateDialog()
                    } else if (event.isRegistered) {
                        redirectToMain()
                    } else {
                        redirectToCreateAccount()
                    }
                } else {
                    if (event.shouldUpdateVersion) {
                        openVersionUpdateDialog()
                    } else {
                        redirectToWelcome()
                    }
                }
            } else {
                redirectToWelcome()
                //server fail
            }
        })
    }

    override fun onUpdate() {
        val appPackageName = packageName // getPackageName() from Context or Activity object
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    private fun openVersionUpdateDialog() {
        UpdateRequiredDialog().show(supportFragmentManager, Constants.UPDATE_REQUIRED_DIALOG)
    }


    private fun redirectToCreateAccount() {
        Log.d("wowSplash", "redirectToCreateAccount")
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun redirectToMain() {
        Log.d("wowSplash", "redirectToMain")
        val intent = Intent(this, MainActivity::class.java)
        cookId?.let {
            intent.putExtra("cook_id", it.toLong())
        }
        menuItemId?.let {
            intent.putExtra("menu_item_id", it.toLong())
        }
        startActivity(intent)

        finish()
    }

    private fun redirectToWelcome() {
        Log.d("wowSplash", "redirectToWelcome")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun init() {
        viewModel.initServerCall() //init all data
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

    private val callback = Branch.BranchReferralInitListener { linkProperties, error ->
        linkProperties?.let {
            Log.d("wowSplash", "Branch.io intent $linkProperties")
            if (it.has("cook_id")) {
                cookId = it.get("cook_id") as String
            }
            if (it.has("menu_item_id")) {
                menuItemId = it.get("menu_item_id") as String
            }
            if(it.has("+non_branch_link")){
                val link = it.get("+non_branch_link") as String
                val sidIndex = link.indexOf("sid=")
                val cidIndex = link.indexOf("cid=")

                val sid: String? = if (sidIndex == -1) null else link.substring(sidIndex+4, cidIndex-1)
                val cid: String? = if (cidIndex == -1) null else link.substring(cidIndex+4)
                Log.d("wowSplash", "sid: $sid cid: $cid")
                viewModel.setUserCampaignParam(sid, cid)
            }
        }
    }

}
