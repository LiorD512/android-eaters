package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen

import com.bupp.wood_spoon_eaters.model.*
import java.util.*
import kotlin.collections.ArrayList

//Multi section view models

data class OrderTrackMapData(val order: Order, val orderUserInfo: OrderUserInfo?)

data class OrderTrackDetailsHeader(val orderNumber: String)

data class OrderUserInfo(val paymentMethod: String? = null, val userInfo: String? = null, val userLocation: Address? = null)
data class OrderTrackDetails(val order: Order, val orderUserInfo: OrderUserInfo?)

data class OrderTrackProgress(val order: Order?)
data class OrderTrackBottom(val dishes: ArrayList<Dish>)

