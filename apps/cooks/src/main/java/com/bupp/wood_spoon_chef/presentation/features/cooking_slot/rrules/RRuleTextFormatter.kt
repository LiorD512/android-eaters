package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules

import biweekly.ICalVersion
import biweekly.io.ParseContext
import biweekly.io.TimezoneInfo
import biweekly.io.WriteContext
import biweekly.io.scribe.property.RecurrenceRuleScribe
import biweekly.parameter.ICalParameters
import biweekly.property.RecurrenceRule
import biweekly.util.Frequency
import biweekly.util.Recurrence
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.RecurringRule
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


data class SimpleRRule(
    val frequency: Frequency,
    val interval: Int,
    val until: Date?
)

class RRuleTextFormatter {

    private val scribe = RecurrenceRuleScribe()
    private val parseContext = ParseContext().apply {
        version = ICalVersion.V2_0
    }

    private val writeContext = WriteContext(ICalVersion.V2_0, TimezoneInfo(), null)
    private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    fun parseRRule(rruleString: String): SimpleRRule? {
        try {

            val rrule = scribe.parseText(rruleString, null, ICalParameters(), parseContext);
            val recurrence = rrule.value;

            return SimpleRRule(
                recurrence.frequency,
                recurrence.interval,
                recurrence.until ?: Date()
            )

        } catch (ex: RuntimeException) {
            Timber.e(ex)
            return null
        }
    }

    fun formatRRule(rruleString: String) = run {
        parseRRule(rruleString)?.let {
            formatRRule(it)
        }
    }

    fun formatRRule(simpleRRule: SimpleRRule): String? {
        try {
            val stringBuilder = StringBuilder()
            stringBuilder.append(
                "Repeats every ${simpleRRule.interval} ${
                    formatFrequency(
                        simpleRRule.frequency,
                        simpleRRule.interval
                    )
                }"
            )
            if (simpleRRule.until != null) {
                stringBuilder.append(" until ${dateFormat.format(Date(simpleRRule.until.time))}")
            }

            return stringBuilder.toString()
        } catch (ex: RuntimeException) {
            Timber.e(ex)
            return null
        }
    }

    private fun formatFrequency(frequency: Frequency, interval: Int): String {
        val word = when (frequency) {
            Frequency.SECONDLY -> "second"
            Frequency.MINUTELY -> "minute"
            Frequency.HOURLY -> "hour"
            Frequency.DAILY -> "day"
            Frequency.WEEKLY -> "week"
            Frequency.MONTHLY -> "month"
            Frequency.YEARLY -> "years"
        }
        val suffix = if (interval > 1) "s" else ""
        return word + suffix
    }

    fun buildRRule(frequency: Frequency, interval: Int, until: Date?): String? {
        return try {
            val recurrence = Recurrence.Builder(frequency).interval(interval).apply {
                until?.let {
                    until(until)
                }
            }.build()
            val rrule = RecurrenceRule(recurrence)
            scribe.writeText(rrule, writeContext)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }

    fun buildRRule(simpleRRule: SimpleRRule) =
        buildRRule(simpleRRule.frequency, simpleRRule.interval, simpleRRule.until)
}

fun formatRcs(recurringRule: RecurringRule?): String {
    if (recurringRule != null) {
        return "Cooking slot will occur ${recurringRule.frequency}, ${recurringRule.count} times"
    }
    return ""
}

fun formatRecurringRule(recurringRule: RecurringRule?): String? {
    if (recurringRule == null) {
        return null
    }
    return "FREQ=${recurringRule?.frequency};COUNT=${recurringRule?.count}".uppercase()

}