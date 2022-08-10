package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.formatters

import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.LastCallForOrderFormatter
import org.joda.time.DateTime
import org.junit.Test

class LastCallForOrderFormatterTest {

    private val suffix = " before your cooking slot ends"


    @Test
    fun check_hours_no_minutes() {

        val endDate = DateTime()

        val string = LastCallForOrderFormatter.formatLastCallForOrder(endDate.minusHours(2).millis, endDate.millis)
        assert(string == "2 hr$suffix")
    }

    @Test
    fun check_no_hours_has_minutes() {

        val endDate = DateTime()

        val string = LastCallForOrderFormatter.formatLastCallForOrder(endDate.minusMinutes(17).millis, endDate.millis)
        assert(string == "17 min$suffix")
    }

    @Test
    fun check_hours_and_minutes() {

        val endDate = DateTime()

        val string = LastCallForOrderFormatter.formatLastCallForOrder(endDate.minusHours(2).minusMinutes(5).millis, endDate.millis)
        assert(string == "2 hr and 5 min$suffix")
    }

}