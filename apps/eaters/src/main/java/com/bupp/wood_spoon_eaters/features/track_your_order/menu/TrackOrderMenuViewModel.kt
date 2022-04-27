package com.bupp.wood_spoon_eaters.features.track_your_order.menu

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderUserInfo
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng


class TrackOrderMenuViewModel(
    val api: ApiService,
    val eaterDataManager: EaterDataManager,
    private val paymentManager: PaymentManager,
    private val metaDataRepository: MetaDataRepository,
    private val flowEventsManager: FlowEventsManager,
    private val eventsManager: EventsManager
) : ViewModel() {




}