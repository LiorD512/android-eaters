package com.bupp.wood_spoon_chef.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import org.joda.time.DateTime

class MemoryCalendarDataSource {

     /**
      * selectedDateFlow - selected date on calendarView
      */
     val selectedDateFlow = MutableStateFlow(DateTime.now().millis)

}