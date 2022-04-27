package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepoState
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.getStripePublishableKey
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentConfiguration
import com.stripe.android.StripeError
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentManager(val appSettingsRepository: AppSettingsRepository, private val sharedPreferences: SharedPreferences) :
    EphemeralKeyProvider.EphemeralKeyProviderListener {

    var lastSelectedCardIdRes: String?
        get() = sharedPreferences.getString(LAST_SELECTED_CARD_ID, null)
        set(selectedCardId) = sharedPreferences.edit().putString(LAST_SELECTED_CARD_ID, selectedCardId).apply()

    var hasStripeInitialized: Boolean = false

    fun getStripeInitializationEvent() = stripeInitializationEvent
    private val stripeInitializationEvent = MutableLiveData<StripeInitializationStatus>()

    enum class StripeInitializationStatus {
        START,
        SUCCESS,
        FAIL
    }

    suspend fun initPaymentManagerWithListener(context: Context) {
        //this method is called when user tries to open any stripe activity while stripe isn't initialized
        //this method fetch metaData if needed and init stripe with LiveData listener.
        withContext(Dispatchers.IO) {
            stripeInitializationEvent.postValue(StripeInitializationStatus.START)
            val key = appSettingsRepository.getStripePublishableKey()
            if (key.isNullOrEmpty()) {
                appSettingsRepository.initAppSettings()
                when (appSettingsRepository.state.value) {
                    is AppSettingsRepoState.Success -> {
                        initStripe(context)
                        stripeInitializationEvent.postValue(StripeInitializationStatus.SUCCESS)
                    }
                    else -> {
                        stripeInitializationEvent.postValue(StripeInitializationStatus.FAIL)
                    }
                }
            } else {
                initStripe(context)
            }
        }
    }

    suspend fun initPaymentManager(context: Context) {
        withContext(Dispatchers.IO) {
            initStripe(context)
        }
    }

    private fun initStripe(context: Context) {
        val key = appSettingsRepository.getStripePublishableKey()
        MTLogger.c(TAG, "initStripe key: $key")
        key?.let {
            PaymentConfiguration.init(context, key)
            CustomerSession.initCustomerSession(context, EphemeralKeyProvider(this), false)
            hasStripeInitialized = true
            getStripeCustomerCards(context)
        }
    }

    override fun onEphemeralKeyProviderError() {
        super.onEphemeralKeyProviderError()
        MTLogger.c(TAG, "initStripe failed")
        hasStripeInitialized = false
        stripeInitializationEvent.postValue(StripeInitializationStatus.FAIL)
    }

    val payments = MutableLiveData<PaymentMethod?>()
    fun getPaymentsLiveData() = payments

    private fun getStripeCustomerCards(context: Context) {
        if (hasStripeInitialized) {
            CustomerSession.getInstance().getPaymentMethods(
                PaymentMethod.Type.Card,
                object : CustomerSession.PaymentMethodsRetrievalListener {
                    override fun onPaymentMethodsRetrieved(@NonNull paymentMethods: List<PaymentMethod>) {
                        MTLogger.c(TAG, "getStripeCustomerCards")
//                            MTLogger.c(TAG, "getStripeCustomerCards $paymentMethods")
                        if (lastSelectedCardIdRes != null) {
                            val lastSelectedCard = paymentMethods.find { it.card?.last4 == lastSelectedCardIdRes }
                            if (lastSelectedCard != null) {
                                payments.value = lastSelectedCard
                            } else {
                                getFirstPaymentOrNull(paymentMethods)
                            }
                        } else {
                            getFirstPaymentOrNull(paymentMethods)
                        }
                    }

                    override fun onError(errorCode: Int, @NonNull errorMessage: String, @Nullable stripeError: StripeError?) {
                        MTLogger.c(TAG, "getStripeCustomerCards ERROR $errorMessage")
                    }
                })
        } else {
            initStripe(context)
        }
    }

    private fun getFirstPaymentOrNull(paymentMethods: List<PaymentMethod>) {
        if (paymentMethods.isNotEmpty()) {
            payments.value = paymentMethods[0]
        } else {
            payments.value = null
        }
    }

    fun getStripeCurrentPaymentMethod(): PaymentMethod? {
        return if (payments.value != null) {
            payments.value!!
        } else {
            null
        }
    }

    fun updateSelectedPaymentMethod(context: Context, paymentMethod: PaymentMethod?) {
        if (paymentMethod == null) {
            getStripeCustomerCards(context)
        } else {
            payments.value = paymentMethod
            lastSelectedCardIdRes = paymentMethod.card?.last4
        }
    }

    fun clearPaymentMethods() {
        payments.postValue(null)
    }

    companion object {
        const val TAG = "wowPaymentManager"
        const val LAST_SELECTED_CARD_ID = "lastSelectedCard"
    }

}