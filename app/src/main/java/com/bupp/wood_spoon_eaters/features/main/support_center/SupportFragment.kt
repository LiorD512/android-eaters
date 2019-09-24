package com.bupp.wood_spoon_eaters.features.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportViewModel
import kotlinx.android.synthetic.main.fragment_support.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SupportFragment : Fragment(), InputTitleView.InputTitleViewListener {

    val viewModel by viewModel<SupportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        supportDialogNext.setBtnEnabled(false)
        supportDialogCommentInput.setInputTitleViewListener(this)

        supportDialogCallButton.setOnClickListener {
            (activity as MainActivity).callPhoneNumber()
        }
        supportDialogTextButton.setOnClickListener {
            (activity as MainActivity).sendSmsText()
        }

        supportDialogNext.setOnClickListener { sendMail() }
    }

    private fun sendMail() {
        val text = supportDialogCommentInput.getText()
        val address = viewModel.getAdminMailAddress()
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$address")
        intent.putExtra(Intent.EXTRA_EMAIL, address)
        intent.putExtra(Intent.EXTRA_SUBJECT, text)
        if (intent.resolveActivity(activity?.packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onInputTitleChange(str: String?) {
        if (supportDialogCommentInput.isValid()) {
            supportDialogNext.setBtnEnabled(true)
        } else {
            supportDialogNext.setBtnEnabled(false)
        }
    }
}
