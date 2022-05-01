package com.bupp.wood_spoon_chef.presentation.features.main.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotSlim
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.extensions.monthOfYearAsShortText
import com.bupp.wood_spoon_chef.utils.extensions.prepareRangeOneMonth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.joda.time.DateTime
import timber.log.Timber
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class CalendarViewModel(
    private val userRepository: UserRepository,
    private val cookingSlotRepository: CookingSlotRepository
) : BaseViewModel() {

    private val _selectedDateFlow = userRepository.getLastSelectedCalendarDateFlow()
    val selectedDateFlow: StateFlow<Long> = _selectedDateFlow

    val calendarEventsLaveData: MutableLiveData<MutableMap<String, List<CookingSlotSlim>>> =
        MutableLiveData(hashMapOf())

    fun setSelectedDate(date: Date) {
        viewModelScope.launch {
            userRepository.setMemorySelectedCalendarDate(DateTime(date))
        }
    }

    fun fetchCookingSlotsPaging(
        firstDayOfMonth: Date,
        showProgress: Boolean = false
    ) = viewModelScope.launch(SupervisorJob() + Dispatchers.IO) {
        if (showProgress) {
            progressData.startProgress()
        }

        val beforeMonthSelectedSlots = async {
            val dateTimeBeforeMonth = DateTime(firstDayOfMonth).minusMonths(1)
            dateTimeBeforeMonth.prepareRangeOneMonth().let { pair ->
                fetchCookingSlotsIfNotCashed(dateTimeBeforeMonth.toDate()) {
                    cookingSlotRepository.getCookingSlotByTimePeriod(pair.first, pair.second)
                }
            }
        }

        val selectedMonthSlots = async {
            DateTime(firstDayOfMonth).prepareRangeOneMonth().let { pair ->
                fetchCookingSlotsIfNotCashed(firstDayOfMonth) {
                    cookingSlotRepository.getCookingSlotByTimePeriod(pair.first, pair.second)
                }
            }
        }

        val afterMonthSelectedSlots = async {
            val dateTimeBeforeMonth = DateTime(firstDayOfMonth).plusMonths(1)
            dateTimeBeforeMonth.prepareRangeOneMonth().let { pair ->
                fetchCookingSlotsIfNotCashed(dateTimeBeforeMonth.toDate()) {
                    cookingSlotRepository.getCookingSlotByTimePeriod(pair.first, pair.second)
                }
            }
        }

        try {
            when (val currentMonthResponse = selectedMonthSlots.await()) {
                is ResponseSuccess -> {
                    doOnSuccessFetchingSlots(currentMonthResponse, firstDayOfMonth)
                }
                is ResponseError -> {
                    doOnError(currentMonthResponse)
                }
            }

            awaitAll(
                beforeMonthSelectedSlots,
                afterMonthSelectedSlots
            ).map { responseResult ->
                when (responseResult) {
                    is ResponseSuccess -> {
                        doOnSuccessFetchingPagingSlots(responseResult)
                    }
                    is ResponseError -> {
                        doOnError(responseResult)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            progressData.endProgress()
        }
    }

    private fun doOnSuccessFetchingPagingSlots(
        responseResult: ResponseSuccess<List<CookingSlotSlim>>
    ) = responseResult.data?.let { list ->
        val value = calendarEventsLaveData.value?.toMutableMap()?.apply {
            put(
                DateTime(list.map { it.startsAt }.first()).monthOfYearAsShortText(),
                list
            )
        }?.toMutableMap()

        calendarEventsLaveData.value = value
    }

    private fun doOnError(responseResult: ResponseError<List<CookingSlotSlim>>) {
        errorEvent.postRawValue(responseResult.error)
    }

    private fun doOnSuccessFetchingSlots(
        currentMonthResponse: ResponseSuccess<List<CookingSlotSlim>>,
        firstDayOfMonth: Date
    ) = currentMonthResponse.data?.let { list ->
        val value = calendarEventsLaveData.value?.toMutableMap()?.apply {
            put(
                DateTime(firstDayOfMonth).monthOfYearAsShortText(),
                list
            )
        }?.toMutableMap()

        calendarEventsLaveData.postValue(value)
    }

    private suspend fun fetchCookingSlotsIfNotCashed(
        firstDayOfMonth: Date,
        block: suspend (Date) -> ResponseResult<List<CookingSlotSlim>>
    ): ResponseResult<List<CookingSlotSlim>> {
        val isMonthAlreadyFetched = calendarEventsLaveData.value?.keys?.contains(
            DateTime(firstDayOfMonth).monthOfYearAsShortText()
        ) ?: false

        return if (isMonthAlreadyFetched.not()) {
            block(firstDayOfMonth)
        } else {
            ResponseSuccess(
                calendarEventsLaveData.value?.get(DateTime(firstDayOfMonth).monthOfYearAsShortText())
            )
        }
    }
}