package com.bupp.wood_spoon_eaters.features.main.support_center

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.databinding.FragmentSupportBinding
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel


class SupportFragment : Fragment(R.layout.fragment_support), InputTitleView.InputTitleViewListener {

    lateinit var binding: FragmentSupportBinding
    val viewModel by viewModel<SupportViewModel>()

    companion object {
        fun newInstance() = SupportFragment()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSupportBinding.bind(view)
        Analytics.with(requireContext()).screen("Support center")

        initUI()
    }

    private fun initUI() {
        with(binding){
            supportDialogNext.setBtnEnabled(false)
            supportDialogCommentInput.setInputTitleViewListener(this@SupportFragment)

            supportDialogCallButton.setOnClickListener {
                (activity as MainActivity).onContactUsClick()
            }
            supportDialogTextButton.setOnClickListener {
                (activity as MainActivity).sendSmsText()
            }
            supportDialogQA.setOnClickListener{ openQaUrl()}
            supportDialogNext.setOnClickListener { sendMail() }
        }
    }

    private fun openQaUrl() {
        WebDocsDialog(Constants.WEB_DOCS_QA).show(childFragmentManager, Constants.WEB_DOCS_DIALOG)
    }

    private fun sendMail() {
        val text = binding.supportDialogCommentInput.getText()
        val address = viewModel.getAdminMailAddress()

        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, viewModel.getEmailSubject())
        emailIntent.putExtra(Intent.EXTRA_TEXT, text)
        emailIntent.selector = selectorIntent

        activity?.startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    override fun onInputTitleChange(str: String?) {
        with(binding){
            if (supportDialogCommentInput.isValid()) {
                supportDialogNext.setBtnEnabled(true)
            } else {
                supportDialogNext.setBtnEnabled(false)
            }
        }
    }
}
