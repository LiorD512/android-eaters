package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen

//Multi section view models

data class TrackOrderData<T>(val viewType: Int, val data: T, val areItemsTheSame: Boolean = true)

