package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class MobileAuthEvent(
  public override val name: String
) : AnalyticsEvent {
  public data class LoginStartedEvent(
    public val source: String,
    public val has_guest_token: Boolean
  ) : MobileAuthEvent(name = "auth_login_started") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "source" to source,
        "has_guest_token" to has_guest_token,
        )
  }

  public data class LoginCallbackReceivedEvent(
    public val token_type: TokenTypeValues,
    public val duration_seconds: Float?
  ) : MobileAuthEvent(name = "auth_login_callback_received") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "token_type" to token_type,
        "duration_seconds" to duration_seconds,
        )

    public enum class TokenTypeValues(
      private val value: String
    ) {
      jwt("jwt"),
      opaque("opaque"),
      ;

      public override fun toString(): String = value
    }
  }

  public data class LogoutStartedEvent(
    public val source: String
  ) : MobileAuthEvent(name = "auth_logout_started") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "source" to source,
        )
  }

  public data class LogoutCallbackReceivedEvent(
    public val duration_seconds: Float?
  ) : MobileAuthEvent(name = "auth_logout_callback_received") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "duration_seconds" to duration_seconds,
        )
  }

  public data class ErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileAuthEvent(name = "auth_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }

  public data class AccountMergedEvent(
    public val from_user_id: String,
    public val to_user_id: String
  ) : MobileAuthEvent(name = "auth_account_merged") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "from_user_id" to from_user_id,
        "to_user_id" to to_user_id,
        )
  }
}
