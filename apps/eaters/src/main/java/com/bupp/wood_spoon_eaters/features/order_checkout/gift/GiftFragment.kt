package com.bupp.wood_spoon_eaters.features.order_checkout.gift

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.country_code_chooser.CountryChooserBottomSheetFragmentResult
import com.bupp.wood_spoon_eaters.custom_views.CheckoutHeaderView
import com.bupp.wood_spoon_eaters.custom_views.phone_number.PhoneSelectorView
import com.bupp.wood_spoon_eaters.custom_views.phone_number.setCountryCode
import com.bupp.wood_spoon_eaters.databinding.GiftFragmentBinding
import com.bupp.wood_spoon_eaters.model.CountriesISO
import com.bupp.wood_spoon_eaters.utils.closeKeyboard
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import com.bupp.wood_spoon_eaters.views.WSEditText
import com.bupp.wood_spoon_eaters.views.WSProgressBar
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GiftFragment : Fragment(R.layout.gift_fragment) {

    private val binding by viewBinding(GiftFragmentBinding::bind)
    private val viewModel by viewModel<GiftViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initInputs()

        observeViewModelState()

        observeViewModelEvents()
    }

    private fun initInputs() {
        binding.giftFragmentHeader.setCheckoutHeaderListener(object :
            CheckoutHeaderView.CheckoutHeaderListener {
            override fun onBackBtnClick() {
                activity?.onBackPressed()
            }
        })

        binding.notifyRecipientCheckBox.setOnClickListener {
            with(binding) {
                recipientEmail.isVisible = notifyRecipientCheckBox.isChecked
            }
        }

        binding.giftSubmitButton.setOnClickListener {
            closeKeyboard()
            gatherInputs()
            viewModel.submit()
        }

        initPhoneSelector()
    }

    private fun gatherInputs() {
        with(binding) {

            viewModel.setValues(
                firstName = recipientFirstName.getText(),
                lastName = recipientLastName.getText(),
                phoneNumber = recipientPhoneSelector.getPhoneString(),
                notifyRecipient = notifyRecipientCheckBox.isChecked,
                email = binding.recipientEmail.getText()
            )
        }
    }

    private fun initPhoneSelector() {
        binding.recipientPhoneSelector.onCountryClickListener =
            PhoneSelectorView.OnCountryClickListener { phoneView ->
                childFragmentManager.setFragmentResultListener(
                    CountryChooserBottomSheetFragmentResult.requestKey,
                    this
                ) { _, result ->
                    result.getParcelable<CountriesISO>(
                        CountryChooserBottomSheetFragmentResult.resultKey
                    )?.let {
                        phoneView.setCountryCode(it)
                    }
                }
                CountryChooserBottomSheetFragmentResult().show(childFragmentManager, null)
            }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        updateInputsWithState(state)
                    }
                }
                launch {
                    viewModel.state.map { it.inProgress }.distinctUntilChanged().collect {
                        binding.giftProgress.setInProgress(it)
                    }
                }
                launch {
                    viewModel.state.map { it.errors }.distinctUntilChanged().collect {
                        updateErrors(it)
                    }
                }
            }
        }
    }

    private fun observeViewModelEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is GiftViewModelEvents.Error -> showErrorToast(
                                event.message ?: getString(R.string.generic_error_message),
                                Toast.LENGTH_SHORT
                            )
                            GiftViewModelEvents.NavigateDone -> {
                                activity?.onBackPressed()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateInputsWithState(state: GiftViewModelState) {
        binding.recipientFirstName.setTextIfNeeded(state.recipientFirstName)
        binding.recipientLastName.setTextIfNeeded(state.recipientLastName)
        binding.recipientPhoneSelector.setPhoneString(state.recipientPhoneNumber)
        binding.recipientEmail.setTextIfNeeded(state.recipientEmail)
        binding.recipientEmail.isVisible = state.notifyRecipient
        binding.notifyRecipientCheckBox.isChecked = state.notifyRecipient
    }

    private fun updateErrors(errors: List<GiftViewModelState.Errors>) {

        errors.forEach { error ->
            when (error) {
                GiftViewModelState.Errors.recipientFirstName -> binding.recipientFirstName.showError()
                GiftViewModelState.Errors.recipientLastName -> binding.recipientLastName.showError()
                GiftViewModelState.Errors.recipientPhoneNumber -> binding.recipientPhoneSelector.showError()
                GiftViewModelState.Errors.recipientEmail -> binding.recipientEmail.showError()
            }
        }
    }
}

fun WSProgressBar.setInProgress(inProgress: Boolean) = if (inProgress) {
    show()
} else {
    hide()
}

private fun WSEditText.setTextIfNeeded(text: String?) {
    if (getText() != text) {
        setText(text)
    }
}