package com.bupp.wood_spoon_eaters.data.data_sorce.memory

import kotlinx.coroutines.flow.MutableStateFlow

class MemoryAppReviewDataSource {

    /**
     * lastSelectedRatingFlow - container for rating that user selected for order.
     * This value updated each time when order is done.
     */
    val lastSelectedRatingFlow = MutableStateFlow(-1)
}