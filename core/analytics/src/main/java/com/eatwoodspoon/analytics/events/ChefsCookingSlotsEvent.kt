package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class ChefsCookingSlotsEvent(
  public override val name: String,
  public open val mode: ModeValues,
  public open val slot_id: Int?
) : AnalyticsEvent {
  public enum class ModeValues(
    private val value: String
  ) {
    Edit("Edit"),
    New("New"),
    ;

    public override fun toString(): String = value
  }

  public class CookingSlotOpenedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_cooking_slot_opened", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class CookingSlotFetchingErrorEvent(
    public val error_code: Int,
    public val error_description: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_cooking_slot_fetching_error", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class DetailsScreenOpenedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_details_screen_opened", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class MakeSlotRecurringClickedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_make_slot_recurring_clicked", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class MakeSlotReccuringSelectedEvent(
    public val rrule: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_make_slot_reccuring_selected", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "rrule" to rrule,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class SetOperatingHoursClickedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_set_operating_hours_clicked", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class SetOperatingHoursSelectedEvent(
    public val starts_at: String?,
    public val ends_at: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_set_operating_hours_selected", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "starts_at" to starts_at,
        "ends_at" to ends_at,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class SetLastCallClickedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_set_last_call_clicked", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class SetLastCallSelectedEvent(
    public val hours: Int,
    public val minutes: Int,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_set_last_call_selected", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "hours" to hours,
        "minutes" to minutes,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class DetailsScreenNextClickedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_details_screen_next_clicked", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class DetailsLocalValidationFailedEvent(
    public val errors: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_details_local_validation_failed", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "errors" to errors,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class DetailsServerValidationFailedEvent(
    public val errors: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_details_server_validation_failed", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "errors" to errors,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class MenuScreenOpenedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_menu_screen_opened", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class AddDishesClickedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_add_dishes_clicked", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class AddDishesAddedEvent(
    public val dish_ids: String,
    public val dish_count: Int,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_add_dishes_added", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_ids" to dish_ids,
        "dish_count" to dish_count,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class DeleteDishClickedEvent(
    public val dish_id: Int,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_delete_dish_clicked", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class DishQuantityChangedEvent(
    public val dish_id: Int,
    public val count: Int,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_dish_quantity_changed", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "dish_id" to dish_id,
        "count" to count,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class DishPickerShownEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_dish_picker_shown", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class CategoriesFilterShownEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_categories_filter_shown", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class MenuScreenNextClickedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_menu_screen_next_clicked", mode = mode, slot_id = slot_id)
      {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class MenuLocalValidationFailedEvent(
    public val errors: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_menu_local_validation_failed", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "errors" to errors,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class MenuServerValidationFailedEvent(
    public val errors: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_menu_server_validation_failed", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "errors" to errors,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class ReviewScreenOpenedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_review_screen_opened", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class ReviewScreenNextClickedEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_review_screen_next_clicked", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public class ReviewScreenUpdateDialogShownEvent(
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_review_screen_update_dialog_shown", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class ReviewScreenUpdateDialogResultEvent(
    public val detach: Boolean,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_review_screen_update_dialog_result", mode = mode, slot_id =
      slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "detach" to detach,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class ReviewScreenSaveErrorEvent(
    public val error_code: Int,
    public val error_description: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_review_screen_save_error", mode = mode, slot_id = slot_id)
      {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class RruleScreenShownEvent(
    public val rrule: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_rrule_screen_shown", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "rrule" to rrule,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }

  public data class RruleValueSelectedEvent(
    public val recurrency: RecurrencyValues,
    public val rrule: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_rrule_value_selected", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "recurrency" to recurrency,
        "rrule" to rrule,
        "mode" to mode,
        "slot_id" to slot_id,
        )

    public enum class RecurrencyValues(
      private val value: String
    ) {
      onetime("onetime"),
      dayly("dayly"),
      weekly("weekly"),
      custom("custom"),
      ;

      public override fun toString(): String = value
    }
  }

  public data class RruleUntilSelectedEvent(
    public val until: String,
    public val rrule: String?,
    public override val mode: ModeValues,
    public override val slot_id: Int?
  ) : ChefsCookingSlotsEvent(name = "rcs_rrule_until_selected", mode = mode, slot_id = slot_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "until" to until,
        "rrule" to rrule,
        "mode" to mode,
        "slot_id" to slot_id,
        )
  }
}
