package com.bupp.wood_spoon_eaters.features.create_profile

import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.repositories.UserRepository

class PhoneNumberVerificationRequestCodeUseCase(
    private val userRepository: UserRepository,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) {

    sealed class Result {
        object Success : Result()
        data class InvalidPhone(val phone: String) : Result()
        data class OtherError(val type: UserRepository.UserRepoStatus, val errorMessage: String?) : Result()
    }

    suspend fun execute(phoneNumber: String): Result {
        val result = userRepository.sendPhoneVerification(phone = phoneNumber)

        return when (result.type) {
            UserRepository.UserRepoStatus.SUCCESS -> {
                Result.Success
            }
            UserRepository.UserRepoStatus.INVALID_PHONE -> {
                Result.InvalidPhone(phoneNumber)
            }
            UserRepository.UserRepoStatus.SERVER_ERROR,
            UserRepository.UserRepoStatus.LOGGED_OUT,
            UserRepository.UserRepoStatus.WRONG_PASSWORD,
            UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG,
            UserRepository.UserRepoStatus.WS_ERROR -> {
                Result.OtherError(result.type, result.errorMessage)
            }
        }
    }

}