package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class SupportCenterEvent(
  public override val name: String
) : AnalyticsEvent {
  public data class IntercomClickedEvent(
    public val source: SourceValues,
    public val order_id: Int?
  ) : SupportCenterEvent(name = "intercom_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "source" to source,
        "order_id" to order_id,
        )

    public enum class SourceValues(
      private val value: String
    ) {
      tabbar("tabbar"),
      checkout("checkout"),
      order_details("order_details"),
      track_order("track_order"),
      account("account"),
      ;

      public override fun toString(): String = value
    }
  }

  public data class ClickHelpInTrackOrderEvent(
    public val order_id: Int?
  ) : SupportCenterEvent(name = "click_help_in_track_order") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public class CallUsClickedInAccountEvent : SupportCenterEvent(name = "call_us_clicked_in_account")
      {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class HelpClickedInAccountEvent : SupportCenterEvent(name = "help_clicked_in_account") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class IntercomHelpCenterClickedEvent : SupportCenterEvent(name =
      "intercom_help_center_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class ContactSupportClickedInOrderDetailsEvent(
    public val order_id: Int?
  ) : SupportCenterEvent(name = "contact_support_clicked_in_order_details") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public data class CallSupportClickedInOrderDetailsEvent(
    public val order_id: Int?
  ) : SupportCenterEvent(name = "call_support_clicked_in_order_details") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }
}
