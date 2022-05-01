package com.bupp.wood_spoon_chef.presentation.features.onboarding.login.fragments


import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.databinding.FragmentCodeBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.onboarding.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CodeFragment : BaseFragment(R.layout.fragment_code) {

    private var binding: FragmentCodeBinding? = null
    private val viewModel: LoginViewModel by sharedViewModel()
    private var timer: CountDownTimer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCodeBinding.bind(view)

        initObservers()
        initUi()
    }


    override fun onResume() {
        super.onResume()
        startResendTimer()
    }

    private fun initObservers() {
        viewModel.emptyCodeEvent.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { empty ->
                if (empty) {
                    binding?.codeFragInputError?.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initUi() {
        binding?.apply {

            codeFragNumber.text = viewModel.getCensoredPhone()

            codeFragInput.isEnabled = false
            codeFragNext.setOnClickListener {
                if (codeFragNext.isEnabled) {
                    sendCode()
                }
            }

            codeFragNumPad.inputEditText.addTextChangedListener(object :
                SimpleTextWatcher() {
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
                        codeFragUnderline1.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.greyish_brown
                            )
                        )
                    } else {
                        codeFragInput1.text = ""
                        codeFragUnderline1.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_periwinkle
                            )
                        )
                    }
                    if (codeString.length >= 2) {
                        codeFragInput2.text = codeString.substring(1, 2)
                        codeFragUnderline2.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.greyish_brown
                            )
                        )
                    } else {
                        codeFragInput2.text = ""
                        codeFragUnderline2.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_periwinkle
                            )
                        )
                    }
                    if (codeString.length >= 3) {
                        codeFragInput3.text = codeString.substring(2, 3)
                        codeFragUnderline3.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.greyish_brown
                            )
                        )
                    } else {
                        codeFragInput3.text = ""
                        codeFragUnderline3.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_periwinkle
                            )
                        )
                    }
                    if (codeString.length >= 4) {
                        codeFragInput4.text = codeString.substring(3, 4)
                        codeFragUnderline4.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.greyish_brown
                            )
                        )
                        sendCode()
                    } else {
                        codeFragInput4.text = ""
                        codeFragUnderline4.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_periwinkle
                            )
                        )
                    }
                }
            })

            codeFragCloseBtn.setOnClickListener { activity?.onBackPressed() }

        }
    }

    private fun resendCode() {
        MTLogger.d("on resend code click")
        viewModel.resendCode()
        startResendTimer()
    }

    private fun sendCode() {
        MTLogger.d("on send code click")
        viewModel.sendPhoneAndCodeNumber()
    }

    private fun startResendTimer() {
        binding?.codeFragResendCode?.setOnClickListener(null)
        timer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding?.codeFragResendCode?.text =
                    "Resend code in: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                binding?.codeFragResendCode?.text = "Press here to resend"
                binding?.codeFragResendCode?.setOnClickListener {
                    resendCode()
                }
            }
        }
        timer?.start()
    }

    override fun onDestroyView() {
        timer = null
        super.onDestroyView()
    }

    override fun clearClassVariables() {
        binding = null
    }

}