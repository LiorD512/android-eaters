package com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.data.remote.network.base.MTError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.domain.CancelCookingSlotUseCase
import com.bupp.wood_spoon_chef.domain.FetchCookingSlotByIdUseCase
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.Exception

sealed class CookingSlotDetailsState {
    object Idle : CookingSlotDetailsState()
    object Loading : CookingSlotDetailsState()
    object SlotCanceled : CookingSlotDetailsState()
    class Success(val cookingSlot: CookingSlot) : CookingSlotDetailsState()
    class Error(val error: MTError) : CookingSlotDetailsState()
}

class CookingSlotDetailsViewModelNew(
    private val fetchCookingSlotByIdUseCase: FetchCookingSlotByIdUseCase,
    private val cancelCookingSlotUseCase: CancelCookingSlotUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow<CookingSlotDetailsState>(CookingSlotDetailsState.Idle)
    val state: StateFlow<CookingSlotDetailsState> = _state.asStateFlow()

    fun fetchCookingSlotById(slotId: Long) = viewModelScope.launch {
        _state.emit(CookingSlotDetailsState.Loading)

        val params = FetchCookingSlotByIdUseCase.Params(cookingSlotId = slotId)
        fetchCookingSlotByIdUseCase.execute(params).collect { result ->
            when (result) {
                is ResponseSuccess -> {
                    result.data?.let {
                        _state.emit(CookingSlotDetailsState.Success(cookingSlot = it))
                    }
                }
                is ResponseError -> {
                    result.error.let {
                        _state.emit(CookingSlotDetailsState.Error(it))
                    }
                }
            }
        }
    }

    fun cancelCookingSlot(slotId: Long) = viewModelScope.launch {
        _state.emit(CookingSlotDetailsState.Loading)

        try {
            val params = CancelCookingSlotUseCase.Params(cookingSlotId = slotId)
            cancelCookingSlotUseCase.execute(params)
                .collect { result ->
                when (result) {
                    is ResponseSuccess -> {
                        _state.emit(CookingSlotDetailsState.SlotCanceled)
                    }
                    is ResponseError -> {
                        result.error.let {
                            _state.emit(CookingSlotDetailsState.Error(it))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let {
                _state.emit(CookingSlotDetailsState.Error(error = CustomError(it)))
            }
        }
    }

}
