package com.bupp.wood_spoon_eaters.dialogs.web_docs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.segment.analytics.Analytics
import kotlinx.android.synthetic.main.web_docs_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class WebDocsDialog(val type: Int) : DialogFragment(), HeaderView.HeaderViewListener {

    val viewModel by viewModel<WebDocsViewModel>()

    override fun getTheme(): Int {
        return R.style.FullScreenDialogStyle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.web_docs_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
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

    private fun initUi() {
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

        webDocsHeaderView.setHeaderViewListener(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun openUrl(type: Int){
        val link = viewModel.getUrl(type)

        webDocsWebView.settings.javaScriptEnabled = true

        webDocsWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                webDocsPb.hide()
                view?.loadUrl(url)
                return true
            }
        }
        webDocsWebView.loadUrl(link)
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

}