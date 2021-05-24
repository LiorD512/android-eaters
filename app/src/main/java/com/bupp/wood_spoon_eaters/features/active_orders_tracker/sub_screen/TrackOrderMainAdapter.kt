//package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen
//
//import android.content.Context
//import androidx.fragment.app.FragmentManager
//import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders.*
//import com.bupp.wood_spoon_eaters.model.Order
//import mva2.adapter.ItemSection
//import mva2.adapter.MultiViewAdapter
//import mva2.adapter.util.Mode
//
//class TrackOrderMainAdapter(
//    val context: Context,
//    fragManager: FragmentManager,
//    progressSectionListener: TrackOrderProgressBinder.TrackOrderProgressListener
//) : MultiViewAdapter(), TrackOrderDetailsHeaderBinder.TrackOrderHeaderListener {
//
//
//    var isDetailsExpanded = false
//
//    val orderDetailHeadersSection = ItemSection<OrderTrackHeader>()
//    val orderDetailsSection = ItemSection<OrderTrackDetails>()
//    val progressSection = ItemSection<OrderTrackProgress>()
//
//    init {
//        this.registerItemBinders(TrackOrderMapBinder(fragManager), TrackOrderDetailsHeaderBinder(this), TrackOrderDetailsBinder(), TrackOrderProgressBinder(progressSectionListener))
//
//        this.addSection(orderDetailHeadersSection)
//        this.addSection(orderDetailsSection)
//        this.addSection(progressSection)
//
//        this.setSectionExpansionMode(Mode.SINGLE)
//        orderDetailsSection.setSectionExpansionMode(Mode.MULTIPLE)
//        orderDetailsSection.hideSection()
//
//    }
//
//    private fun hideAllSections() {
////        mapSection.hideSection()
//        progressSection.hideSection()
//    }
//
//    override fun onHeaderClick(isExpanded: Boolean) {
//        isDetailsExpanded = isExpanded
//        when(isExpanded){
//            false  -> orderDetailsSection.hideSection()
//            true -> orderDetailsSection.showSection()
//        }
//    }
//
//    fun updateUi(order: Order, userInfo: OrderUserInfo?) {
//        //map section
////        mapSection.setItem(OrderTrackMapData(order, userInfo))
////        mapSection.showSection()
//
//        //details Header
//        orderDetailHeadersSection.setItem(OrderTrackHeader(order.orderNumber))
//        orderDetailHeadersSection.showSection()
//
//        //Details:
//        orderDetailsSection.setItem(OrderTrackDetails(order, userInfo))
//        if(isDetailsExpanded){
//            orderDetailsSection.showSection()
//        }
//
//        //Progress
//        progressSection.setItem(OrderTrackProgress(order))
//        progressSection.showSection()
//
//    }
//
//
//}