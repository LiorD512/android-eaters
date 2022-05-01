package com.bupp.wood_spoon_chef.presentation.dialogs.web_docs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.WebDocsDialogBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class WebDocsDialog : TopCorneredBottomSheet() {

    var type: Int? = null

    var binding : WebDocsDialogBinding? = null
    val viewModel by viewModel<WebDocsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.web_docs_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = WebDocsDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            type = requireArguments().getInt(TYPE)
        }

        setFullScreenDialog()

//        webDocsPb.show()
        initUi()
        if(type != null){
            when(type){
                Constants.WEB_DOCS_PRIVACY -> {
                    binding!!.webDocsHeaderView.setTitle("Privacy policy and terms of use")
                    binding!!.webDocsTypesLayout.visibility = View.VISIBLE
                    binding!!.webDocsPrivacy.performClick()
                }
                Constants.WEB_DOCS_TERMS -> {
                    binding!!.webDocsHeaderView.setTitle("Privacy policy and terms of use")
                    binding!!.webDocsTypesLayout.visibility = View.VISIBLE
                    binding!!.webDocsTerms.performClick()
                }
                Constants.WEB_DOCS_QA -> {
                    binding!!.webDocsHeaderView.setTitle("Popular Q&A")
                    binding!!.webDocsTypesLayout.visibility = View.GONE
                    openUrl(Constants.WEB_DOCS_QA)
                }
            }
        }else{
            binding!!.webDocsPrivacy.performClick()
        }
    }

    private fun initUi() {

        binding!!.webDocsPrivacy.setOnClickListener {
            binding!!.webDocsTerms.isSelected = false
            binding!!.webDocsPrivacy.isSelected = true
                openUrl(Constants.WEB_DOCS_PRIVACY)
            }
        binding!!.webDocsTerms.setOnClickListener {
            binding!!.webDocsTerms.isSelected = true
            binding!!.webDocsPrivacy.isSelected = false
                openUrl(Constants.WEB_DOCS_TERMS)
            }
        binding!!.webDocsHeaderView.setOnIconClickListener {
            dismiss()
        }

    }

    private fun openUrl(type: Int){
        Log.d("openUrl", "openUrl: open")
        val link = viewModel.getUrl(type)

        binding!!.webDocsWebView.settings.loadWithOverviewMode = true
        binding!!. webDocsWebView.settings.useWideViewPort = true

        binding!!.webDocsWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                webDocsPb.hide()
                url?.let{
                    view?.loadUrl(url)
                    Log.d("openUrl", "openUrl: load")
                }
                return true
            }
        }

        binding!!.webDocsWebView.loadUrl(link)
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        private const val TYPE = "type"

        fun newInstance(type: Int): WebDocsDialog {
            val fragment = WebDocsDialog()
            val args = Bundle()
            args.putInt(TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

}