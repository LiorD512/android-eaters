package com.bupp.wood_spoon_eaters.bottom_sheets.time_picker

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import java.util.*


class TimePickerViewModel(
    private val feedDataManager: FeedDataManager,
) : ViewModel() {

    fun getSelectedData(): Date?{
        return feedDataManager.getCurrentDeliveryDate()
    }

    private fun getEventData(time: Date): Map<String, String> {
        return mutableMapOf("delivery_time" to time.toString())
    }

    companion object{
        const val TAG = "wowTimePickerVM"
    }
}