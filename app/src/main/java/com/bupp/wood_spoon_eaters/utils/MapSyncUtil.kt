package com.bupp.wood_spoon_eaters.utils

import com.bupp.wood_spoon_eaters.model.Order


class MapSyncUtil {

    fun getMapImage(order: Order): String {
        val lat = order.deliveryAddress?.lat
        val lng = order.deliveryAddress?.lng
        val url = "http://maps.google.com/maps/api/staticmap?center=$lat,$lng&zoom=17&" +
                "size=600x400&" +
                "sensor=false&" +
                "markers=icon:https://res.cloudinary.com/woodspoon/image/upload/c_scale,h_64/uploads/app/assets/map_pin.png|$lat,$lng&" +
                "key=AIzaSyCowuTI2_0q8rpGYlqueBX6nbk2kSjjitU"
        return url
    }
}