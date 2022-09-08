package com.bupp.wood_spoon_eaters.features.create_profile

import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.eatwoodspoon.analytics.AnalyticsEventReporter
import com.eatwoodspoon.analytics.events.MobileAuthEvent

class SendPhoneVerificationUseCase(
    private val userRepository: UserRepository,
    private val analytics: AnalyticsEventReporter
) {

    sealed class Result {
        object Success : Result()
        object WrongPassword : Result()
        data class OtherError(val type: UserRepository.UserRepoStatus, val errorMessage: String?) :
            Result()
    }

    suspend fun execute(phoneNumber: String, code: String): Result {

        val initialUserId = userRepository.currentEaterFlow.value?.id

        val result = userRepository.sendCodeAndPhoneVerification(phoneNumber, code)
        return when (result.type) {
            UserRepository.UserRepoStatus.SUCCESS -> {
                val onSuccessUserId = userRepository.currentEaterFlow.value?.id
                if (onSuccessUserId != initialUserId) {
                    analytics.reportEvent(
                        MobileAuthEvent.EatersAccountMergedEvent(
                            from_user_id = "$initialUserId",
                            to_user_id = "$onSuccessUserId",
                        )
                    )
                }
                Result.Success
            }
            UserRepository.UserRepoStatus.WRONG_PASSWORD -> {
                Result.WrongPassword
            }
            UserRepository.UserRepoStatus.SERVER_ERROR,
            UserRepository.UserRepoStatus.LOGGED_OUT,
            UserRepository.UserRepoStatus.INVALID_PHONE,
            UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG,
            UserRepository.UserRepoStatus.WS_ERROR -> {
                Result.OtherError(result.type, result.errorMessage)
            }
        }
    }
}