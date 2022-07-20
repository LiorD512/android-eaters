package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.local

import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DishesWithCategoryMemoryDataSource {

    private val sectionWithDishesMutex = Mutex()
    private var sectionWithDishes: SectionWithDishes? = null

    fun getSectionsAndDishes(): SectionWithDishes? {
        return sectionWithDishes
    }

    suspend fun setSectionsAndDishes(value: SectionWithDishes) {
        sectionWithDishesMutex.withLock {
            this.sectionWithDishes = value
        }
    }
}