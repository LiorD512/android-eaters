package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentConfiguration
import com.stripe.android.StripeError
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentManager(val metaDataRepository: MetaDataRepository, private val sharedPreferences: SharedPreferences) : EphemeralKeyProvider.EphemeralKeyProviderListener {

    var lastSelectedCardIdRes: String?
        get() = sharedPreferences.getString(LAST_SELECTED_CARD_ID, null)
        set(selectedCardId) = sharedPreferences.edit().putString(LAST_SELECTED_CARD_ID, selectedCardId).apply()

    var hasStripeInitialized: Boolean = false

    fun getStripeInitializationEvent() = stripeInitializationEvent
    private val stripeInitializationEvent = MutableLiveData<StripeInitializationStatus>()
    enum class StripeInitializationStatus{
        START,
        SUCCESS,
        FAIL
    }

    suspend fun initPaymentManagerWithListener(context: Context) {
        //this method is called when user tries to open any stripe activity while stripe isn't initialized
        //this method fetch metaData if needed and init stripe with LiveData listener.
        withContext(Dispatchers.IO){
            stripeInitializationEvent.postValue(StripeInitializationStatus.START)
            val key = metaDataRepository.getStripePublishableKey()
            if(key.isNullOrEmpty()){
                val result = metaDataRepository.initMetaData()
                when(result.status){
                    MetaDataRepository.MetaDataRepoStatus.SUCCESS -> {
                        initStripe(context)
                        stripeInitializationEvent.postValue(StripeInitializationStatus.SUCCESS)
                    }
                    MetaDataRepository.MetaDataRepoStatus.FAILED -> {
                        stripeInitializationEvent.postValue(StripeInitializationStatus.FAIL)
                    }
                    else -> {
                        stripeInitializationEvent.postValue(StripeInitializationStatus.FAIL)
                    }

                }
            }else{
                stripeInitializationEvent.postValue(StripeInitializationStatus.FAIL)
            }
        }
    }
    suspend fun initPaymentManager(context: Context) {
        withContext(Dispatchers.IO){
            initStripe(context)
        }
    }

    private fun initStripe(context: Context) {
        val key = metaDataRepository.getStripePublishableKey()
        Log.d(TAG, "initStripe key: $key")
        key?.let {
            PaymentConfiguration.init(context, key)
            CustomerSession.initCustomerSession(context, EphemeralKeyProvider(this), false)
            hasStripeInitialized = true
            getStripeCustomerCards(context)

        }
    }

    override fun onEphemeralKeyProviderError() {
        super.onEphemeralKeyProviderError()
        Log.d(TAG, "initStripe failed")
        hasStripeInitialized = false
        stripeInitializationEvent.postValue(StripeInitializationStatus.FAIL)
    }

    override fun onEphemeralKeyProviderSuccess() {
        super.onEphemeralKeyProviderSuccess()
//        Log.d(TAG, "onEphemeralKeyProviderSuccess")
//        stripeInitializationEvent.postValue(StripeInitializationStatus.SUCCESS)
    }

    val payments = MutableLiveData<PaymentMethod>()
    fun getPaymentsLiveData() = payments

    fun getStripeCustomerCards(context: Context, forceRefresh: Boolean = false){
//        val paymentsLiveData = SingleLiveEvent<List<PaymentMethod>>()
        if (hasStripeInitialized) {
//            if (this.payments.value != null && !forceRefresh) {
//                paymentsLiveData.postValue(payments.value)
//            } else {
                CustomerSession.getInstance().getPaymentMethods(
                    PaymentMethod.Type.Card,
                    object : CustomerSession.PaymentMethodsRetrievalListener {
                        override fun onPaymentMethodsRetrieved(@NonNull paymentMethods: List<PaymentMethod>) {
                            Log.d(TAG, "getStripeCustomerCards $paymentMethods")
                            if(lastSelectedCardIdRes != null){
                                val lastSelectedCard = paymentMethods.find { it.card?.last4 == lastSelectedCardIdRes }
                                lastSelectedCard?.let{
                                    payments.value = it
                                }
                            }else{
                                paymentMethods.isNotEmpty().let {
                                    payments.value = paymentMethods[0]
                                }
                            }
                        }

                        override fun onError(errorCode: Int, @NonNull errorMessage: String, @Nullable stripeError: StripeError?) {
                            Log.d(TAG, "getStripeCustomerCards ERROR $errorMessage")
                        }
                    })
//            }
        } else {
            initStripe(context)
        }
//        return paymentsLiveData
    }


    fun updatePaymentsMethod(paymentMethod: PaymentMethod) {
        payments.value = listOf(paymentMethod)
    }

    fun getStripeCurrentPaymentMethod(): PaymentMethod? {
        return if (payments.value != null) {
            payments.value!!
        } else {
            null
        }
    }

    fun updateSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        payments.value = paymentMethod
        lastSelectedCardIdRes = paymentMethod.card?.last4
    }

    companion object{
        const val TAG = "wowPaymentManager"
        const val LAST_SELECTED_CARD_ID = "lastSelectedCard"
    }

}