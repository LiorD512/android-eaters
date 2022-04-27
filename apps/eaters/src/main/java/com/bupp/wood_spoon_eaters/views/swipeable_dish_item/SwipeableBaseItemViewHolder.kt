package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeableBaseItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract val isSwipeable: Boolean
    }