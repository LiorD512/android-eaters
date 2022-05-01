package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.PaymentMethodsDialogBinding
import com.uxcam.UXCam


class PaymentMethodsBottomSheet: TopCorneredBottomSheet() {

    var url: String = ""
    var binding: PaymentMethodsDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyleAdjustNothing)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_methods_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = PaymentMethodsDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        setFullScreenDialog()

        arguments?.let {
            url = requireArguments().getString(URL,"")
        }

        initUi()
    }

    @JavascriptInterface
    fun initUi() {
        binding?.apply {
            UXCam.occludeSensitiveViewWithoutGesture(paymentMethodsWebView)
            paymentMethodsHeader.setOnIconClickListener {
                dismiss()
            }
            paymentMethodsWebView.settings.javaScriptEnabled = true
            paymentMethodsWebView.settings.domStorageEnabled = true
            paymentMethodsWebView.loadUrl(url)
        }

    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        private const val URL = "url"

        fun newInstance(url: String): PaymentMethodsBottomSheet {
            val fragment = PaymentMethodsBottomSheet()
            val args = Bundle()
            args.putString(URL, url)
            fragment.arguments = args
            return fragment
        }
    }


}