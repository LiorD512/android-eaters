package com.bupp.wood_spoon_eaters.dialogs.payment_methods

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.dialogs.PaymentMethodAcceptedDialog
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class PaymentMethodsViewModel(val context: Context) : ViewModel() {

    val addCard: SingleLiveEvent<AddCardEvent> = SingleLiveEvent()
    data class AddCardEvent(val isSuccess: Boolean = false)

    fun tokenizeCard(ephemeralKey:String, card: Card) {
        val stripe = Stripe(context, ephemeralKey)
        stripe.createToken(card, object : ApiResultCallback<Token> {
            override fun onSuccess(token: Token) {
                addCard.postValue(AddCardEvent(true))
            }

            override fun onError(e: Exception) {
                Log.d("wowPaymentMethodVm","wrror on save card !")
                addCard.postValue(AddCardEvent(false))
            }
        }
        )
    }


}