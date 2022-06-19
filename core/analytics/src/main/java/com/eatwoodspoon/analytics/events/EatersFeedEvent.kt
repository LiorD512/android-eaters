package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class EatersFeedEvent(
  public override val name: String
) : AnalyticsEvent {
  public data class FeedHeroItemClickedEvent(
    public val hero_id: Int?
  ) : EatersFeedEvent(name = "feed_hero_item_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "hero_id" to hero_id,
        )
  }

  public class SwipeBetweenDishesFeedEvent : EatersFeedEvent(name = "swipe_between_dishes_feed") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }
}
