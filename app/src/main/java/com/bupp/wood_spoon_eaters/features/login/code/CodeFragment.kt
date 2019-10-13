package com.bupp.wood_spoon_eaters.features.login.code

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_code.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CodeFragment(val phoneNumber: String) : Fragment() {

    private val viewModel: CodeViewModel by viewModel<CodeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initUi()
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer { navigationEvent ->
            if (navigationEvent != null) {
                (activity as LoginActivity).handlePb(false)
                if (navigationEvent.isCodeLegit) {
                    Log.d("wow", "code success")
                    viewModel.updateMetaData()
                    if(navigationEvent.isAfterLogin){
                        (activity as LoginActivity).onRegisteredUser()
                    }else{
                        (activity as LoginActivity).onCodeSuccess()
                    }
                } else {
                    Log.d("wow", "code fail")
                    Toast.makeText(context, "Invalid code entered", Toast.LENGTH_LONG).show()
                    codeFragNumPad.clearInput()
                }
            }
        })

        viewModel.resendCodeEvent.observe(this, Observer { resendCodeEvent: CodeViewModel.ResendCodeEvent ->
            if (resendCodeEvent != null) {
                (activity as LoginActivity).handlePb(false)
                if (resendCodeEvent.hasSent) {
                    Log.d("wowCode", "resend code success")
                    Toast.makeText(context, "Code sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("wowCode", "resend code fail")
                }
            }
        })
    }

    private fun getCode(): String {
        return codeFragNumPad.getInputText().text.toString()
    }

    private fun initUi() {
        val subtitle = resources.getString(R.string.code_fragment_sub_title)
        codeFragSendNumber.text = "$subtitle $phoneNumber"
        codeFragInput.isEnabled = false
        codeFragNext.setBtnEnabled(false)
        codeFragNext.setOnClickListener {
            if (codeFragNext.isEnabled) {
                sendCode(getCode())
            }
        }

        codeFragNumPad.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val codeString: String = s.toString()
                if (codeString.length > 4) {
                    codeFragNumPad.getInputText().setText(codeString.substring(0, 4))
                    return
                }
                if (codeString.isNotEmpty()) {
                    codeFragInput1.text = codeString.substring(0, 1)
                    codeFragInput1Bkg.elevation = 18f
                } else {
                    codeFragInput1.text = ""
                    codeFragInput1Bkg.elevation = 0f
                }
                if (codeString.length >= 2) {
                    codeFragInput2.text = codeString.substring(1, 2)
                    codeFragInput2Bkg.elevation = 18f
                } else {
                    codeFragInput2.text = ""
                    codeFragInput2Bkg.elevation = 0f
                }
                if (codeString.length >= 3) {
                    codeFragInput3.text = codeString.substring(2, 3)
                    codeFragInput3Bkg.elevation = 18f
                } else {
                    codeFragInput3.text = ""
                    codeFragInput3Bkg.elevation = 0f
                }
                if (codeString.length >= 4) {
                    codeFragInput4.text = codeString.substring(3, 4)
                    codeFragInput4Bkg.elevation = 18f
                    codeFragNext.setBtnEnabled(true)
                    sendCode(getCode())
                } else {
                    codeFragInput4.text = ""
                    codeFragNext.setBtnEnabled(false)
                    codeFragInput4Bkg.elevation = 0f
                }
            }

        })

        codeFragResendCode.setOnClickListener {
            resendCode()
        }
    }

    private fun resendCode() {
        (activity as LoginActivity).handlePb(true)
        viewModel.sendPhoneNumber(phoneNumber)
    }

    private fun sendCode(codeString: String) {
        (activity as LoginActivity).handlePb(true)
        viewModel.sendCode(phoneNumber, codeString)
    }


//        codeFragmentHelp.setOnClickListener { v ->
//            Log.d("wow", "what is code dialog")
////        var welcomeDialog : WelcomeDialog = WelcomeDialog()
////            welcomeDialog.show(fragmentManager, "welcomeDialog")
//            var businessDialog : BusinessCodeDialog = BusinessCodeDialog()
//            businessDialog.show(fragmentManager, Constants.TAG_BUSINESS_DIALOG)
//        }
//    }


}