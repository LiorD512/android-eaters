package com.eatwoodspoon.auth

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.eatwoodspoon.analytics.AnalyticsEventReporter
import com.eatwoodspoon.analytics.events.MobileAuthEvent
import timber.log.Timber

data class WoodSpoonAuthConfiguration(
    val appSchema: String,
    val authDomain: String,
    @ColorInt val mainColor: Int,
    val defaultSuccessIntent: PendingIntent
)

interface WoodSpoonAuthConfigurationProvider {
    val configuration: WoodSpoonAuthConfiguration
}

class WoodSpoonAuth(
    configurationProvider: WoodSpoonAuthConfigurationProvider,
    private val tokenRepository: AuthTokenRepository,
    private val analyticsReporter: AnalyticsEventReporter
) {

    private val configuration = configurationProvider.configuration

    private var loginSuccessPendingIntent: PendingIntent? = null
    private var loginStartedAt: Long? = null

    private var logoutSuccessPendingIntent: PendingIntent? = null
    private var logoutStartedAt: Long? = null


    fun startWebLogin(context: Context, onSuccessPendingIntent: PendingIntent?, source: String) {

        loginSuccessPendingIntent = onSuccessPendingIntent
        loginStartedAt = System.currentTimeMillis()

        val redirectUrl = "${configuration.appSchema}://login"
        try {
            val authUri =
                Uri.parse("https://${configuration.authDomain}/auth?redirectToPath=success/mobile&redirectToUrl=${redirectUrl}")
            startBrowser(context, authUri)
        } catch (ex: Exception) {
            Timber.e(ex)
            reportEvent(MobileAuthEvent.ErrorEvent(error_code = -1, error_description = ex.message))
        }
        reportEvent(MobileAuthEvent.LoginStartedEvent(source = source, has_guest_token = false))
    }

    fun startWebLogout(context: Context, onSuccessPendingIntent: PendingIntent?, source: String) {

        logoutSuccessPendingIntent = onSuccessPendingIntent
        logoutStartedAt = System.currentTimeMillis()

        tokenRepository.authorizationToken = null

        val redirectUrl = "${configuration.appSchema}://logout"

        try {
            val authUri =
                Uri.parse("https://${configuration.authDomain}/logout/mobile?redirectToUrl=${redirectUrl}")
            startBrowser(context, authUri)
        } catch (ex: Exception) {
            Timber.e(ex)
            reportEvent(MobileAuthEvent.ErrorEvent(error_code = -1, error_description = ex.message))
        }
        reportEvent(MobileAuthEvent.LogoutStartedEvent(source = source))
    }

    fun handleUrlIntent(intent: Intent, context: Context): Boolean {
        val uri = intent.takeIf { intent.action == Intent.ACTION_VIEW }?.data ?: return false

        return when (uri.authority) {
            "login" -> handleLoginUri(uri, context)
            "logout" -> handleLogoutUri(uri, context)
            else -> {
                reportEvent(
                    MobileAuthEvent.ErrorEvent(
                        error_code = -1,
                        error_description = "Unsupported host received: ${uri.authority}"
                    )
                )
                false
            }
        }
    }

    private fun handleLoginUri(uri: Uri, context: Context): Boolean {
        val token = uri.getQueryParameter("token")
        if (token.isNullOrBlank() || listOf("null", "undefined").contains(token.lowercase())) {
            reportEvent(
                MobileAuthEvent.ErrorEvent(
                    error_code = -1,
                    error_description = "No or invalid token"
                )
            )
            return false
        }
        reportEvent(MobileAuthEvent.LoginCallbackReceivedEvent(
            token_type = token.analyticsTokenType(),
            duration_seconds = loginStartedAt?.durationSince()?.also {
                loginStartedAt = null
            }
        ))

        tokenRepository.authorizationToken = token
        try {
            (loginSuccessPendingIntent ?: configuration.defaultSuccessIntent).send(context)
        } catch (ex: PendingIntent.CanceledException) {
            Timber.e(ex)
            reportEvent(MobileAuthEvent.ErrorEvent(error_code = -1, error_description = ex.message))
        }
        loginSuccessPendingIntent = null
        return true
    }

    private fun handleLogoutUri(uri: Uri, context: Context): Boolean {
        reportEvent(MobileAuthEvent.LogoutCallbackReceivedEvent(
            duration_seconds = logoutStartedAt?.durationSince()?.also {
                logoutStartedAt = null
            }
        ))
        try {
            (logoutSuccessPendingIntent ?: configuration.defaultSuccessIntent).send(context)
        } catch (ex: PendingIntent.CanceledException) {
            Timber.e(ex)
            reportEvent(MobileAuthEvent.ErrorEvent(error_code = -1, error_description = ex.message))
        }
        logoutSuccessPendingIntent = null
        return true
    }

    private fun startBrowser(context: Context, authUri: Uri) {
        try {
            val chromeTabBuilder = CustomTabsIntent.Builder()
                .setShowTitle(false)
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(configuration.mainColor).build()
                )
                .setInstantAppsEnabled(false)
                .setUrlBarHidingEnabled(true)
            val chromeTab = chromeTabBuilder.build()
//            chromeTab.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            chromeTab.launchUrl(context, authUri)
        } catch (ex: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, authUri)
            context.startActivity(intent)
            reportEvent(
                MobileAuthEvent.ErrorEvent(
                    error_code = -2,
                    error_description = "Chrome not installed"
                )
            )
        }
    }

    private fun reportEvent(event: MobileAuthEvent) {
        analyticsReporter.reportEvent(event)
    }
}

private fun String.analyticsTokenType() = if (count { it == '.' } == 2) {
    MobileAuthEvent.LoginCallbackReceivedEvent.TokenTypeValues.jwt
} else {
    MobileAuthEvent.LoginCallbackReceivedEvent.TokenTypeValues.opaque
}

private fun Long.durationSince() =
    this.minus(System.currentTimeMillis()).times(-1).toFloat().div(100)

private fun PendingIntent.send(context: Context) = send(context, 1, null)