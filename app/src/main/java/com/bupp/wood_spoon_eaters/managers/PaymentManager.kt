package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentConfiguration
import com.stripe.android.StripeError
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentManager(val metaDataRepository: MetaDataRepository) : EphemeralKeyProvider.EphemeralKeyProviderListener {

    private var hasStripeInitialized: Boolean = false

    suspend fun initPaymentManager(context: Context) {
        withContext(Dispatchers.IO){
            initStripe(context)
        }
    }

    private fun initStripe(context: Context) {
        val key = metaDataRepository.getStripePublishableKey()
        Log.d("wowPaymentManager", "initStripe key: $key")
        key?.let {
            PaymentConfiguration.init(context, key)
            CustomerSession.initCustomerSession(context, EphemeralKeyProvider(this), false)
            hasStripeInitialized = true
            getStripeCustomerCards(context)
        }
    }

    override fun onEphemeralKeyProviderError() {
        super.onEphemeralKeyProviderError()
        Log.d("wowPaymentManager", "initStripe failed")
        hasStripeInitialized = false
    }


    val payments = MutableLiveData<List<PaymentMethod>>()
    fun getPaymentsLiveData() = payments

    fun getStripeCustomerCards(context: Context, forceRefresh: Boolean = false): SingleLiveEvent<List<PaymentMethod>> {
        val paymentsLiveData = SingleLiveEvent<List<PaymentMethod>>()
        if (hasStripeInitialized) {
            if (this.payments.value != null && !forceRefresh) {
                paymentsLiveData.postValue(payments.value)
            } else {
                CustomerSession.getInstance().getPaymentMethods(
                    PaymentMethod.Type.Card,
                    object : CustomerSession.PaymentMethodsRetrievalListener {
                        override fun onPaymentMethodsRetrieved(@NonNull paymentMethods: List<PaymentMethod>) {
                            Log.d("wowPaymentManager", "getStripeCustomerCards $paymentMethods")
                            payments.value = paymentMethods
                            paymentsLiveData.postValue(paymentMethods)
                        }

                        override fun onError(errorCode: Int, @NonNull errorMessage: String, @Nullable stripeError: StripeError?) {
                            Log.d("wowPaymentManager", "getStripeCustomerCards ERROR $errorMessage")
                        }
                    })
            }
        } else {
            initStripe(context)
        }
        return paymentsLiveData
    }

    fun getStripeCurrentPaymentMethod(): PaymentMethod? {
        return if (payments.value != null && payments.value!!.isNotEmpty()) {
            payments.value!![0]
        } else {
            null
        }
    }

}