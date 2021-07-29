package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models

import com.bupp.wood_spoon_eaters.model.CookingSlot
import java.util.*

data class DeliveryDate(val date: Date, val cookingSlots: MutableList<CookingSlot>)