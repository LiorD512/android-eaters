package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class MobileContactDetailsEvent(
  public override val name: String
) : AnalyticsEvent {
  public data class ScreenOpenedEvent(
    public val source: String,
    public val has_email: Boolean,
    public val email_verified: Boolean,
    public val has_phone: Boolean,
    public val phone_verified: Boolean
  ) : MobileContactDetailsEvent(name = "contact_details_screen_opened") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "source" to source,
        "has_email" to has_email,
        "email_verified" to email_verified,
        "has_phone" to has_phone,
        "phone_verified" to phone_verified,
        )
  }

  public class SaveClickedEvent : MobileContactDetailsEvent(name = "contact_details_save_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class LocalValidationErrorEvent(
    public val errors: String
  ) : MobileContactDetailsEvent(name = "contact_details_local_validation_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "errors" to errors,
        )
  }

  public data class SaveSuccessEvent(
    public val phone_verification_required: Boolean
  ) : MobileContactDetailsEvent(name = "contact_details_save_success") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "phone_verification_required" to phone_verification_required,
        )
  }

  public data class SaveErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileContactDetailsEvent(name = "contact_details_save_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }

  public data class RequestVerificationCodeEvent(
    public val is_retry: Boolean
  ) : MobileContactDetailsEvent(name = "contact_details_request_verification_code") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "is_retry" to is_retry,
        )
  }

  public class RequestVerificationCodeSuccessEvent : MobileContactDetailsEvent(name =
      "contact_details_request_verification_code_success") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class RequestVerificationCodeErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileContactDetailsEvent(name = "contact_details_request_verification_code_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }

  public class OtpScreenOpenedEvent : MobileContactDetailsEvent(name =
      "contact_details_otp_screen_opened") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class OtpScreenValidateClickedEvent : MobileContactDetailsEvent(name =
      "contact_details_otp_screen_validate_clicked") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class OtpScreenValidateSuccessEvent : MobileContactDetailsEvent(name =
      "contact_details_otp_screen_validate_success") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public class OtpScreenInvalidCodeEvent : MobileContactDetailsEvent(name =
      "contact_details_otp_screen_invalid_code") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        )
  }

  public data class OtpScreenValidateErrorEvent(
    public val error_code: Int,
    public val error_description: String?
  ) : MobileContactDetailsEvent(name = "contact_details_otp_screen_validate_error") {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "error_code" to error_code,
        "error_description" to error_description,
        )
  }
}
