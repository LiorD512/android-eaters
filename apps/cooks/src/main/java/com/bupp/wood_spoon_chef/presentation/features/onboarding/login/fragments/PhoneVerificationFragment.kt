package com.bupp.wood_spoon_chef.presentation.features.onboarding.login.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentPhoneVerificationBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen.country_chooser.CountryCodeChooserBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.onboarding.login.LoginViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CountriesISO
import com.bupp.wood_spoon_chef.utils.CountryCodeUtils
import com.bupp.wood_spoon_chef.utils.Utils
import com.bupp.wood_spoon_chef.presentation.views.WSEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PhoneVerificationFragment : BaseFragment(R.layout.fragment_phone_verification),
    CountryCodeChooserBottomSheet.CountryCodeListener, WSEditText.WSEditTextListener {


    var binding: FragmentPhoneVerificationBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhoneVerificationBinding.bind(view)


        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.phoneFieldErrorEvent.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { it ->
                if (it) {
                    binding?.verificationFragmentInput?.showError()
                }
            }
        })
        viewModel.countryCodeEvent.observe(viewLifecycleOwner, {
            viewModel.setUserPhonePrefix("${it.country_code}")
            binding?.apply {
                verificationFragmentInput.setPrefix("+${it.country_code}  ")
                verificationFragFlag.text = it.flag
            }
        })
    }

    private fun initUi() {
        with(binding!!) {
            openKeyboard(verificationFragmentInput)

            val deviceCountryCode = CountryCodeUtils.getCountryCodeData(requireContext())
            deviceCountryCode.let {
                verificationFragmentInput.setPrefix("+${it.countryCodeIso}  ")
                verificationFragFlag.text = it.flag
                viewModel.setUserPhonePrefix("${it.countryCodeIso}")
            }

            verificationFragmentNext.setOnClickListener {
                sendCode()
            }
            verificationFragCloseBtn.setOnClickListener {
                activity?.onBackPressed()
            }

            verificationFragFlag.setOnClickListener {
                val countryCodePicker = CountryCodeChooserBottomSheet()
                countryCodePicker.setCountryCodeListener(this@PhoneVerificationFragment)
                countryCodePicker.show(childFragmentManager, Constants.COUNTRY_CODE_BOTTOM_SHEET)
            }

            verificationFragmentTerms.setOnClickListener {
                WebDocsDialog.newInstance(Constants.WEB_DOCS_TERMS).show(
                    childFragmentManager,
                    Constants.WEB_DOCS_DIALOG
                )
            }
            verificationFragmentInput.requestFocus()
            verificationFragmentInput.setWSEditTextListener(this@PhoneVerificationFragment)
        }
    }

    override fun onIMEDoneClick(text: String) {
        Utils.hideKeyBoard(requireActivity())
        sendCode()
    }

    override fun onCountryCodeSelected(country: CountriesISO) {
        country.let {
            viewModel.setUserPhonePrefix("${it.country_code}")
            binding!!.verificationFragmentInput.setPrefix("+${it.country_code}  ")
            binding!!.verificationFragFlag.text = it.flag
        }
    }

    private fun sendCode() {
        binding?.apply {
            val phoneStr = verificationFragmentInput.getTextOrNull()
            phoneStr?.let {
                val phone = CountryCodeUtils.simplifyNumber(requireContext(), it)
                if (CountryCodeUtils.isPhoneValid(phone, viewModel.getUserPhonePrefix())) {
                    viewModel.setUserPhone(phone)
                    viewModel.sendPhoneNumber()
                } else {
                    verificationFragmentInput.showError()
                }
            }
        }
    }

    private fun openKeyboard(focusView: View) {
        val inputManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(focusView, InputMethodManager.SHOW_FORCED)
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        const val TAG = "wowPhoneVerification"
    }
}