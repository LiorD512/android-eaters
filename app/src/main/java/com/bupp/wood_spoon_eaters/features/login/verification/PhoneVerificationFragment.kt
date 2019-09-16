package com.bupp.wood_spoon_eaters.features.login.verification

import android.graphics.Color
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.fragment_phone_verification.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PhoneVerificationFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private var phoneEntered: Boolean = false
    private var privacyPolicyCb: Boolean = false
    private val viewModel: PhoneVerificationViewModel by viewModel<PhoneVerificationViewModel>()
    private var phoneNumber: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initCb()
        initObservers()
    }

    private fun initCb() {
        verificationFragPrivacyPolicyCb.setOnCheckedChangeListener(this)

        val ss = SpannableString("Please indicate that you accept WoodSpoon Terms and that you have read our Privacy policy")
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

        ss.setSpan(clickableSpan, 42, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpan2, 75, 89, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        verificationFragCbText.text = ss
        verificationFragCbText.movementMethod = LinkMovementMethod.getInstance()
        verificationFragCbText.highlightColor = Color.TRANSPARENT

        verificationFragCbText.setOnClickListener {
            verificationFragPrivacyPolicyCb.performClick()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        privacyPolicyCb = isChecked
        verifyFields()
    }

    fun verifyFields(){
        if(privacyPolicyCb && phoneEntered){
            verificationFragmentNext.setBtnEnabled(true)
        }else{
            verificationFragmentNext.setBtnEnabled(false)
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer { event ->
            if (event.isSuccess) {
                Utils.hideKeyBoard(activity!!)
                Log.d("wow", "phoneVerification success")
                verificationFragmentPb.hide()
                (activity as LoginActivity).loadCodeFragment(phoneNumber)
            } else {
                Log.d("wow", "phoneVerification fail")
                Toast.makeText(context, "Invalid phone number", Toast.LENGTH_LONG).show()
                verificationFragmentPb.hide()
            }
        })
    }

    private fun initUi() {
        verificationFragmentNext.setBtnEnabled(false)

        verificationFragmentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val str: String = s.toString()
                if (str.length > 0) {
                    phoneEntered = true
                    verifyFields()
                } else {
                    phoneEntered = false
                }
            }

        })
        verificationFragmentInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        verificationFragmentNext.setOnClickListener {
            sendCode()
        }

        verificationFragmentInput.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                verificationFragmentInputLayout.elevation = 18f
            } else {
                verificationFragmentInputLayout.elevation = 8f
            }
        }
    }

    private fun sendCode() {
        phoneNumber = verificationFragmentInput.text.toString()
        verificationFragmentPb.show()
        viewModel.sendPhoneNumber(phoneNumber)
    }


}