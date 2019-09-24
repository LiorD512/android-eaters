package com.bupp.wood_spoon_eaters.dialogs.web_docs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.web_docs_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WebDocsDialog(val type: Int) : DialogFragment(), HeaderView.HeaderViewListener {

    val viewModel by viewModel<WebDocsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.web_docs_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        webDocsPb.show()
        initUi()
        if(type != null){
            when(type){
                Constants.WEB_DOCS_PRIVACY -> {
                    webDocsPrivacy.performClick()
                }
                Constants.WEB_DOCS_TERMS -> {
                    webDocsTerms.performClick()
                }
            }
        }else{
            webDocsPrivacy.performClick()
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

    fun openUrl(type: Int){
        val link = viewModel.getUrl(type)

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