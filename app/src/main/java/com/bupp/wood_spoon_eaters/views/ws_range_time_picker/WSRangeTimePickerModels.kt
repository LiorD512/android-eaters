package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.os.Parcelable
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

sealed class WSBaseTimePicker(
    var type: WSBaseTimePickerType?
) : Parcelable {
    abstract val title: String?
    abstract val date: Date?
}

enum class WSBaseTimePickerType{
    RANGE_TIME_PICKER,
    SINGLE_TIME_PICKER,
    COOKING_SLOT_TIME_PICKER,
}

@Parcelize
@JsonClass(generateAdapter = true)
data class WSRangeTimePickerModel(
    override val title: String? = null,
    override val date: Date,
    val hours: List<Date>
):Parcelable, WSBaseTimePicker(WSBaseTimePickerType.RANGE_TIME_PICKER)

@Parcelize
@JsonClass(generateAdapter = true)
data class WSSingleTimePicker(
    override val title: String? = null,
    override val date: Date,
):Parcelable, WSBaseTimePicker(WSBaseTimePickerType.SINGLE_TIME_PICKER)

@Parcelize
@JsonClass(generateAdapter = true)
data class WSCookingSlotTimePicker(
    val cookingSlot: CookingSlot,
    override val title: String? = null,
    override val date: Date? = null
):Parcelable, WSBaseTimePicker(WSBaseTimePickerType.COOKING_SLOT_TIME_PICKER)