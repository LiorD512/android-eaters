//package com.bupp.wood_spoon_eaters.dialogs.payment_methods
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
//import com.stripe.android.Stripe
//import com.stripe.android.model.Card
//import com.stripe.android.model.SourceParams
//import java.lang.Exception
//
//class PaymentMethodsViewModel(val context: Context) : ViewModel() {
//
//    val addCard: SingleLiveEvent<AddCardEvent> = SingleLiveEvent()
//    data class AddCardEvent(val isSuccess: Boolean = false)
//
//    fun tokenizeCard(ephemeralKey:String, card: Card) {
//        val stripe = Stripe(context, "pk_test_eCDAnCOC8dcX1AU09JbgVKU700Q5GSVt0F")
////        val cardSourceParams = SourceParams.createCardParams(card)
////        stripe.createSource(
////            cardSourceParams,
////            object : SourceCallback {
////                override fun onSuccess(source: Source) {
////                    // Store the source somewhere, use it, etc
////                }
////
////                override fun onError(error: Exception) {
////                    // Tell the user that something went wrong
////                }
////            })
////        stripe.createToken(card, object : ApiResultCallback<Token> {
////            override fun onSuccess(token: Token) {
////                Log.d("wowPaymentMethodVm","save card success!")
////                addCard.postValue(AddCardEvent(true))
////            }
////
////            override fun onError(e: Exception) {
////                Log.d("wowPaymentMethodVm","error on save card !: ${e.message}")
////                addCard.postValue(AddCardEvent(false))
////            }
////        }
////        )
//    }
//
//
//}