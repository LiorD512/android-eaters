package com.bupp.wood_spoon_eaters.features.appreview.rule


/**
 * User lives 5 star review for chef after getting an order
 */
class Lived5StarReviewForOrderRule(
    override val params: HaveDoneAnOrderParam
) : AvailabilityRule<Lived5StarReviewForOrderRule.HaveDoneAnOrderParam>(params) {

    class HaveDoneAnOrderParam(
        val lastLivedReview: Int
    ) : RuleParam

    override fun isAvailable(): Boolean = params.lastLivedReview == 5
}