package com.eatwoodspoon.analytics.events

import kotlin.Any
import kotlin.String
import kotlin.collections.Map

public interface AnalyticsEvent {
  public val name: String

  public val nullableParams: Map<String, Any?>

  public val params: Map<String, Any>
    get() = nullableParams.filterValues { it != null } as Map<String, Any>
}
