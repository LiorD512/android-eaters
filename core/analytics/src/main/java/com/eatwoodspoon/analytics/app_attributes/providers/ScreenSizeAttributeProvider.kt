package com.eatwoodspoon.analytics.app_attributes.providers

import android.content.res.Resources
import android.util.Size
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider

internal class ScreenSizeAttributeProvider : AttributeProvider<Size> {
    override fun get() = Resources.getSystem().displayMetrics.let {
        Size(it.widthPixels, it.heightPixels)
    }
}
