package com.bupp.wood_spoon_eaters.dialogs.web_docs

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.WebDocsDialogBinding
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel


class WebDocsDialog(val type: Int) : DialogFragment(R.layout.web_docs_dialog), HeaderView.HeaderViewListener {

    lateinit var binding: WebDocsDialogBinding
    val viewModel by viewModel<WebDocsViewModel>()

    override fun getTheme(): Int {
        return R.style.FullScreenDialogStyle
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = WebDocsDialogBinding.bind(view)

        initUi()

    }

    private fun initUi() {
        with(binding){
            webDocsPrivacy.setOnClickListener {
                webDocsTerms.isSelected = false
                webDocsPrivacy.isSelected = true
                openUrl(Constants.WEB_DOCS_PRIVACY)
            }

            webDocsTerms.setOnClickListener {
                webDocsTerms.isSelected = true
                webDocsPrivacy.isSelected = false
                openUrl(Constants.WEB_DOCS_TERMS)
            }

            webDocsHeaderView.setHeaderViewListener(this@WebDocsDialog)

            when(type){
                Constants.WEB_DOCS_PRIVACY -> {
                    webDocsPrivacy.performClick()
                    Analytics.with(requireContext()).screen("Privacy policy")
                }
                Constants.WEB_DOCS_TERMS -> {
                    webDocsTerms.performClick()
                    Analytics.with(requireContext()).screen("Terms of use")
                }
                Constants.WEB_DOCS_QA -> {
                    Analytics.with(requireContext()).screen("QA")
                    webDocsBtnsHeader.visibility = View.GONE
                    openUrl(type)
                    webDocsHeaderView.setTitle("Popular Q&A")

                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun openUrl(type: Int){
        val link = viewModel.getUrl(type)

        binding.webDocsWebView.settings.javaScriptEnabled = true

        binding.webDocsWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                webDocsPb.hide()
                view?.loadUrl(url)
                return true
            }
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                MTLogger.d(TAG, "onReceivedSslError")
                handler.proceed() // Ignore SSL certificate errors
            }

            override fun onPageFinished(view: WebView, url: String) {
                MTLogger.d(TAG, "\"onPageFinished: $url\"")
                if ("about:blank" == url && view.tag != null) {
                    view.loadUrl(view.tag.toString())
                } else {
                    view.tag = url
                }
            }
        }
        binding.webDocsWebView.loadUrl(link)
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    companion object{
        const val TAG = "wowWebDocsDialog"
    }

}