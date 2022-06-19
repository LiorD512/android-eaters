package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.String
import kotlin.collections.Map

public sealed class EatersGiftEvent(
  public override val name: String,
  public open val order_id: String
) : AnalyticsEvent {
  public class ClickGiftInCheckoutEvent(
    public override val order_id: String
  ) : EatersGiftEvent(name = "click_gift_in_checkout", order_id = order_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public class ClickRecipientFirstNameEvent(
    public override val order_id: String
  ) : EatersGiftEvent(name = "click_recipient_first_name", order_id = order_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public class ClickRecipientLastNameEvent(
    public override val order_id: String
  ) : EatersGiftEvent(name = "click_recipient_last_name", order_id = order_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public class ClickRecipientPhoneNumberEvent(
    public override val order_id: String
  ) : EatersGiftEvent(name = "click_recipient_phone_number", order_id = order_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public class ClickAddDetailsInGiftScreenEvent(
    public override val order_id: String
  ) : EatersGiftEvent(name = "click_add_details_in_gift_screen", order_id = order_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public class ClearGiftClickedEvent(
    public override val order_id: String
  ) : EatersGiftEvent(name = "clear_gift_clicked", order_id = order_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }

  public class EditGiftClickedEvent(
    public override val order_id: String
  ) : EatersGiftEvent(name = "edit_gift_clicked", order_id = order_id) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "order_id" to order_id,
        )
  }
}
