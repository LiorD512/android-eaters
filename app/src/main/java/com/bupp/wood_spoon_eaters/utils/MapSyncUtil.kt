package com.bupp.wood_spoon_eaters.utils

import android.util.Log
import com.bupp.wood_spoon_eaters.model.Order
import com.google.android.gms.maps.model.LatLng
import kotlin.math.ln
import kotlin.math.sin


class MapSyncUtil {

    fun getMapImage(order: Order, width: Int = 600, height: Int = 400): String {
        var centerLatLng: LatLng? = null

        val eaterLat = order.deliveryAddress?.lat ?: 0.0
        val eaterLng = order.deliveryAddress?.lng ?: 0.0
        val eaterLatLng = LatLng(eaterLat, eaterLng)

        val chefLat = order.restaurant?.pickupAddress?.lat ?: 0.0
        val chefLng = order.restaurant?.pickupAddress?.lng ?: 0.0
        val chefLatLng = LatLng(chefLat, chefLng)

        var courierLatLng: LatLng? = null
        if(order.courier != null){
            val courierLat = order.courier.lat ?: 0.0
            val courierLng = order.courier.lng ?: 0.0
            courierLatLng = LatLng(courierLat, courierLng)
            centerLatLng = midPoint(eaterLatLng, courierLatLng)
        }else{
            centerLatLng = midPoint(eaterLatLng, chefLatLng)
        }


        var url = "http://maps.google.com/maps/api/staticmap?center=${centerLatLng.latitude},${centerLatLng.longitude}&zoom=13&" +
                "size=${width}x${height}&" +
                "sensor=false&" +
                "markers=scale:2|icon:https://res.cloudinary.com/woodspoon/image/upload/c_scale,h_64/uploads/app/assets/map_pin.png|$eaterLat,$eaterLng&" +
                "markers=scale:2|icon:https://res.cloudinary.com/woodspoon/image/upload/uploads/app/assets/chef.png|$chefLat,$chefLng&"

        order.courier?.let {
            val courierLat = it.lat
            val courierLng = it.lng
            url += "markers=scale:2|icon:https://res.cloudinary.com/woodspoon/image/upload/uploads/app/assets/courier.png|$courierLat,$courierLng&"
        }

        url += "key=AIzaSyCowuTI2_0q8rpGYlqueBX6nbk2kSjjitU"

        Log.d("wowMapSyncUtil","url: $url")
        return url
    }

    fun midPoint(firstLatLng: LatLng, secondLatLng: LatLng): LatLng {
        var lat1 = firstLatLng.latitude
        var lon1 = firstLatLng.longitude
        var lat2 = secondLatLng.latitude
        val dLon = Math.toRadians(secondLatLng.longitude - lon1)

        //convert to radians
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)
        lon1 = Math.toRadians(lon1)
        val Bx = Math.cos(lat2) * Math.cos(dLon)
        val By = Math.cos(lat2) * Math.sin(dLon)
        val lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By))
        val lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx)

        //print out in degrees
        return LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3))
    }

    val GLOBE_WIDTH = 256 // a constant in Google's map projection
    val ZOOM_MAX = 21

    fun getBoundsZoomLevel(
        northeast: LatLng, southwest: LatLng,
        width: Int, height: Int
    ): Int {
        val latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI
        val lngDiff = northeast.longitude - southwest.longitude
        val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360
        val latZoom = zoom(height.toDouble(), GLOBE_WIDTH.toDouble(), latFraction)
        val lngZoom = zoom(width.toDouble(), GLOBE_WIDTH.toDouble(), lngFraction)
        val zoom = latZoom.coerceAtMost(lngZoom).coerceAtMost(ZOOM_MAX.toDouble())
        return zoom.toInt()
    }

    private fun latRad(lat: Double): Double {
        val sin = sin(lat * Math.PI / 180)
        val radX2 = ln((1 + sin) / (1 - sin)) / 2
        return radX2.coerceAtMost(Math.PI).coerceAtLeast(-Math.PI) / 2
    }

    private fun zoom(mapPx: Double, worldPx: Double, fraction: Double): Double {
        val LN2 = .693147180559945309417
        return ln(mapPx / worldPx / fraction) / LN2
    }
}