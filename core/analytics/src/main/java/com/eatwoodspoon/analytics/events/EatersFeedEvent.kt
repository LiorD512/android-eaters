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
        public override val nullableParams: Map<String, Any?> = emptyMap()
    }

    public data class FeedHeroCampaignClickedEvent(
        public val campaign_id: Int?
    ) : EatersFeedEvent(name = "feed_hero_campaign_clicked") {
        public override val nullableParams: Map<String, Any?> = mapOf(
            "campaign_id" to campaign_id,
        )
    }

    public data class FeedDishItemClickedEvent(
        public val dish_id: Int
    ) : EatersFeedEvent(name = "feed_dish_item_clicked") {
        public override val nullableParams: Map<String, Any?> = mapOf(
            "dish_id" to dish_id,
        )
    }

    public data class FeedChefItemClickedEvent(
        public val chef_id: Int
    ) : EatersFeedEvent(name = "feed_chef_item_clicked") {
        public override val nullableParams: Map<String, Any?> = mapOf(
            "chef_id" to chef_id,
        )
    }
}
