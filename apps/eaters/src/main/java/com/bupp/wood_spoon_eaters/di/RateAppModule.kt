package com.bupp.wood_spoon_eaters.di

import com.bupp.wood_spoon_eaters.data.data_sorce.memory.MemoryAppReviewDataSource
import com.bupp.wood_spoon_eaters.features.appreview.checker.EatersAvailabilityChecker
import com.bupp.wood_spoon_eaters.features.appreview.rule.Lived5StarReviewForOrderRule
import org.koin.dsl.module

val rateApp = module {

    // rules
    single { provideIsFirstRule(get()) }

    //checker
    single { provideEatersAvailabilityChecker(get()) }

}

//todo  3d orderRule

fun provideIsFirstRule(
    memoryAppReviewDataSource: MemoryAppReviewDataSource
) = Lived5StarReviewForOrderRule(
    params = Lived5StarReviewForOrderRule.HaveDoneAnOrderParam(
        lastLivedReview = memoryAppReviewDataSource.lastSelectedRatingFlow.value
    )
)

fun provideEatersAvailabilityChecker(
    livesFiveStarReviewRule: Lived5StarReviewForOrderRule
) = EatersAvailabilityChecker(
    rules = mutableListOf(
        livesFiveStarReviewRule
    )
)
