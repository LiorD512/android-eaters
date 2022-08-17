package com.bupp.wood_spoon_eaters.features.appreview.rule

interface RuleParam

abstract class AvailabilityRule<T>(
    open val params: RuleParam
) {
    abstract fun isAvailable(): Boolean
}
