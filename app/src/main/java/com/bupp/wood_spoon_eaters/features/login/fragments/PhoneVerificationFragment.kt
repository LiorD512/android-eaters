package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.view.View
import android.text.Spanned
import android.graphics.Color
import android.text.TextPaint
import androidx.lifecycle.Observer
import android.text.SpannableString
import com.bupp.wood_spoon_eaters.R
import androidx.fragment.app.Fragment
import android.text.style.ClickableSpan
import android.text.method.LinkMovementMethod
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.utils.CountryCodeUtils
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.databinding.FragmentPhoneVerificationBinding
import com.bupp.wood_spoon_eaters.features.bottom_sheets.country_code_chooser.CountryChooserBottomSheet


class PhoneVerificationFragment : Fragment(R.layout.fragment_phone_verification),
    InputTitleView.InputTitleViewListener{

    var binding: FragmentPhoneVerificationBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhoneVerificationBinding.bind(view)

        initUi()
        initObservers()

    }

    private fun initObservers() {
        viewModel.phoneFieldErrorEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                ErrorEventType.PHONE_EMPTY -> {
                    binding!!.verificationFragmentInput.showError()
                }
                else -> {}
            }
        })
        viewModel.countryCodeEvent.observe(viewLifecycleOwner, {
            binding!!.verificationFragmentInput.setPrefix("+${it.country_code}")
            binding!!.verificationFragFlag.text = it.flag
        })
    }

    private fun initUi() {
        with(binding!!){
            val deviceCountryCode = CountryCodeUtils.getCountryCodeData(requireContext())
            deviceCountryCode.let{
                verificationFragmentInput.setPrefix("+${it.countryCodeIso}")
                verificationFragFlag.text = it.flag
                viewModel.setUserPhonePrefix("${it.countryCodeIso}")
            }

            verificationFragmentNext.setOnClickListener {
                sendCode()
            }

            verificationFragFlagLayout.setOnClickListener {
                val countryCodePicker = CountryChooserBottomSheet()
                countryCodePicker.show(childFragmentManager, Constants.COUNTRY_CODE_BOTTOM_SHEET)
            }

            verificationFragmentTerms.setOnClickListener {
                WebDocsDialog(Constants.WEB_DOCS_TERMS).show(childFragmentManager, Constants.WEB_DOCS_DIALOG)
            }
        }
    }

    private fun sendCode() {
        val phoneStr = binding!!.verificationFragmentInput.getText()
        viewModel.setUserPhone(phoneStr)
        viewModel.sendPhoneNumber()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    companion object{
        const val TAG = "wowPhoneVerification"
    }


}