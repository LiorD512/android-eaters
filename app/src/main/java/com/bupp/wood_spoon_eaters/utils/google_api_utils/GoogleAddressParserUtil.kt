package com.bupp.wood_spoon_eaters.utils.google_api_utils

import android.location.Address
import android.util.Log
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.google.android.libraries.places.api.model.Place
import java.util.*


object GoogleAddressParserUtil {

    const val TAG = "wowGgleAddressParseUtil"

    private val allowed_types = setOf(
        "route", //street
        "country", //country
        "locality", //city
        "postal_code",
        "street_number",
        "sublocality_level_1", //district
        "administrative_area_level_1" //state
    )

    fun parsePlaceToAddressRequest(place: Place): AddressRequest {

        var addressRequest = AddressRequest()
        place.latLng?.let {
            addressRequest.lat = it.latitude
            addressRequest.lng = it.longitude
        }

        val addressComponents = place.addressComponents?.asList()
        addressComponents?.forEach {
            val data = it.types.intersect(allowed_types).toString().replace("[", "").replace("]", "")
            var result = data.split(",").map { it.trim() }
            result.forEach { data ->
                Log.d(TAG, "parseLocationToAddress: $data")
                if (data.isNotEmpty()) {
                    Log.d(TAG, "parseLocationToAddress: ${it.name}")
                    when (data) {
                        "route" -> {
                            addressRequest.streetLine1 = it.name
                        }
                        "country" -> {
                            addressRequest.countryIso = it.shortName
                        }
                        "sublocality_level_1", "sublocality", "locality" -> {
                            addressRequest.cityName = it.name
                        }
                        "postal_code" -> {
                            addressRequest.zipCode = it.name
                        }
                        "street_number" -> {
                            addressRequest.streetNumber = it.name
                        }
                        "administrative_area_level_1" -> {
                            addressRequest.stateIso = it.shortName
                        }
                    }
                }
            }
//            replaceNullFields(addressRequest)
        }
        return addressRequest
    }


    fun parseMyLocationToAddressRequest(location: Address): AddressRequest {
        val addressRequest = AddressRequest()

        addressRequest.lat = location.latitude
        addressRequest.lng = location.longitude
        addressRequest.streetNumber = location.featureName
        addressRequest.streetLine1 = location.thoroughfare
        location.subLocality?.let{
            addressRequest.cityName = it
        }
        location.locality?.let{
            addressRequest.cityName = it
        }
        location.adminArea?.let{
            addressRequest.stateIso = State.valueOfState(it)?.iso
        }
        addressRequest.countryIso = location.countryCode
        addressRequest.zipCode = location.postalCode
        addressRequest.addressSlug = location.getAddressLine(0)

        return addressRequest
    }


    enum class State(
        /**
         * The state's name.
         */
        val state: String,
        /**
         * The state's abbreviation.
         */
        val iso: String
    ) {
        ALABAMA("Alabama", "AL"), ALASKA("Alaska", "AK"), AMERICAN_SAMOA("American Samoa", "AS"), ARIZONA("Arizona", "AZ"), ARKANSAS(
            "Arkansas", "AR"
        ),
        CALIFORNIA("California", "CA"), COLORADO("Colorado", "CO"), CONNECTICUT("Connecticut", "CT"), DELAWARE(
            "Delaware", "DE"
        ),
        DISTRICT_OF_COLUMBIA("District of Columbia", "DC"), FEDERATED_STATES_OF_MICRONESIA(
            "Federated States of Micronesia", "FM"
        ),
        FLORIDA("Florida", "FL"), GEORGIA("Georgia", "GA"), GUAM("Guam", "GU"), HAWAII(
            "Hawaii", "HI"
        ),
        IDAHO("Idaho", "ID"), ILLINOIS("Illinois", "IL"), INDIANA("Indiana", "IN"), IOWA("Iowa", "IA"), KANSAS(
            "Kansas", "KS"
        ),
        KENTUCKY("Kentucky", "KY"), LOUISIANA("Louisiana", "LA"), MAINE("Maine", "ME"), MARYLAND("Maryland", "MD"), MARSHALL_ISLANDS(
            "Marshall Islands", "MH"
        ),
        MASSACHUSETTS("Massachusetts", "MA"), MICHIGAN("Michigan", "MI"), MINNESOTA("Minnesota", "MN"), MISSISSIPPI(
            "Mississippi", "MS"
        ),
        MISSOURI("Missouri", "MO"), MONTANA("Montana", "MT"), NEBRASKA("Nebraska", "NE"), NEVADA(
            "Nevada",
            "NV"
        ),
        NEW_HAMPSHIRE("New Hampshire", "NH"), NEW_JERSEY("New Jersey", "NJ"), NEW_MEXICO("New Mexico", "NM"), NEW_YORK(
            "New York", "NY"
        ),
        NORTH_CAROLINA("North Carolina", "NC"), NORTH_DAKOTA("North Dakota", "ND"), NORTHERN_MARIANA_ISLANDS(
            "Northern Mariana Islands", "MP"
        ),
        OHIO("Ohio", "OH"), OKLAHOMA("Oklahoma", "OK"), OREGON("Oregon", "OR"), PALAU(
            "Palau",
            "PW"
        ),
        PENNSYLVANIA("Pennsylvania", "PA"), PUERTO_RICO("Puerto Rico", "PR"), RHODE_ISLAND("Rhode Island", "RI"), SOUTH_CAROLINA(
            "South Carolina", "SC"
        ),
        SOUTH_DAKOTA("South Dakota", "SD"), TENNESSEE("Tennessee", "TN"), TEXAS("Texas", "TX"), UTAH(
            "Utah", "UT"
        ),
        VERMONT("Vermont", "VT"), VIRGIN_ISLANDS("Virgin Islands", "VI"), VIRGINIA("Virginia", "VA"), WASHINGTON(
            "Washington", "WA"
        ),
        WEST_VIRGINIA("West Virginia", "WV"), WISCONSIN("Wisconsin", "WI"), WYOMING("Wyoming", "WY"), UNKNOWN(
            "Unknown", ""
        );

        /**
         * Returns the state's abbreviation.
         *
         * @return the state's abbreviation.
         */

        companion object {
            /**
             * The set of states addressed by abbreviations.
             */
            private val STATES_BY_ABBR: MutableMap<String, State> = HashMap()

            fun valueOfState(name: String): State? {
                val enumName = name.toUpperCase().replace(" ".toRegex(), "_")
                return try {
                    valueOf(enumName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            /* static initializer */
            init {
                for (state in values()) {
                    STATES_BY_ABBR[state.iso] = state
                }
            }
        }

        override fun toString(): String {
            return name
        }


    }
}
