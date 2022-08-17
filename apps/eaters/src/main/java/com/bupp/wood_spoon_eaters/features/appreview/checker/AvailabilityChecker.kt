package com.bupp.wood_spoon_eaters.features.appreview.checker

import com.bupp.wood_spoon_eaters.features.appreview.rule.AvailabilityRule
import com.bupp.wood_spoon_eaters.features.appreview.rule.RuleParam

abstract class AvailabilityChecker {

    private var availabilityList: List<AvailabilityRule<out RuleParam>> = mutableListOf()

    init {
        applyRules()
    }

    private fun applyRules() {
        (availabilityList as MutableList).addAll(declareRules())
    }

    abstract fun declareRules(): MutableList<AvailabilityRule<out RuleParam>>

    fun checkAll(): Boolean {
        return availabilityList
            .filter { it.isAvailable() }
            .toList().size == availabilityList.size
    }
}