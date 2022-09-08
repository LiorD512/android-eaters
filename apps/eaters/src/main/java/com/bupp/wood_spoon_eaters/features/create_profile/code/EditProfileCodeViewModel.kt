package com.bupp.wood_spoon_eaters.features.create_profile.code

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.create_profile.PhoneNumberVerificationRequestCodeUseCase
import com.bupp.wood_spoon_eaters.features.create_profile.SendPhoneVerificationUseCase
import com.eatwoodspoon.analytics.AnalyticsEventReporter
import com.eatwoodspoon.analytics.events.MobileAuthEvent
import com.eatwoodspoon.android_utils.flows.FlowViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileCodeState(
    val progress: Boolean = false,
    val verificationCode: String? = null,
    val phoneNumber: String? = null,
)

sealed class EditProfileCodeEvents {
    object InvalidPhoneNumber : EditProfileCodeEvents()
    object SendPhoneNumberGeneralError : EditProfileCodeEvents()
    object ValidateCodeGeneralError : EditProfileCodeEvents()
    object EmptyCodeError : EditProfileCodeEvents()
    object WrongPassword : EditProfileCodeEvents()
    object CodeValidationSuccess : EditProfileCodeEvents()
}

class EditProfileCodeViewModel(
    private val phoneNumberVerificationRequestCodeUseCase: PhoneNumberVerificationRequestCodeUseCase,
    private val sendPhoneVerificationUseCase: SendPhoneVerificationUseCase,
    private val analytics: AnalyticsEventReporter
) :
    FlowViewModel<EditProfileCodeState, EditProfileCodeEvents>(initialState = EditProfileCodeState()) {

    init {
        analytics.reportEvent(MobileAuthEvent.EatersPhoneVerificationScreenOpenedEvent())
    }

    fun setVerificationCode(codeString: String) {
        _state.update {
            it.copy(verificationCode = codeString)
        }
    }

    fun setPhoneNumber(phoneNumber: String) {
        _state.update {
            it.copy(phoneNumber = phoneNumber)
        }
        sendRequestCode(false)
    }

    fun retryRequestCode() {
        sendRequestCode(true)
    }

    private fun sendRequestCode(isRetry: Boolean) {
        analytics.reportEvent(MobileAuthEvent.EatersRequestVerificationCodeEvent(is_retry = isRetry))
        viewModelScope.launch {
            startProgress()
            try {
                val phoneNumber = _state.value.phoneNumber ?: return@launch
                when (val result = phoneNumberVerificationRequestCodeUseCase.execute(phoneNumber)) {
                    PhoneNumberVerificationRequestCodeUseCase.Result.Success -> {
                        analytics.reportEvent(MobileAuthEvent.EatersRequestVerificationCodeSuccessEvent())
                    }
                    is PhoneNumberVerificationRequestCodeUseCase.Result.InvalidPhone -> {
                        analytics.reportEvent(
                            MobileAuthEvent.EatersRequestVerificationCodeErrorEvent(
                                error_code = -1,
                                error_description = "Invalid Phone"
                            )
                        )
                        _events.emit(EditProfileCodeEvents.InvalidPhoneNumber)
                    }
                    is PhoneNumberVerificationRequestCodeUseCase.Result.OtherError -> {
                        analytics.reportEvent(
                            MobileAuthEvent.EatersRequestVerificationCodeErrorEvent(
                                error_code = result.type.ordinal,
                                error_description = result.errorMessage
                            )
                        )
                        _events.emit(EditProfileCodeEvents.SendPhoneNumberGeneralError)
                    }
                }
            } finally {
                endProgress()
            }
        }
    }

    fun onSendValidateCodeClicked() {
        analytics.reportEvent(MobileAuthEvent.EatersPhoneVerificationScreenValidateClickedEvent())
        viewModelScope.launch {
            val code = _state.value.verificationCode ?: run {
                _events.emit(EditProfileCodeEvents.EmptyCodeError)
                return@launch
            }
            val phoneNumber = _state.value.phoneNumber ?: return@launch

            startProgress()
            try {
                when (val result = sendPhoneVerificationUseCase.execute(phoneNumber, code)) {
                    SendPhoneVerificationUseCase.Result.Success -> {
                        analytics.reportEvent(MobileAuthEvent.EatersPhoneVerificationScreenValidateSuccessEvent())
                        _events.emit(EditProfileCodeEvents.CodeValidationSuccess)
                    }
                    SendPhoneVerificationUseCase.Result.WrongPassword -> {
                        analytics.reportEvent(MobileAuthEvent.EatersPhoneVerificationScreenInvalidCodeEvent())
                        _events.emit(EditProfileCodeEvents.WrongPassword)
                    }
                    is SendPhoneVerificationUseCase.Result.OtherError -> {
                        analytics.reportEvent(
                            MobileAuthEvent.EatersPhoneVerificationScreenValidateErrorEvent(
                                result.type.ordinal,
                                result.errorMessage
                            )
                        )
                        _events.emit(EditProfileCodeEvents.ValidateCodeGeneralError)
                    }
                }
            } finally {
                endProgress()
            }
        }
    }

    private fun startProgress() {
        _state.update { it.copy(progress = true) }
    }

    private fun endProgress() {
        _state.update { it.copy(progress = false) }
    }
}
