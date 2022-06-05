package com.eatwoodspoon.analytics.app_attributes

interface AttributeProvider<T> {
    fun get(): T
}
