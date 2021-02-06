//package com.bupp.wood_spoon_eaters.views.ws_range_time_picker
//
//import android.content.Context
//import mva2.adapter.ItemSection
//import mva2.adapter.MultiViewAdapter
//import java.util.*
//
//class WSRangeTimePickerMainAdapter(val context: Context) : MultiViewAdapter() {
//
//    private val dateSection = ItemSection<DateSectionModel>()
//    private val timeSection = ItemSection<TimeSectionModel>()
//
//    init {
//        this.registerItemBinders(WSRangeTimePickerDateBinder())
//
//        this.addSection(dateSection)
//        this.addSection(timeSection)
//
//    }
//
//
//    fun initDateSection(dates: List<Date>? = null) {
//        dates?.let{
//            dateSection.setItem(DateSectionModel(dates))
//        }
//    }
//
////    fun refreshSupportConversationsSection(supportConversations: List<SupportConversation>? = null) {
////        Timber.d("refreshMyConversationsSection")
////        supportConversations?.let{
////            supportConversationsSection.clear()
////            supportConversationsSection.addAll(supportConversations)
////            supportConversationsSection.showSection()
////        }
////    }
//
//
//
//
//}