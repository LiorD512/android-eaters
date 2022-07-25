package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterAdapterSectionModel(
    val sectionName: String,
    val isSelected: Boolean = false
): Parcelable