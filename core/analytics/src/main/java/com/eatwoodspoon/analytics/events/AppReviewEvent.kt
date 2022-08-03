package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.String
import kotlin.collections.Map

public sealed class AppReviewEvent(
  public override val name: String
) : AnalyticsEvent {
  public data class EaterAppReviewRequestedEvent(
    public val trigger: TriggerValues
  ) : AppReviewEvent(name = "eater_app_review_requested") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "trigger" to trigger,
        )

    public enum class TriggerValues(
      private val value: String
    ) {
      five_star_review("five_star_review"),
      third_order_or_higher("third_order_or_higher"),
      sent_referral("sent_referral"),
      ;

      public override fun toString(): String = value
    }
  }

  public class ProfileScreenEaterRateAppClickedEvent : AppReviewEvent(name =
      "profile_screen_eater_rate_app_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }
}
