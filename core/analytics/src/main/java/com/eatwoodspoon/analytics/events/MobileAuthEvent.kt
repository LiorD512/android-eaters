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

  public data class AuthErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileAuthEvent(name = "auth_auth_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }

  public data class EatersContactScreenOpenedEvent(
    public val source: String,
    public val has_email: Boolean,
    public val email_verified: Boolean,
    public val has_phone: Boolean,
    public val phone_verified: Boolean
  ) : MobileAuthEvent(name = "auth_eaters_contact_screen_opened") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "source" to source,
        "has_email" to has_email,
        "email_verified" to email_verified,
        "has_phone" to has_phone,
        "phone_verified" to phone_verified,
        )
  }

  public class EatersContactScreenSaveClickedEvent : MobileAuthEvent(name =
      "auth_eaters_contact_screen_save_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class EatersContactScreenLocalValidationErrorEvent(
    public val errors: String
  ) : MobileAuthEvent(name = "auth_eaters_contact_screen_local_validation_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "errors" to errors,
        )
  }

  public data class EatersContactScreenSaveSuccessEvent(
    public val phone_verification_required: Boolean
  ) : MobileAuthEvent(name = "auth_eaters_contact_screen_save_success") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "phone_verification_required" to phone_verification_required,
        )
  }

  public data class EatersContactScreenSaveErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileAuthEvent(name = "auth_eaters_contact_screen_save_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }

  public data class EatersRequestVerificationCodeEvent(
    public val is_retry: Boolean
  ) : MobileAuthEvent(name = "auth_eaters_request_verification_code") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "is_retry" to is_retry,
        )
  }

  public class EatersRequestVerificationCodeSuccessEvent : MobileAuthEvent(name =
      "auth_eaters_request_verification_code_success") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class EatersRequestVerificationCodeErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileAuthEvent(name = "auth_eaters_request_verification_code_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }

  public class EatersPhoneVerificationScreenOpenedEvent : MobileAuthEvent(name =
      "auth_eaters_phone_verification_screen_opened") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class EatersPhoneVerificationScreenValidateClickedEvent : MobileAuthEvent(name =
      "auth_eaters_phone_verification_screen_validate_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class EatersPhoneVerificationScreenValidateSuccessEvent : MobileAuthEvent(name =
      "auth_eaters_phone_verification_screen_validate_success") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class EatersPhoneVerificationScreenInvalidCodeEvent : MobileAuthEvent(name =
      "auth_eaters_phone_verification_screen_invalid_code") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class EatersPhoneVerificationScreenValidateErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileAuthEvent(name = "auth_eaters_phone_verification_screen_validate_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }

  public data class EatersAccountMergedEvent(
    public val from_user_id: String,
    public val to_user_id: String
  ) : MobileAuthEvent(name = "auth_eaters_account_merged") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "from_user_id" to from_user_id,
        "to_user_id" to to_user_id,
        )
  }
}
