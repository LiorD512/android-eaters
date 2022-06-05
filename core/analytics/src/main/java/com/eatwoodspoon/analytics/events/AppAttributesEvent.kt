package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public sealed class AppAttributesEvent(
  public override val name: String,
  public open val os: OsValues,
  public open val os_full: String,
  public open val os_version: String,
  public open val screen_height: Int,
  public open val screen_width: Int,
  public open val timezone: String,
  public open val device_type: DeviceTypeValues,
  public open val platform: PlatformValues,
  public open val device_locale: String,
  public open val device_language: String
) : AnalyticsEvent {
  public enum class OsValues(
    private val value: String
  ) {
    iOS("iOS"),
    Android("Android"),
    Linux("Linux"),
    Windows("Windows"),
    MacOS("MacOS"),
    Other("Other"),
    ;

    public override fun toString(): String = value
  }

  public enum class DeviceTypeValues(
    private val value: String
  ) {
    tablet("tablet"),
    phone("phone"),
    desktop("desktop"),
    ;

    public override fun toString(): String = value
  }

  public enum class PlatformValues(
    private val value: String
  ) {
    web("web"),
    native("native"),
    ;

    public override fun toString(): String = value
  }

  public data class MobileFeatureFlagAttributesEvent(
    public val app_build_number: Int,
    public val app_version_string: String,
    public val app_name: AppNameValues,
    public val brand: String,
    public val carrier: String?,
    public val manufacturer: String,
    public val model: String,
    public val device_id: String,
    public val radio: String?,
    public val wifi: Boolean?,
    public override val os: OsValues,
    public override val os_full: String,
    public override val os_version: String,
    public override val screen_height: Int,
    public override val screen_width: Int,
    public override val timezone: String,
    public override val device_type: DeviceTypeValues,
    public override val platform: PlatformValues,
    public override val device_locale: String,
    public override val device_language: String
  ) : AppAttributesEvent(name = "mobile_feature_flag_attributes", os = os, os_full = os_full,
      os_version = os_version, screen_height = screen_height, screen_width = screen_width, timezone
      = timezone, device_type = device_type, platform = platform, device_locale = device_locale,
      device_language = device_language) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "app_build_number" to app_build_number,
        "app_version_string" to app_version_string,
        "app_name" to app_name,
        "brand" to brand,
        "carrier" to carrier,
        "manufacturer" to manufacturer,
        "model" to model,
        "device_id" to device_id,
        "radio" to radio,
        "wifi" to wifi,
        "os" to os,
        "os_full" to os_full,
        "os_version" to os_version,
        "screen_height" to screen_height,
        "screen_width" to screen_width,
        "timezone" to timezone,
        "device_type" to device_type,
        "platform" to platform,
        "device_locale" to device_locale,
        "device_language" to device_language,
        )

    public enum class AppNameValues(
      private val value: String
    ) {
      Chefs("Chefs"),
      Diners("Diners"),
      ;

      public override fun toString(): String = value
    }
  }

  public data class WebFeatureFlagAttributesEvent(
    public val browser: String,
    public val browser_version: String,
    public override val os: OsValues,
    public override val os_full: String,
    public override val os_version: String,
    public override val screen_height: Int,
    public override val screen_width: Int,
    public override val timezone: String,
    public override val device_type: DeviceTypeValues,
    public override val platform: PlatformValues,
    public override val device_locale: String,
    public override val device_language: String
  ) : AppAttributesEvent(name = "web_feature_flag_attributes", os = os, os_full = os_full,
      os_version = os_version, screen_height = screen_height, screen_width = screen_width, timezone
      = timezone, device_type = device_type, platform = platform, device_locale = device_locale,
      device_language = device_language) {
    public override val nullableParams: Map<String, Any?> = mapOf(
        "browser" to browser,
        "browser_version" to browser_version,
        "os" to os,
        "os_full" to os_full,
        "os_version" to os_version,
        "screen_height" to screen_height,
        "screen_width" to screen_width,
        "timezone" to timezone,
        "device_type" to device_type,
        "platform" to platform,
        "device_locale" to device_locale,
        "device_language" to device_language,
        )
  }
}
