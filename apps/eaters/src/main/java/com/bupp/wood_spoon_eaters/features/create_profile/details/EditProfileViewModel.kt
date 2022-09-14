package com.bupp.wood_spoon_eaters.features.create_profile.details

import android.net.Uri
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.managers.MediaUploadManager
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.utils.CountryCodeUtils
import com.eatwoodspoon.analytics.AnalyticsEventReporter
import com.eatwoodspoon.analytics.events.MobileContactDetailsEvent
import com.eatwoodspoon.android_utils.flows.toImmutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileState(
    val user: User? = null,
    val photoSectionEnabled: Boolean = false,
    val progress: Boolean = false,
) {
    data class User(
        val firstName: String? = null,
        val lastName: String? = null,
        val email: String? = null,
        val canEditEmail: Boolean = true,
        val phoneNumber: String? = null,
        val canEditPhoneNumber: Boolean = true,
        val thumbnail: String? = null,
    )


}

sealed class EditProfileEvents {
    data class NavigateToPhoneValidation(val phoneNumber: String) : EditProfileEvents()
    object NavigateEditProfileDone : EditProfileEvents()
    data class InputError(val field: Field, val message: String?) : EditProfileEvents() {
        enum class Field {
            @Keep
            FirstName, LastName, Email, PhoneNumber;

            fun withMessage(message: String?) = InputError(field = this, message = message)
        }
    }
}

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val eaterDataManager: EaterDataManager,
    private val mediaUploadManager: MediaUploadManager,
    private val analytics: AnalyticsEventReporter,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.toImmutable()
    private val _events = MutableSharedFlow<EditProfileEvents>()
    val events = _events.toImmutable()

    private var pendingThumbnail: Uri? = null
    private var pendingThumbnailPreSignedUrl: String? = null

    private val originalUserData: EditProfileState.User? =
        eaterDataManager.currentEater?.mapToStateUser()

    init {
        _state.update {
            it.copy(user = originalUserData)
        }
        analytics.reportEvent(
            MobileContactDetailsEvent.ScreenOpenedEvent(
                source = "checkout", //TODO provide from outside for future usage
                has_email = !originalUserData?.email.isNullOrEmpty(),
                email_verified = originalUserData?.canEditEmail != true,
                has_phone = !originalUserData?.phoneNumber.isNullOrEmpty(),
                phone_verified = originalUserData?.canEditPhoneNumber != true
            )
        )
    }

    private fun startProgress() {
        _state.update { it.copy(progress = true) }
    }

    private fun endProgress() {
        _state.update { it.copy(progress = false) }
    }

    private suspend fun uploadMediaIdNeeded() = true
//    private suspend fun uploadMediaIdNeeded(): Boolean {
//        val thumbnailToUpload = pendingThumbnail ?: return true
//        val mediaUploadRequests = listOf(thumbnailToUpload)
//
//        return try {
//            val results = mediaUploadManager.upload(mediaUploadRequests)
//            pendingThumbnailPreSignedUrl = results.firstOrNull()?.preSignedUrlKey
//            pendingThumbnail = null
//            true
//        } catch (ex: Exception) {
//            // TODO post error
//            false
//        }
//    }

    private suspend fun validateFields(
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?
    ): Boolean {
        val errors = mutableListOf<EditProfileEvents.InputError>().apply {
            if (firstName.isNullOrEmpty()) {
                add(EditProfileEvents.InputError.Field.FirstName.withMessage("First name can't be empty"))
            }

            if (lastName.isNullOrEmpty()) {
                add(EditProfileEvents.InputError.Field.LastName.withMessage("Last name can't be empty"))
            }

            if (email.isNullOrEmpty()) {
                add(EditProfileEvents.InputError.Field.Email.withMessage("Email can't be empty"))
            } else if (!isValidEmailAddress(email)) {
                add(EditProfileEvents.InputError.Field.Email.withMessage("Not a valid email"))
            }
            if (phoneNumber.isNullOrEmpty() || phoneNumber.length <= 4) {
                add(EditProfileEvents.InputError.Field.PhoneNumber.withMessage("Phone number can't be empty"))
            } else if (!CountryCodeUtils.isPhoneValid(phoneNumber)) {
                add(EditProfileEvents.InputError.Field.PhoneNumber.withMessage("Phone number is not valid"))
            }

        }.toList()
        errors.firstOrNull()?.let {
            _events.emit(it)
            analytics.reportEvent(
                MobileContactDetailsEvent.LocalValidationErrorEvent(
                    errors = it.message ?: it.field.name
                )
            )
        }
        return errors.isEmpty()
    }

    private fun phoneValidationRequired(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrEmpty()) {
            return false
        }
        return originalUserData?.canEditPhoneNumber == true
    }

    fun onSaveUserClicked(
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?
    ) {
        analytics.reportEvent(MobileContactDetailsEvent.SaveClickedEvent())
        viewModelScope.launch {
            if (!validateFields(firstName, lastName, email, phoneNumber)) {
                return@launch
            }
            updateUserState(firstName = firstName, lastName = lastName, email = email, phoneNumber = phoneNumber)

            startProgress()
            launch(Dispatchers.IO) {
                try {
                    if (!uploadMediaIdNeeded()) {
                        return@launch
                    }
                    sendUpdateEaterRequest()
                } finally {
                    endProgress()
                }
            }
        }
    }

    private suspend fun navigateOnSuccess(phoneNumber: String?) {
        if (phoneNumber != null && phoneValidationRequired(phoneNumber)) {
            analytics.reportEvent(
                MobileContactDetailsEvent.SaveSuccessEvent(
                    phone_verification_required = true
                )
            )
            _events.emit(EditProfileEvents.NavigateToPhoneValidation(phoneNumber))
        } else {
            analytics.reportEvent(
                MobileContactDetailsEvent.SaveSuccessEvent(
                    phone_verification_required = false
                )
            )
            _events.emit(EditProfileEvents.NavigateEditProfileDone)
        }
    }

    private fun updateUserState(firstName: String?, lastName: String?, email: String?, phoneNumber: String?) =
        _state.update { profileState ->
            val currentUserValue = (profileState.user ?: EditProfileState.User())
            val updatedUserValue = currentUserValue
                .copy(firstName = firstName, lastName = lastName)
                .let {
                    if (currentUserValue.canEditEmail) {
                        it.copy(email = email)
                    } else {
                        it
                    }
                }
                .let {
                    if (currentUserValue.canEditPhoneNumber) {
                        it.copy(phoneNumber = phoneNumber)
                    } else {
                        it
                    }
                }
            profileState.copy(user = updatedUserValue)
        }

    private suspend fun sendUpdateEaterRequest() {
        val eaterRequest =
            state.value.user?.mapTopEaterRequest()?.copy(thumbnail = pendingThumbnailPreSignedUrl)
                ?: return

        // Need to keep phoneNumber here to prevent overwriting after save
        val phoneNumber = _state.value.user?.phoneNumber

        val userRepoResult = userRepository.updateEater(eaterRequest)
        when (userRepoResult.type) {
            UserRepository.UserRepoStatus.SUCCESS -> {
                _state.update { it.copy(user = eaterDataManager.currentEater?.mapToStateUser()) }
                navigateOnSuccess(phoneNumber)
            }
            else -> {
                analytics.reportEvent(
                    MobileContactDetailsEvent.SaveErrorEvent(
                        userRepoResult.type.ordinal,
                        userRepoResult.errorMessage
                    )
                )
            }
        }
    }
}

private fun Eater.mapToStateUser() = EditProfileState.User(
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    canEditPhoneNumber = phoneNumberVerified != true || phoneNumber.isNullOrEmpty(),
    email = email,
    canEditEmail = phoneNumberVerified != true || email.isNullOrEmpty(),
    thumbnail = thumbnail
)

private fun EditProfileState.User.mapTopEaterRequest() = EaterRequest(
    firstName = firstName,
    lastName = lastName,
    email = email,
    thumbnail = thumbnail
)

private fun isValidEmailAddress(email: String): Boolean {
    val ePattern =
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
    val p = java.util.regex.Pattern.compile(ePattern)
    val m = p.matcher(email)
    return m.matches()
}