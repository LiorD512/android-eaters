package com.bupp.wood_spoon_eaters.common.recyclerview_ext

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: SnapOnScrollListener.OnSnapPositionChangeListener?
) {
    onFlingListener = null
    snapHelper.attachToRecyclerView(this)
    onSnapPositionChangeListener?.let{
        val snapOnScrollListener = SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener)
        addOnScrollListener(snapOnScrollListener)
    }
}