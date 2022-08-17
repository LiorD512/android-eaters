package com.bupp.wood_spoon_eaters.features.appreview.checker

import com.bupp.wood_spoon_eaters.features.appreview.rule.AvailabilityRule
import com.bupp.wood_spoon_eaters.features.appreview.rule.RuleParam

class EatersAvailabilityChecker(
    private val rules: MutableList<AvailabilityRule<out RuleParam>>
) : AvailabilityChecker() {

    override fun declareRules(): MutableList<AvailabilityRule<out RuleParam>> = rules
}
