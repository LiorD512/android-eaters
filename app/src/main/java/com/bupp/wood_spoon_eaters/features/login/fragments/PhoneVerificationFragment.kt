package com.bupp.wood_spoon_eaters.features.login.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.views.FloatingLabelEditText
import kotlinx.android.synthetic.main.fragment_phone_verification.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PhoneVerificationFragment : Fragment(R.layout.fragment_phone_verification), CompoundButton.OnCheckedChangeListener,
    InputTitleView.InputTitleViewListener, FloatingLabelEditText.FloatingLabelEventListener {

    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initCb()
        initObservers()

    }

    private fun initCb() {
//        verificationFragPrivacyPolicyCb.setOnCheckedChangeListener(this)

        val ss = SpannableString("By continuing you confirm our Terms of use & Privacy Policy")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                WebDocsDialog(Constants.WEB_DOCS_TERMS).show(childFragmentManager, Constants.WEB_DOCS_DIALOG)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.linkColor = Color.BLUE
            }
        }

        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(textView: View) {
                WebDocsDialog(Constants.WEB_DOCS_PRIVACY).show(childFragmentManager, Constants.WEB_DOCS_DIALOG)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }

        ss.setSpan(clickableSpan, 30, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpan2, 45, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        verificationFragTermsText.text = ss
        verificationFragTermsText.movementMethod = LinkMovementMethod.getInstance()
        verificationFragTermsText.highlightColor = Color.TRANSPARENT
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        viewModel.setPhoneCb(isChecked)
//        if (isChecked) {
//            verificationFragPrivacyPolicyCb.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark))
//        }
    }

    private fun initObservers() {
        viewModel.phoneFieldErrorEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.ErrorEventType.PHONE_EMPTY -> {
                    verificationFragmentInput.showError()
                }
            }
        })
//        viewModel.phoneCbFieldErrorEvent.observe(viewLifecycleOwner, Observer{
//            when(it){
//                LoginViewModel.ErrorEventType.CB_REQUIRED -> {
//                    verificationFragPrivacyPolicyCb.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
//                }
//            }
//        })
    }

    private fun initUi() {
        verificationFragmentInput.setEventListener(this)

        verificationFragmentNext.setOnClickListener {
            sendCode()
        }
    }

    private fun sendCode() {
        viewModel.sendPhoneNumber()
    }


    override fun onFieldInputChange(curView: FloatingLabelEditText) {
        curView.getText().let {
            viewModel.setUserPhone(it)
        }
    }


}