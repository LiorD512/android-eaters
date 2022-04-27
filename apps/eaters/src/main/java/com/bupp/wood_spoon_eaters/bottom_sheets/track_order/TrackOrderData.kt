package com.bupp.wood_spoon_eaters.bottom_sheets.track_order

//Multi section view models

data class TrackOrderData<T>(val viewType: Int, val data: T, val areItemsTheSame: Boolean = true)
