package com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.bupp.wood_spoon_chef.utils.extensions.prepareRangeOneDay
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.lang.Exception

class CookingSlotDetailsViewModel(
    private val userRepository: UserRepository,
    private val cookingSlotRepository: CookingSlotRepository
) : BaseViewModel() {

    val allCookingSlotLiveData: MutableLiveData<MutableList<CookingSlot>> = MutableLiveData()

    fun fetchCookingSlotsByDate(selectedCookingSlitsDate: DateTime) =
        viewModelScope.launch {
            progressData.startProgress()

            try {
                val startEndDate = selectedCookingSlitsDate.prepareRangeOneDay()

                when (val result = cookingSlotRepository.getCookingSlotByTimePeriod(
                    startTimeSeconds = startEndDate.first,
                    endTimeSeconds = startEndDate.second
                )) {
                    is ResponseSuccess -> {
                        result.data?.let { list ->
                            fetchCookingSlotsById(list.map { it.id }.toList())
                        }
                    }
                    is ResponseError -> {
                        result.error.message?.let { errorEvent.postRawValue(CustomError(it)) }
                    }
                }

            } catch (e: Exception) {
                e.message?.let { errorEvent.postRawValue(CustomError(it)) }
            }

            progressData.endProgress()
        }

    private fun fetchCookingSlotsById(cookingSlotsId: List<Long>) = viewModelScope.launch {
        try {
            val result = cookingSlotsId
                .map { cookingSlotId ->
                    async { cookingSlotRepository.getCookingSlotById(cookingSlotId) }
                }.awaitAll()
                .filterIsInstance<ResponseSuccess<CookingSlot>>()
                .mapNotNull { it.data }

            allCookingSlotLiveData.value = (result as MutableList<CookingSlot>?)
        } catch (e: Exception) {
            e.message?.let { errorEvent.postRawValue(CustomError(it)) }
        }
    }

    fun getShareText(cookingSlot: CookingSlot): String {
        val inviteUrl = userRepository.getCurrentChef()?.inviteUrl
        val text = "Hey I'm cooking on: ${DateUtils.getCookingSlotStartString(cookingSlot)}\n" +
                "Download WoodSpoon and you can order my delicious dishes: \n"
        return "$text \n $inviteUrl"
    }

    fun cancelCookingSlot(cookingSlotId: Long) = viewModelScope.launch {
        progressData.startProgress()

        try {
            when (val result = cookingSlotRepository.cancelCookingSlot(cookingSlotId)) {
                is ResponseSuccess -> {
                    doOnSuccessCancelSlot(cookingSlotId)
                }
                is ResponseError -> {
                    doOnError(result)
                }
            }
        } catch (e: Exception) {
            e.message?.let { errorEvent.postRawValue(CustomError(it)) }
        }

        progressData.endProgress()
    }


    private fun doOnError(result: ResponseError<Any>) {
        errorEvent.postRawValue(result.error)
    }

    private fun doOnSuccessCancelSlot(cookingSlotId: Long) {
        val list = allCookingSlotLiveData.value
        list?.filterNot { it.id == cookingSlotId }.also {
            allCookingSlotLiveData.postValue(it as MutableList<CookingSlot>?)
        }
    }

}