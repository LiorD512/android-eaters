package com.bupp.wood_spoon_chef.data.local

import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import kotlinx.coroutines.flow.MutableStateFlow

class MemoryCategoriesWithDishDataSource {

    /**
     * sectionWithDishes - is fetched from API and stored in memory as a single source of truth.
     */
    val sectionWithDishes = MutableStateFlow<SectionWithDishes?>(null)

}