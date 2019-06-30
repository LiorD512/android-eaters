package com.bupp.wood_spoon_eaters.features.login.verification

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_phone_verification.*
import org.koin.android.viewmodel.ext.android.viewModel


class PhoneVerificationFragment : Fragment() {

    private val viewModel: PhoneVerificationViewModel by viewModel<PhoneVerificationViewModel>()
    private var phoneNumber: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer { event ->
            if (event.isSuccess) {
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
                if (str.isNotEmpty()) {
                    verificationFragmentNext.setBtnEnabled(true)
                } else {
                    verificationFragmentNext.setBtnEnabled(false)
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