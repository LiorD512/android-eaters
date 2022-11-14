package com.bupp.wood_spoon_eaters.features.main.feed.time_filter

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*


data class FeedTimeFilterState(
    val selectedTimeFilterBtn: SingleColumnTimePickerBottomSheet.DeliveryType? = SingleColumnTimePickerBottomSheet.DeliveryType.ANYTIME
)

class FeedTimeFilterViewModel(
    private val feedDataManager: FeedDataManager
): ViewModel() {

    private val _state = MutableStateFlow(FeedTimeFilterState())
    val state: StateFlow<FeedTimeFilterState> = _state

    fun onItemClicked(deliveryType: SingleColumnTimePickerBottomSheet.DeliveryType?){
        _state.update {
            it.copy(selectedTimeFilterBtn = deliveryType ?: SingleColumnTimePickerBottomSheet.DeliveryType.ANYTIME)
        }
    }

    fun getSelectedData(): Date?{
        return feedDataManager.getCurrentDeliveryDate()
    }
}