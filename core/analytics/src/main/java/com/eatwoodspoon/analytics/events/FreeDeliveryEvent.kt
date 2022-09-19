package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class FreeDeliveryEvent(
  public override val name: String,
  public open val order_id: Int,
  public open val screen: String
) : AnalyticsEvent {
  public class AddMoreItemsClickedEvent(
    public override val order_id: Int,
    public override val screen: String
  ) : FreeDeliveryEvent(name = "free_delivery_add_more_items_clicked", order_id = order_id, screen =
      screen) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        "screen" to screen,
        )
  }

  public class ThresholdAchievedEvent(
    public override val order_id: Int,
    public override val screen: String
  ) : FreeDeliveryEvent(name = "free_delivery_threshold_achieved", order_id = order_id, screen =
      screen) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        "screen" to screen,
        )
  }

  public class ViewClickedEvent(
    public override val order_id: Int,
    public override val screen: String
  ) : FreeDeliveryEvent(name = "free_delivery_view_clicked", order_id = order_id, screen = screen) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        "screen" to screen,
        )
  }
}
