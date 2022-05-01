package com.bupp.wood_spoon_chef.presentation.features.main.my_dishes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.data.remote.model.DishStatus
import com.bupp.wood_spoon_chef.data.remote.model.DishUpdatedDialogStatus
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.DishRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.UserSettings
import kotlinx.coroutines.launch

class MyDishesViewModel(
    val settings: UserSettings,
    private val userRepository: UserRepository,
    private val dishRepository: DishRepository
) : BaseViewModel() {

    private var currentSelectedDish: Dish? = null

    val dishUpdateEvent = LiveEventData<NewDishDoneDialogData>()
    val editDishEvent = LiveEventData<Long?>()

    val getDishesEvent: MutableLiveData<List<Dish>?> = MutableLiveData()
    val filterDishesEvent: MutableLiveData<List<Dish>?> = MutableLiveData()
    fun getMyDishes() {
        progressData.startProgress()
        viewModelScope.launch {
            when (val result = dishRepository.getMyDishes()) {
                is ResponseSuccess -> {
                    getDishesEvent.postValue(result.data)
                }
                is ResponseError -> {
                    errorEvent.postRawValue(result.error)
                }
            }
        }
        progressData.endProgress()
    }


    fun filterList(input: String) {
        filterDishesEvent.postValue(dishRepository.filterDishes(input))
    }

    fun getGreetingsText(): String {
        val name = userRepository.getUserData().value?.firstName
        return "Hey $name"
    }

    //Menu methods
    data class ItemMenuData(val status: DishStatus?)

    val itemMenuEvent = LiveEventData<ItemMenuData>()
    fun onItemMenuClick(selectedDish: Dish?) {
        this.currentSelectedDish = selectedDish
        itemMenuEvent.postRawValue(ItemMenuData(selectedDish?.status))
    }

    fun onEditDishClick() {
        currentSelectedDish?.let {
            editDishEvent.postRawValue(it.id)
        }
    }

    fun publishDish() {
        currentSelectedDish?.id?.let {
            progressData.startProgress()
            viewModelScope.launch {
                when (val response = dishRepository.publishDish(it)) {
                    is ResponseSuccess -> {
                        dishUpdateEvent.postRawValue(getDoneDialogData(DishUpdatedDialogStatus.DRAFT_PUBLISH))
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun unPublishDish() {
        currentSelectedDish?.id?.let {
            progressData.startProgress()
            viewModelScope.launch {
                when (val response = dishRepository.unPublishDish(it)) {
                    is ResponseSuccess -> {
                        dishUpdateEvent.postRawValue(getDoneDialogData(DishUpdatedDialogStatus.UNPUBLISH))
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun duplicateDish() {
        currentSelectedDish?.id?.let {
            progressData.startProgress()
            viewModelScope.launch {
                when (val response = dishRepository.duplicateDish(it)) {
                    is ResponseSuccess -> {
                        dishUpdateEvent.postRawValue(getDoneDialogData(DishUpdatedDialogStatus.DUPLICATE))
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun hideDish() {
        currentSelectedDish?.id?.let {
            viewModelScope.launch {
                progressData.startProgress()
                when (val response = dishRepository.hideDish(it)) {
                    is ResponseSuccess -> {
                        dishUpdateEvent.postRawValue(getDoneDialogData(DishUpdatedDialogStatus.HIDE))
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }


    data class NewDishDoneDialogData(val title: String, val body: String? = null)

    private fun getDoneDialogData(status: DishUpdatedDialogStatus): NewDishDoneDialogData {
        return when (status) {
            DishUpdatedDialogStatus.DRAFT_PUBLISH -> {
                NewDishDoneDialogData(title = "Dish published successfully")
            }
            DishUpdatedDialogStatus.UNPUBLISH -> {
                NewDishDoneDialogData(title = "Dish unpublished successfully")
            }
            DishUpdatedDialogStatus.HIDE -> {
                NewDishDoneDialogData(title = "Dish removed successfully")
            }
            DishUpdatedDialogStatus.DUPLICATE -> {
                NewDishDoneDialogData(title = "Dish duplicated successfully")
            }
            else -> {
                NewDishDoneDialogData(title = "Dish updated successfully")
            }
        }
    }

    companion object {
        const val TAG = "wowMyDishesVM"
    }

}