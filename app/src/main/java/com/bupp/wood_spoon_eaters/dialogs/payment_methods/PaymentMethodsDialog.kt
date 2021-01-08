//package com.bupp.wood_spoon_eaters.dialogs.payment_methods
//
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.DialogFragment
//import androidx.lifecycle.Observer
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.HeaderView
//import com.bupp.wood_spoon_eaters.dialogs.PaymentMethodAcceptedDialog
//import com.bupp.wood_spoon_eaters.common.Constants
//import com.stripe.android.Stripe
//import kotlinx.android.synthetic.main.payment_methods_dialog.*
//import org.koin.androidx.viewmodel.ext.android.viewModel
//import com.stripe.android.CustomerSession
//import android.R.string
//import androidx.annotation.NonNull
//import androidx.annotation.Size
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import com.stripe.android.EphemeralKeyUpdateListener
//
//
//class PaymentMethodsDialog(val ephemeralKey: String) : DialogFragment(), HeaderView.HeaderViewListener {
//
//    val viewModel by viewModel<PaymentMethodsViewModel>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater!!.inflate(R.layout.payment_methods_dialog, null)
////        getDialog().getWindow().setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)));
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initUi()
//}
//
//    private fun initUi() {
//        paymentMethodsHeaderView.setHeaderViewListener(this)
//        paymentMethodsSave.setOnClickListener { onSave() }
//
////        CustomerSession.initCustomerSession(context!!, EphemeralKeyProvider(
////                object : EphemeralKeyProvider.ProgressListener() {
////                    fun onStringResponse(string: String) {
////                        if (string.startsWith("Error: ")) {
////                            // Show the error to the user.
////                        }
////                    }
////                })
////        )
//    }
////    fun createEphemeralKey(
////        @NonNull @Size(min = 4) apiVersion: String,
////        @NonNull keyUpdateListener: EphemeralKeyUpdateListener
////    ) {
////        val apiParamMap = HashMap()
////        apiParamMap.put("api_version", apiVersion)
////
////        mCompositeDisposable.add(
////            mStripeService.createEphemeralKey(apiParamMap)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(
////                    { response ->
////                        try {
////                            val rawKey = response.string()
////                            keyUpdateListener.onKeyUpdate(rawKey)
////                            mProgressListener.onStringResponse(rawKey)
////                        } catch (ignored: IOException) {
////                        }
////                    },
////                    { throwable -> mProgressListener.onStringResponse(throwable.getMessage()) })
////        )
////    }
//
//
//    private fun onSave() {
//        val cardToSave = paymentMethodsCardWidget.card
//        //optional added information
////        cardToSave = cardToSave.toBuilder().name("Customer Name").build();
////        cardToSave = cardToSave.toBuilder().addressZip("12345").build();
//        if (cardToSave != null) {
//            viewModel.tokenizeCard(ephemeralKey, cardToSave)
//        }
//
//        viewModel.addCard.observe(this, Observer { addCardEvent ->
////            rateLastOrderPb.hide()
//            if (addCardEvent.isSuccess) {
//                PaymentMethodAcceptedDialog().show(childFragmentManager, Constants.PAYMENT_METHOD_SUCCESS_DIALOG)
//            }
//        })
//
//
//    }
//
//
//
//    override fun onHeaderBackClick() {
//        dismiss()
//    }
//
//
//}