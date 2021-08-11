package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtrasDishPage(
    val menuItem: MenuItem,
    val currentSelectedDate: Long? = null,
    val availability: AvailabilityDate? = null
) : Parcelable
