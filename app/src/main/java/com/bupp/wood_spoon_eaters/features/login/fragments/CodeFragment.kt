package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_code.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CodeFragment() : Fragment(R.layout.fragment_code) {

    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initUi()
    }

    private fun initObservers() {
        viewModel.errorEvents.observe(viewLifecycleOwner, Observer{
            when(it){
                LoginViewModel.ErrorEventType.CODE_EMPTY -> {
                    codeFragInputError.visibility = View.VISIBLE
                }
            }
        })
//        viewModel.resendCodeEvent.observe(viewLifecycleOwner, Observer { resendCodeEvent ->
//            val event = resendCodeEvent.getContentIfNotHandled()
//            event?.let{
//                if (event.hasSent) {
//                    Log.d("wowCode", "resend code success")
//                    Toast.makeText(context, "Code sent!", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.d("wowCode", "resend code fail")
//                }
//            }
//        })
    }

    private fun initUi() {
        val subtitle = resources.getString(R.string.code_fragment_sub_title)
        codeFragSendNumber.text = "$subtitle ${viewModel.phone}"
        codeFragInput.isEnabled = false
        codeFragNext.setOnClickListener {
            if (codeFragNext.isEnabled) {
                sendCode()
            }
        }

        codeFragNumPad.inputEditText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                val codeString: String = s.toString()
                codeFragInputError.visibility = View.INVISIBLE
                viewModel.setUserCode(codeString)
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
                    sendCode()
                } else {
                    codeFragInput4.text = ""
                    codeFragInput4Bkg.elevation = 0f
                }
            }
        })

        codeFragResendCode.setOnClickListener {
            resendCode()
        }
    }

    private fun resendCode() {
        viewModel.resendCode()
    }

    private fun sendCode() {
        viewModel.sendPhoneAndCodeNumber()
    }

}