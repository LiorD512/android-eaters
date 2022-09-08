package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class UpsaleEvent(
  public override val name: String
) : AnalyticsEvent {
  public data class SwipeAddDishEvent(
    public val dish_id: Int,
    public val dish_name: String,
    public val dish_price: Float?,
    public val dish_tags: Boolean
  ) : UpsaleEvent(name = "upsale_swipe_add_dish") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        "dish_name" to dish_name,
        "dish_price" to dish_price,
        "dish_tags" to dish_tags,
        )
  }

  public data class SwipeRemoveDishEvent(
    public val dish_id: Int,
    public val dish_name: String,
    public val dish_price: Float?,
    public val dish_tags: Boolean
  ) : UpsaleEvent(name = "upsale_swipe_remove_dish") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        "dish_name" to dish_name,
        "dish_price" to dish_price,
        "dish_tags" to dish_tags,
        )
  }

  public class ClickProceedToCartEvent : UpsaleEvent(name = "upsale_click_proceed_to_cart") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class ClickedNoThanksEvent : UpsaleEvent(name = "upsale_clicked_no_thanks") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }
}
