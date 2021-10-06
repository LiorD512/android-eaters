package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.country_code_chooser.CountryChooserBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.databinding.FragmentPhoneVerificationBinding
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.utils.CountryCodeUtils
import com.bupp.wood_spoon_eaters.views.WSEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PhoneVerificationFragment : Fragment(R.layout.fragment_phone_verification),
    InputTitleView.InputTitleViewListener, WSEditText.WSEditTextListener {

    var binding: FragmentPhoneVerificationBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhoneVerificationBinding.bind(view)

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_GET_OTF_CODE)

        initUi()
        initObservers()

    }

    private fun initObservers() {
        viewModel.phoneFieldErrorEvent.observe(viewLifecycleOwner, {
            when (it) {
                ErrorEventType.PHONE_EMPTY -> {
                    binding!!.verificationFragmentInput.showError()
                }
                else -> {
                }
            }
        })
        viewModel.countryCodeEvent.observe(viewLifecycleOwner, {
            viewModel.setUserPhonePrefix("${it.country_code}")
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

            verificationFragCloseBtn.setOnClickListener {
                activity?.onBackPressed()
            }

            verificationFragFlag.setOnClickListener {
                val countryCodePicker = CountryChooserBottomSheet()
                countryCodePicker.show(childFragmentManager, Constants.COUNTRY_CODE_BOTTOM_SHEET)
            }

            verificationFragmentTerms.setOnClickListener {
                WebDocsDialog(Constants.WEB_DOCS_TERMS).show(childFragmentManager, Constants.WEB_DOCS_DIALOG)
            }
            verificationFragmentInput.requestFocus()

            verificationFragmentInput.setWSEditTextListener(this@PhoneVerificationFragment)
        }
    }

    private fun sendCode() {
        val phoneStr = binding!!.verificationFragmentInput.getText()
        if(CountryCodeUtils.isPhoneValid(phoneStr)){
            phoneStr!!.let{
                val phone = CountryCodeUtils.simplifyNumber(it)
                phone.let{
                    viewModel.setUserPhone(it)
                    viewModel.sendPhoneNumber()
                }
            }
        }else{
            binding!!.verificationFragmentInput.showError()
        }
    }

    override fun onWSEditTextActionDone() {
        binding!!.verificationFragmentNext.performClick()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


    companion object{
        const val TAG = "wowPhoneVerification"
    }


}