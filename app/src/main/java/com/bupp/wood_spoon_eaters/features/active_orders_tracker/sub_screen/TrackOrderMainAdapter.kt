package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders.*
import com.bupp.wood_spoon_eaters.model.Order
import mva2.adapter.ItemSection
import mva2.adapter.MultiViewAdapter
import mva2.adapter.util.Mode

class TrackOrderMainAdapter(
    val context: Context,
    fragManager: FragmentManager,
    bottomSectionListener: TrackOrderBottomBinder.TrackOrderBottomListener
) : MultiViewAdapter(), TrackOrderDetailsHeaderBinder.TrackOrderHeaderListener {


    private val TAG: String = "wowAdditionalMainAdApter"
    var isDetailsExpanded = true

    val mapSection = ItemSection<OrderTrackMapData>()
    val orderDetailHeadersSection = ItemSection<OrderTrackDetailsHeader>()
    val orderDetailsSection = ItemSection<OrderTrackDetails>()
    val progressSection = ItemSection<OrderTrackProgress>()
    val bottomSection = ItemSection<OrderTrackBottom>()

    init {
        this.registerItemBinders(TrackOrderMapBinder(fragManager), TrackOrderDetailsHeaderBinder(this), TrackOrderDetailsBinder(), TrackOrderProgressBinder(), TrackOrderBottomBinder(bottomSectionListener))

        this.addSection(mapSection)
        this.addSection(orderDetailHeadersSection)
        this.addSection(orderDetailsSection)
        this.addSection(progressSection)
        this.addSection(bottomSection)



//        hideAllSections()
        this.setSectionExpansionMode(Mode.SINGLE)
        orderDetailsSection.setSectionExpansionMode(Mode.MULTIPLE)



        bottomSection.setItem(OrderTrackBottom(arrayListOf()))
        bottomSection.showSection()

    }

    private fun hideAllSections() {
        mapSection.hideSection()
        orderDetailsSection.hideSection()
        progressSection.hideSection()
        bottomSection.hideSection()
    }

    override fun onHeaderClick(isExpanded: Boolean) {
        isDetailsExpanded = isExpanded
        when(isExpanded){
            false  -> orderDetailsSection.hideSection()
            true -> orderDetailsSection.showSection()
        }
    }

    fun updateUi(order: Order, userInfo: OrderUserInfo?) {
        //update map
        if(order.courier == null){
            mapSection.hideSection()
        }else{
            mapSection.setItem(OrderTrackMapData(order, userInfo))
            mapSection.showSection()
        }

        //details Header
        orderDetailHeadersSection.setItem(OrderTrackDetailsHeader(order.orderNumber))
        orderDetailHeadersSection.showSection()

        //Details:
        orderDetailsSection.setItem(OrderTrackDetails(order, userInfo))
        if(isDetailsExpanded){
            orderDetailsSection.showSection()
        }

        //Progress
        progressSection.setItem(OrderTrackProgress(order))
        progressSection.showSection()

    }


}