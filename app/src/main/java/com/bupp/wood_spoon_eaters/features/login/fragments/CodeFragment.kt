package com.bupp.wood_spoon_eaters.features.login.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.FragmentCodeBinding
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import me.ibrahimsn.lib.util.clear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CodeFragment() : Fragment(R.layout.fragment_code) {

    var binding: FragmentCodeBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCodeBinding.bind(view)

        initObservers()
        initUi()
    }

    private fun initObservers() {
        viewModel.errorEvents.observe(viewLifecycleOwner, Observer{
            when(it){
                ErrorEventType.CODE_EMPTY -> {
                    binding!!.codeFragInputError.visibility = View.VISIBLE
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initUi() {
        with(binding!!){
            codeFragNumber.text = "+${viewModel.getCensoredPhone()}"
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
                        codeFragUnderline1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greyish_brown))
                    } else {
                        codeFragInput1.text = ""
                        codeFragUnderline1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_periwinkle))
                    }
                    if (codeString.length >= 2) {
                        codeFragInput2.text = codeString.substring(1, 2)
                        codeFragUnderline2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greyish_brown))
                    } else {
                        codeFragInput2.text = ""
                        codeFragUnderline2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_periwinkle))
                    }
                    if (codeString.length >= 3) {
                        codeFragInput3.text = codeString.substring(2, 3)
                        codeFragUnderline3.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greyish_brown))
                    } else {
                        codeFragInput3.text = ""
                        codeFragUnderline3.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_periwinkle))
                    }
                    if (codeString.length >= 4) {
                        codeFragInput4.text = codeString.substring(3, 4)
                        codeFragUnderline4.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greyish_brown))
                        sendCode()
                    } else {
                        codeFragInput4.text = ""
                        codeFragUnderline4.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_periwinkle))
                    }
                }
            })

            codeFragResendCode.setOnClickListener {
                resendCode()
            }
        }
    }

    private fun resendCode() {
        viewModel.resendCode()
    }

    private fun sendCode() {
        viewModel.sendPhoneAndCodeNumber()
    }

}