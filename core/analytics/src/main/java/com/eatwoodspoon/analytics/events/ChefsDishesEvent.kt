package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.String
import kotlin.collections.Map

public sealed class ChefsDishesEvent(
  public override val name: String
) : AnalyticsEvent {
  public class MyDishesClickedEvent : ChefsDishesEvent(name = "dishes_my_dishes_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class AddDishClickedEvent : ChefsDishesEvent(name = "dishes_add_dish_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class EditDishClickedEvent(
    public val dish_id: String
  ) : ChefsDishesEvent(name = "dishes_edit_dish_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        )
  }

  public data class DuplicateDishClickedEvent(
    public val dish_id: String
  ) : ChefsDishesEvent(name = "dishes_duplicate_dish_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        )
  }

  public data class UnpublishDishClickedEvent(
    public val dish_id: String
  ) : ChefsDishesEvent(name = "dishes_unpublish_dish_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        )
  }

  public data class DeleteDishClickedEvent(
    public val dish_id: String
  ) : ChefsDishesEvent(name = "dishes_delete_dish_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        )
  }

  public data class SearchForDishClickedEvent(
    public val query: String
  ) : ChefsDishesEvent(name = "dishes_search_for_dish_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "query" to query,
        )
  }
}
