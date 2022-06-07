package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.support_center

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.FragmentSupportBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class SupportBottomSheet : TopCorneredBottomSheet() {

    private var binding: FragmentSupportBinding? = null
    val viewModel by viewModel<SupportViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSupportBinding.bind(view)
        setFullScreenDialog()

        initUI()
    }

    private fun initUI() {
        binding?.apply {
            supportDialogCallButton.setOnClickListener {
                (activity as MainActivity).onContactUsClick()
            }
            supportDialogTextButton.setOnClickListener {
                (activity as MainActivity).sendSmsText()
            }

            supportCenterQA.setOnClickListener { openQA() }

            supportDialogNext.setOnClickListener { sendMail() }

            supportDialogHeader.setOnIconClickListener {
                dismiss()
            }
        }
    }

    private fun openQA() {
        WebDocsDialog.newInstance(Constants.WEB_DOCS_QA)
            .show(childFragmentManager, Constants.WEB_DOCS_DIALOG)
    }

    private fun sendMail() {
        val text = binding?.supportDialogCommentInput?.text
        val title = viewModel.getMailTitle()
        val address = viewModel.getAdminMailAddress()
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        try {
            startActivity(intent)
        }catch (ex: ActivityNotFoundException){
            binding?.supportDialogContentLayout?.let {
                showErrorToast(getString(R.string.email_app_not_found_error), it)
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }
}
