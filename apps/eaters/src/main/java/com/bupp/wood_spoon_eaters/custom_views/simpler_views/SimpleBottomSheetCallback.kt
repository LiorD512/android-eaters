package com.bupp.wood_spoon_eaters.custom_views.simpler_views

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class SimpleBottomSheetCallback: BottomSheetBehavior.BottomSheetCallback() {

    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

    override fun onStateChanged(bottomSheet: View, newState: Int) {}
}