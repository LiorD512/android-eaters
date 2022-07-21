package com.bupp.wood_spoon_chef.domain

import com.bupp.wood_spoon_chef.data.remote.model.*
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import kotlinx.coroutines.flow.*

class CookingSlotWithCategoriesInteractor(
    private val fetchCookingSlotByIdUseCase: FetchCookingSlotByIdUseCase,
    private val getSectionsWithDishesUseCase: GetSectionsWithDishesUseCase
) {

    fun getCookingSlotWithCategories(cookingSlotId: Long): Flow<ResponseResult<CookingSlot>> {
        val paramsSlot = FetchCookingSlotByIdUseCase.Params(cookingSlotId = cookingSlotId)
        val paramsSection = GetSectionsWithDishesUseCase.Params(isForceLoad = false)

        val slotWithCategories: Flow<ResponseResult<CookingSlot>> =
            getSectionsWithDishesUseCase.execute(paramsSection)
                .combineTransform(fetchCookingSlotByIdUseCase.execute(paramsSlot)) { responseSection, responseSlot ->
                    val section: SectionWithDishes? = (responseSection as? ResponseSuccess)?.data

                    when (responseSlot) {
                        is ResponseSuccess -> {
                            responseSlot.data?.let { slot ->
                                section?.let { section ->
                                    emit(
                                        ResponseSuccess(
                                            extendSlotWithSections(
                                                extendableSlot = slot,
                                                sections = section
                                            )
                                        )
                                    )
                                }
                            }
                        }
                        is ResponseError -> {
                            emit(responseSlot)
                        }
                    }
                }

        return slotWithCategories
    }

    private fun extendSlotWithSections(
        extendableSlot: CookingSlot,
        sections: SectionWithDishes
    ): CookingSlot {
        val toList: List<MenuItem> = extendableSlot.menuItems.map { menuItem ->
            sections.sections?.forEach { section ->
                section.dishIds?.contains(menuItem.dish.id)?.let { isContains ->
                    if (isContains) {
                        menuItem.dish.category = DishCategory(
                            id = -1L,
                            name = section.title ?: ""
                        )
                    }
                }
            }
            menuItem
        }.toList()

        return extendableSlot.copy(
            menuItems = toList
        )
    }
}