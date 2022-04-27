package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen

import com.bupp.wood_spoon_eaters.experiments.PricingExperimentParams
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Order

//Multi section view models

data class OrderTrackHeader(val orderNumber: String?)

data class OrderUserInfo(val paymentMethod: String? = null, val userInfo: String? = null, val userLocation: Address? = null, val note: String? = null)
data class OrderTrackDetails(val order: Order, val orderUserInfo: OrderUserInfo?, val pricingExperimentParams: PricingExperimentParams)

data class OrderTrackProgress(val order: Order?)

