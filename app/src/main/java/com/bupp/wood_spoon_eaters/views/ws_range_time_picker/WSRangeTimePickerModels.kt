package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import com.squareup.moshi.JsonClass
import java.util.*

sealed class WSBaseTimePicker(){
    abstract val title: String?
    abstract val date: Date

}

@JsonClass(generateAdapter = true)
data class WSRangeTimePickerModel(
    override val title: String? = null,
    override val date: Date,
    val hours: List<Date>
): WSBaseTimePicker()

@JsonClass(generateAdapter = true)
data class WSSingleTimePicker(
    override val title: String? = null,
    override val date: Date,
): WSBaseTimePicker()