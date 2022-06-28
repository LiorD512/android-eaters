package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDateForHours
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class CreateCookingSlotNewViewModel(
    private val cookingSlotFlowCoordinator: CookingSlotFlowCoordinator
) : BaseViewModel() {

    private var startTime: Long? = null
    private var endTime: Long? = null

    fun openMenuFragment() {
        viewModelScope.launch {
            cookingSlotFlowCoordinator.next(CookingSlotFlowCoordinator.Step.OPEN_MENU_FRAGMENT)
        }
    }

    fun setStartTime(startTime: Long) {
        this.startTime = startTime
    }

    fun setEndTime(endTime: Long) {
        this.endTime = endTime
    }

    fun getOperatingHours(): String {
        return "${DateTime(startTime).prepareFormattedDateForHours()} - ${DateTime(endTime).prepareFormattedDateForHours()}"
    }

}