package com.bupp.wood_spoon_chef.presentation.features.main.calendar.create_cooking_slot

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter.MenuAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter.MenuAdapterViewType
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.data.remote.model.MenuItemRequest
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.DishRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class CreateCookingSlotViewModel(
    private val dishRepository: DishRepository,
    private val cookingSlotRepository: CookingSlotRepository
) : BaseViewModel() {

    private var eventId: Long? = null
    private var editableCookingSlotId: Long? = null

    val getDishesLiveData: MutableLiveData<List<MenuAdapterModel>> = MutableLiveData()
    val savedCookingSlotLiveData: MutableLiveData<CookingSlot> = MutableLiveData()

    val editingCookingSlotLiveData: MutableLiveData<CookingSlot> = MutableLiveData()

    fun getMyDishes() = viewModelScope.launch(Dispatchers.IO) {
        progressData.startProgress()

        try {
            when (val result = dishRepository.getMyDishes()) {
                is ResponseSuccess -> {
                    val dishes = result.data?.filter { it.isActive() }

                    dishes?.let {
                        val menuAdapterItems = parseDataForAdapter(dishes)
                        getDishesLiveData.postValue(menuAdapterItems)
                    }
                }
                is ResponseError -> {
                    errorEvent.postRawValue(result.error)
                }
            }
        } catch (e: Exception) {
            e.message?.let { errorEvent.postRawValue(CustomError(it)) }
        }

        progressData.endProgress()
    }

    fun fetchCookingSlotById(cookingSlotId: Long) = viewModelScope.launch(Dispatchers.IO) {
        progressData.startProgress()

        editableCookingSlotId = cookingSlotId
        try {
            when (val result = cookingSlotRepository.getCookingSlotById(cookingSlotId)) {
                is ResponseSuccess -> {
                    editingCookingSlotLiveData.postValue(result.data)
                }
                is ResponseError -> {
                    errorEvent.postRawValue(result.error)
                }
            }
        } catch (e: Exception) {
            e.message?.let {
                errorEvent.postRawValue(CustomError(it))
            }
            Timber.e(e)
        }

        progressData.endProgress()
    }

    private fun parseDataForAdapter(dishes: List<Dish>): List<MenuAdapterModel> {
        val menuAdapterItems: MutableList<MenuAdapterModel> = mutableListOf()
        menuAdapterItems.add(MenuAdapterModel(type = MenuAdapterViewType.HEADER))
        dishes.forEach { dish ->
            menuAdapterItems.add(
                MenuAdapterModel(
                    type = MenuAdapterViewType.ITEM,
                    dishId = dish.id,
                    img = dish.imageGallery?.first(),
                    name = dish.name
                )
            )
        }
        return menuAdapterItems
    }

    fun saveOrUpdateCookingSlot(
        startTime: Long?,
        endTime: Long?,
        lastCallTime: Long?,
        menuItems: List<MenuItemRequest>,
        freeDelivery: Boolean,
        worldwide: Boolean
    ) {
        val cookingSlotRequest = CookingSlotRequest()
        cookingSlotRequest.eventId = eventId
        cookingSlotRequest.startsAt = startTime
        cookingSlotRequest.endsAt = endTime
        cookingSlotRequest.lastCallAt = lastCallTime
        cookingSlotRequest.menuItems = menuItems.toMutableList()
        cookingSlotRequest.freeDelivery = freeDelivery
        cookingSlotRequest.worldwide = worldwide

        if (editableCookingSlotId != null) {
            updateCookingSlot(cookingSlotRequest)
        } else {
            saveCookingSlot(cookingSlotRequest)
        }
    }

    private fun saveCookingSlot(cookingSlotRequest: CookingSlotRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            progressData.startProgress()
            try {
                when (val result = cookingSlotRepository.postCookingSlot(cookingSlotRequest)) {
                    is ResponseSuccess -> {
                        savedCookingSlotLiveData.postValue(result.data)
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(result.error)
                    }
                }
            } catch (e: Exception) {
                e.message?.let { errorEvent.postRawValue(CustomError(it)) }
            }
            progressData.endProgress()
        }
    }

    private fun updateCookingSlot(cookingSlotRequest: CookingSlotRequest) {
        editableCookingSlotId?.let {
            viewModelScope.launch {
                progressData.startProgress()
                try {
                    when (val result =
                        cookingSlotRepository.updateCookingSlot(it, cookingSlotRequest)) {
                        is ResponseSuccess -> {
                            savedCookingSlotLiveData.postValue(result.data)
                        }
                        is ResponseError -> {
                            errorEvent.postRawValue(result.error)
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let { errorEvent.postRawValue(CustomError(it)) }
                }
                progressData.endProgress()
            }
        }
    }
}