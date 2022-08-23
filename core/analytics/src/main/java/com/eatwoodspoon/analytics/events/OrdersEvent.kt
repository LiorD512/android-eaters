package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class OrdersEvent(
  public override val name: String
) : AnalyticsEvent {
  public data class OrderAgainClickedEvent(
    public val source: SourceValues,
    public val chef_id: Int,
    public val chef_name: String
  ) : OrdersEvent(name = "order_again_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "source" to source,
        "chef_id" to chef_id,
        "chef_name" to chef_name,
        )

    public enum class SourceValues(
      private val value: String
    ) {
      order_details("order_details"),
      other("other"),
      ;

      public override fun toString(): String = value
    }
  }
}
