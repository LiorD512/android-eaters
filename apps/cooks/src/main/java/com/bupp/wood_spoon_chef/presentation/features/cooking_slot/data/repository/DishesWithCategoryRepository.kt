package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository

import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.local.DishesWithCategoryMemoryDataSource
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network.DishesWithCategoryApiService

class DishesWithCategoryRepository(
    private val apiService: DishesWithCategoryApiService,
    private val memoryDataSource: DishesWithCategoryMemoryDataSource
) {

    suspend fun getSectionsAndDishes(forceFetch: Boolean = false): Result<SectionWithDishes> {
        try {
            var result = if (!forceFetch) {
                memoryDataSource.getSectionsAndDishes()
            } else {
                null
            }
            if (result == null) {
                result = apiService.getSectionsAndDishes().data?.also { data ->
                    memoryDataSource.setSectionsAndDishes(data)
                }
            }
            return if (result != null) {
                Result.success(result)
            } else {
                Result.failure(
                    DishWithCategoryRepositoryException
                        ("We didn't get dishes with category data")
                )
            }
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    fun filterListByCategoryName(sectionList: List<FilterAdapterSectionModel>): SectionWithDishes? {
        val sectionName = sectionList.map { it.sectionName }
        return if (sectionName.isNotEmpty()) {
            val filteredSections = memoryDataSource.getSectionsAndDishes()?.sections?.filter { section ->
                sectionName.any{it.equals(section.title, true)}
            }
            SectionWithDishes(memoryDataSource.getSectionsAndDishes()?.dishes, filteredSections)
        } else {
            memoryDataSource.getSectionsAndDishes()
        }
    }

}

class DishWithCategoryRepositoryException(message: String?, cause: Throwable? = null) :
    RuntimeException(message, cause)