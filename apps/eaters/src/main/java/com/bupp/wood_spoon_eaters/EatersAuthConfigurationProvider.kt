package com.bupp.wood_spoon_eaters

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import com.eatwoodspoon.auth.WoodSpoonAuthConfiguration
import com.eatwoodspoon.auth.WoodSpoonAuthConfigurationProvider

class EatersAuthConfigurationProvider(context: Context) :
    WoodSpoonAuthConfigurationProvider {
    override val configuration: WoodSpoonAuthConfiguration =
        WoodSpoonAuthConfiguration(
            appSchema = BuildConfig.DEEPLINK_SCHEMA,
            authDomain = BuildConfig.AUTH_DOMAIN,
            mainColor = context.resources.getColor(R.color.teal_blue, context.theme),
            defaultSuccessIntent = splashActivityPendingIntent(context),
        )
}

private fun splashActivityPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, SplashActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return PendingIntent.getActivity(context, 1, intent, 0)
}
