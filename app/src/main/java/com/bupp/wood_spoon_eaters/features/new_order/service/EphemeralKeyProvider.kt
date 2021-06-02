package com.bupp.wood_spoon_eaters.features.new_order.service

import android.util.Log
import androidx.annotation.Size
import com.bupp.wood_spoon_eaters.network.ApiService
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

import java.io.IOException

class EphemeralKeyProvider(val listener: EphemeralKeyProviderListener) : EphemeralKeyProvider, KoinComponent {

    val api: ApiService by inject()

    interface EphemeralKeyProviderListener{
        fun onEphemeralKeyProviderError(){}
        fun onEphemeralKeyProviderSuccess(){}
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun createEphemeralKey(@Size(min = 4) apiVersion: String, keyUpdateListener: EphemeralKeyUpdateListener) {
        compositeDisposable.add(
            api.getEphemeralKey()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {responseBody ->
                    try {
                        val jsObject = JSONObject(responseBody.string())
                        val ephemeralKeyJson = jsObject.get("data").toString()
                        keyUpdateListener.onKeyUpdate(ephemeralKeyJson)
                        Log.d("wowEphemeralKeyProvider","success")
                        listener.onEphemeralKeyProviderSuccess()
                    } catch (e: IOException) {
                        keyUpdateListener.onKeyUpdateFailure(0, e.message ?: "")
                    }}, {e -> showError(e)}
                )
        )
    }

    private fun showError(e: Throwable?) {
        Log.d("wowEphemeralKeyProvider","error ${e?.message}")
        listener.onEphemeralKeyProviderError()
    }
}


