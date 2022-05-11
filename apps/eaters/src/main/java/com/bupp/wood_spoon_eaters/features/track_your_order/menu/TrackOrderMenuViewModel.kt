package com.bupp.wood_spoon_eaters.features.track_your_order.menu

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository


class TrackOrderMenuViewModel(
    val api: ApiService,
    val eaterDataManager: EaterDataManager,
    private val paymentManager: PaymentManager,
    private val metaDataRepository: MetaDataRepository,
    private val flowEventsManager: FlowEventsManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) : ViewModel() {




}