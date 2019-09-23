package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.dialogs.RateLastOrderViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentConfiguration
import com.stripe.android.StripeError
import com.stripe.android.model.PaymentMethod
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MyProfileViewModel(val api: ApiService, val appSettings: AppSettings, val eaterDataManager: EaterDataManager, val metaDataManager: MetaDataManager) :
    ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {


    val TAG = "wowMyProfileVM"
    data class GetUserDetails(val isSuccess: Boolean, val eater: Eater? = null)
    val getUserDetails: SingleLiveEvent<GetUserDetails> = SingleLiveEvent()

    fun getUserDetails() {
        api.getMeCall().enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    val eater = response.body()?.data
                    if (eater != null) {
                        getUserDetails.postValue(GetUserDetails(true, eater))
                    } else {
                        getUserDetails.postValue(GetUserDetails(false))
                    }
                } else {
                    getUserDetails.postValue(GetUserDetails(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                getUserDetails.postValue(GetUserDetails(false))
            }

        })
    }

    fun getDeliveryAddress(): String {
        val streetLine1 = eaterDataManager.getLastChosenAddress()?.streetLine1
        return if (streetLine1.isNullOrEmpty()) {
            ""
        } else {
            streetLine1
        }
    }

    fun logout(context: Context) {
        appSettings.logout(context)
    }

    fun initStripe(activity: Activity) {
        PaymentConfiguration.init(metaDataManager.getStripePublishableKey())
        CustomerSession.initCustomerSession(activity, EphemeralKeyProvider(this), false)
    }

    val getStripeCustomerCards: SingleLiveEvent<StripeCustomerCardsEvent> = SingleLiveEvent()
    data class StripeCustomerCardsEvent(val isSuccess: Boolean, val paymentMethods: List<PaymentMethod>? = null)
    fun getStripeCustomerCards(){
        CustomerSession.getInstance().getPaymentMethods(PaymentMethod.Type.Card,
            object : CustomerSession.PaymentMethodsRetrievalListener {
                override fun onPaymentMethodsRetrieved(@NonNull paymentMethods: List<PaymentMethod>) {
                    Log.d("wowCheckoutVM","getStripeCustomerCards $paymentMethods")
                    getStripeCustomerCards.postValue(StripeCustomerCardsEvent(true, paymentMethods))
                }

                override fun onError(errorCode: Int, @NonNull errorMessage: String, @Nullable stripeError: StripeError?) {
                    Log.d("wowCheckoutVM","getStripeCustomerCards ERROR $errorMessage")
                    getStripeCustomerCards.postValue(StripeCustomerCardsEvent(false))
                }
            })
    }

    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
        eaterDataManager.updateCustomerCard(paymentMethod)
    }



}
