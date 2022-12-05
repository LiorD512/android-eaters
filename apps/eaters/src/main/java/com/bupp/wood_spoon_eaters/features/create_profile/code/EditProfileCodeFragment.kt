package com.bupp.wood_spoon_eaters.features.create_profile.code

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.FragmentCodeBinding
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Integer.min

private const val PIN_SIZE = 4

@Parcelize
@Keep
data class EditProfileCodeFragmentParams(
    val phoneNumber: String
) : Parcelable

class EditProfileCodeFragment : Fragment(R.layout.fragment_code) {

    private val binding by viewBinding(FragmentCodeBinding::bind)
    private val viewModel by viewModel<EditProfileCodeViewModel>()
    private val navArgs by navArgs<EditProfileCodeFragmentArgs>()

    private var timer: CountDownTimer? = null

    private val inputControls by lazy {
        with(binding) {
            listOf(
                Pair(codeFragInput1, codeFragUnderline1),
                Pair(codeFragInput2, codeFragUnderline2),
                Pair(codeFragInput3, codeFragUnderline3),
                Pair(codeFragInput4, codeFragUnderline4),
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setPhoneNumber(navArgs.params.phoneNumber)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initUi()
    }

    override fun onResume() {
        super.onResume()
        startResendTimer()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.codeFragNumber.text = "+${state.phoneNumber?.dropWhile { it == '+' }}"
                    binding.progress.setProgress(state.progress)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }

    private fun handleEvent(event: EditProfileCodeEvents) {
        binding.codeFragInputError.isVisible = false
        when (event) {
            EditProfileCodeEvents.EmptyCodeError -> {
                binding.codeFragInputError.text = getString(R.string.login_error_empty_code)
                binding.codeFragInputError.isVisible = true
            }

            EditProfileCodeEvents.WrongPassword -> {
                binding.codeFragInputError.text = getString(R.string.login_error_wrong_code)
                binding.codeFragInputError.isVisible = true
            }
            is EditProfileCodeEvents.InvalidPhoneNumber -> {
                showErrorToast(event.errorMessage ?: getString(R.string.login_error_wrong_phone), Toast.LENGTH_LONG)
                findNavController().navigateUp()
            }
            EditProfileCodeEvents.ValidateCodeGeneralError -> showErrorToast("Can't validation code")
            EditProfileCodeEvents.SendPhoneNumberGeneralError -> showErrorToast("Can't request validation code")
            EditProfileCodeEvents.CodeValidationSuccess -> {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUi() = with(binding) {

        codeFragInput.isEnabled = false
        codeFragNext.setOnClickListener {
            if (codeFragNext.isEnabled) {
                sendCode()
            }
        }

        codeFragNumPad.inputEditText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                val codeString = s.toString().take(min(s.length, PIN_SIZE))

                codeFragInputError.visibility = View.INVISIBLE
                viewModel.setVerificationCode(codeString)

                val charactersArray = codeString.toCharArray()
                inputControls.forEachIndexed { index, pair ->
                    val input = pair.first
                    val underline = pair.second

                    input.text = charactersArray.getOrNull(index)?.toString() ?: ""
                    underline.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.greyish_brown
                        )
                    )
                }
                inputControls.drop(charactersArray.size).forEach { pair ->
                    val input = pair.first
                    val underline = pair.second
                    input.text = ""
                    underline.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_periwinkle
                        )
                    )
                }


                if (codeString.length == PIN_SIZE) {
                    viewModel.onSendValidateCodeClicked()
                }
            }
        })

        codeFragCloseBtn.setOnClickListener { findNavController().navigateUp() }

    }

    private fun resendCode() {
        viewModel.retryRequestCode()
    }

    private fun sendCode() {
        viewModel.onSendValidateCodeClicked()
    }

    private fun startResendTimer() {
        binding?.codeFragResendCode?.setOnClickListener(null)
        timer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding?.codeFragResendCode?.text = "Resend code in: ${millisUntilFinished / 1000}"
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
        timer?.cancel()
        timer = null
        super.onDestroyView()
    }

    companion object {
        const val ARGS_KEY = "fragment_parameters"
    }
}